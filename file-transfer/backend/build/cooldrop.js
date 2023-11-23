"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const ClientConnection_1 = require("./ClientConnection");
const ClientConnectionManager_1 = require("./ClientConnectionManager");
const WebSocket = require("ws");
const ServerMessage_1 = require("./ServerMessage");
const Connection_1 = require("./Connection");
const Logger_1 = require("./Logger");
const { v4: uuidv4 } = require('uuid');
const server = new WebSocket.Server({ port: 8080 });
server.on('connection', (cws) => {
    Logger_1.default.getIns().logVerbose("Recieved new connection.");
    let connection = new Connection_1.Connection(cws);
    connection.addMessageListener(ServerMessage_1.MessageType.PRIVATE_UUID, (client_info) => {
        let info = JSON.parse(client_info);
        let manager_instnace = ClientConnectionManager_1.default.getInstance();
        let client = new ClientConnection_1.default(cws, info.peer_uuid, info.peer_name);
        manager_instnace.registerClient(client);
        Logger_1.default.getIns().logInfo("Client " + client.getSessionUUID() + " joined.");
        client.addCloseListener(() => {
            manager_instnace.unRegisterClient(client);
            Logger_1.default.getIns().logInfo("Client " + client.getSessionUUID() + " left.");
        });
        client.addMessageListener(ServerMessage_1.MessageType.TEST, (data) => console.log("Recieved test message: " + data));
        client.send(ServerMessage_1.MessageType.PUBLIC_UUID, client.getSessionUUID());
    });
    connection.send(ServerMessage_1.MessageType.PRIVATE_UUID_REQ, "");
});
