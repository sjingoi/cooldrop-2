"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Logger_1 = require("./Logger");
const Types_1 = require("./Types");
class ClientConnectionManager {
    constructor() {
        this.client_list = [];
    }
    static getInstance() {
        return ClientConnectionManager.instance || (ClientConnectionManager.instance = new ClientConnectionManager());
    }
    registerClient(new_client) {
        Logger_1.default.getIns().logVerbose("Added Client " + new_client.getSessionUUID() + " to array.");
        let new_client_info = {
            peer_name: new_client.getName(),
            peer_uuid: new_client.getSessionUUID(),
        };
        new_client.addMessageListener(Types_1.MessageType.SDP_OFFER, (data) => this.forwardSDP(JSON.parse(data), new_client, Types_1.MessageType.SDP_OFFER));
        new_client.addMessageListener(Types_1.MessageType.SDP_ANSWER, (data) => this.forwardSDP(JSON.parse(data), new_client, Types_1.MessageType.SDP_ANSWER));
        for (let client of this.client_list)
            client.send(Types_1.MessageType.SDP_OFFER_REQ, JSON.stringify(new_client_info));
        this.client_list.push(new_client);
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
        Logger_1.default.getIns().logVerbose("Forwarding SDP");
        if (sdp.origin_uuid != origin_client.getSessionUUID()) {
            Logger_1.default.getIns().logError("UUID Mismatch: " + sdp.origin_uuid + " vs " + origin_client.getSessionUUID());
            return;
        }
        let recipient = this.getClient(sdp.recipient_uuid);
        if (recipient) {
            recipient.send(sdp_type, JSON.stringify(sdp));
        }
        else {
            Logger_1.default.getIns().logError("Could not find recipient " + sdp.recipient_uuid);
        }
    }
}
exports.default = ClientConnectionManager;
