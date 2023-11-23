"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Logger_1 = require("./Logger");
const ServerMessage_1 = require("./ServerMessage");
class ClientConnectionManager {
    constructor() {
        this.client_list = [];
    }
    static getInstance() {
        return ClientConnectionManager.instance || (ClientConnectionManager.instance = new ClientConnectionManager());
    }
    registerClient(new_client) {
        let new_client_info = {
            peer_name: new_client.getName(),
            peer_uuid: new_client.getSessionUUID(),
        };
        for (let client of this.client_list)
            client.send(ServerMessage_1.MessageType.SDP_OFFER_REQ, JSON.stringify(new_client_info));
        this.client_list.push(new_client);
        new_client.addMessageListener(ServerMessage_1.MessageType.SDP_OFFER, (data) => this.forwardSDP(JSON.parse(data), new_client, ServerMessage_1.MessageType.SDP_OFFER));
        new_client.addMessageListener(ServerMessage_1.MessageType.SDP_ANSWER, (data) => this.forwardSDP(JSON.parse(data), new_client, ServerMessage_1.MessageType.SDP_ANSWER));
    }
    unRegisterClient(client) {
        this.client_list.splice(this.client_list.findIndex(other_client => other_client == client), 1);
        Logger_1.default.getIns().logVerbose("Removed Client " + client.getSessionUUID() + " from array.");
    }
    getClient(uuid) {
        const predicate = (client) => {
            return client.getSessionUUID() === uuid;
        };
        return this.client_list.find(predicate);
    }
    forwardSDP(sdp, origin_client, sdp_type) {
        if (sdp.origin_uuid != origin_client.getSessionUUID()) {
            Logger_1.default.getIns().logError("UUID Mismatch: " + sdp.origin_uuid + " vs " + origin_client.getSessionUUID());
            return;
        }
        let recipient = this.getClient(sdp.recipient_uuid);
        if (recipient) {
            recipient.send(ServerMessage_1.MessageType.SDP_OFFER, JSON.stringify(sdp));
        }
    }
}
exports.default = ClientConnectionManager;
