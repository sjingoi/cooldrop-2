import ClientConnection from "./ClientConnection";
import Logger from "./Logger";
import { MessageType, PeerInfo, SDP } from "./ServerMessage";

class ClientConnectionManager {
    
    private constructor() {
        this.client_list = [];
    }
    
    private static instance: ClientConnectionManager;
    
    private client_list: ClientConnection[];
    
    public static getInstance() {
        return ClientConnectionManager.instance || (ClientConnectionManager.instance = new ClientConnectionManager());
    }

    public registerClient(new_client: ClientConnection) {
        let new_client_info: PeerInfo = {
            peer_name: new_client.getName(),
            peer_uuid: new_client.getSessionUUID(),
        }
        for (let client of this.client_list) client.send(MessageType.SDP_OFFER_REQ, JSON.stringify(new_client_info));

        this.client_list.push(new_client);
        
        new_client.addMessageListener(MessageType.SDP_OFFER, (data) => this.forwardSDP(JSON.parse(data), new_client, MessageType.SDP_OFFER))
        new_client.addMessageListener(MessageType.SDP_ANSWER, (data) => this.forwardSDP(JSON.parse(data), new_client, MessageType.SDP_ANSWER))
        
    }

    public unRegisterClient(client: ClientConnection) {
        this.client_list.splice(this.client_list.findIndex(other_client => other_client == client), 1);
        Logger.getIns().logVerbose("Removed Client " + client.getSessionUUID() + " from array.")
    }

    public getClient(uuid: string): ClientConnection | undefined {
        const predicate = (client: ClientConnection): Boolean => {
            return client.getSessionUUID() === uuid;
        }
        return this.client_list.find(predicate);
    }

    private forwardSDP(sdp: SDP, origin_client: ClientConnection, sdp_type: MessageType) {
        if (sdp.origin_uuid != origin_client.getSessionUUID()) {
            Logger.getIns().logError("UUID Mismatch: " + sdp.origin_uuid + " vs " + origin_client.getSessionUUID());
            return;
        }

        let recipient = this.getClient(sdp.recipient_uuid);
        if (recipient) {
            recipient.send(MessageType.SDP_OFFER, JSON.stringify(sdp));
        }
    }
}

export default ClientConnectionManager;