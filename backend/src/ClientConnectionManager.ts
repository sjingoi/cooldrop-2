import ClientConnection from "./ClientConnection";
import Logger from "./Logger";
import { IceCandidate, MessageType, PeerInfo, RTCMessageType as RTCMessageType, RTCType, SDP } from "./Types";

class ClientConnectionManager {

    protected client_list: ClientConnection[];
    
    constructor() {
        this.client_list = [];
    }

    public registerClient(new_client: ClientConnection) {
        Logger.getIns().logVerbose("Added Client " + new_client.getSessionUUID() + " to array.")
        let new_client_info: PeerInfo = {
            peer_name: new_client.getName(),
            peer_uuid: new_client.getSessionUUID(),
        }

        new_client.socket.on(MessageType.SDP_OFFER, (data) => this.forwardMessage(JSON.parse(data), new_client, MessageType.SDP_OFFER));
        new_client.socket.on(MessageType.SDP_ANSWER, (data) => this.forwardMessage(JSON.parse(data), new_client, MessageType.SDP_ANSWER));
        new_client.socket.on(MessageType.ICE_CANDIDATE, (data) => this.forwardMessage(JSON.parse(data), new_client, MessageType.ICE_CANDIDATE));
        

        // for (let client of this.client_list) client.send(MessageType.SDP_OFFER_REQ, JSON.stringify(new_client_info));

        this.client_list.forEach((client) => {client.send(MessageType.SDP_OFFER_REQ, JSON.stringify(new_client_info))})

        this.client_list.push(new_client);
    }

    public unRegisterClient(client: ClientConnection) {
        this.client_list.splice(this.client_list.findIndex(other_client => other_client == client), 1);
        this.client_list.forEach((c) => c.send(MessageType.PEER_DISCONNECT, client.getSessionUUID()));
        Logger.getIns().logVerbose("Removed Client " + client.getSessionUUID() + " from array.")
    }

    public getClient(uuid: string): ClientConnection | undefined {
        const predicate = (client: ClientConnection): Boolean => {
            return client.getSessionUUID() === uuid;
        }
        return this.client_list.find(predicate);
    }

    private forwardMessage(data: RTCType, origin_client: ClientConnection, message_type: RTCMessageType) {
        Logger.getIns().logVerbose("Forwarding " + message_type);
        if (data.origin_uuid != origin_client.getSessionUUID()) {
            Logger.getIns().logError("UUID Mismatch: " + data.origin_uuid + " vs " + origin_client.getSessionUUID());
            return;
        }

        let recipient = this.getClient(data.recipient_uuid);
        if (recipient) {
            recipient.send(message_type, JSON.stringify(data));
        } else {
            Logger.getIns().logError("Could not find recipient " + data.recipient_uuid);
        }
    }
}

export default ClientConnectionManager;