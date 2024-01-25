import ClientConnection from "./ClientConnection";
import ClientConnectionManager from "./ClientConnectionManager";
import WebSocket = require("ws");
import { ServerMessage, MessageType, SDP, PeerInfo } from "./Types";
import { Connection } from "./Connection";
import Logger from "./Logger";
import * as express from 'express';
const { v4: uuidv4 } = require('uuid');

// const server = new WebSocket.Server({port: 8080});

const app = express();
const server = require('http').Server(app);
app.use(express.static("../client-vite/dist"));

server.listen(8080, "0.0.0.0");

// server.on('connection', (cws: WebSocket) => {

//     Logger.getIns().logVerbose("Recieved new connection.");

//     let connection: Connection = new Connection(cws);
//     connection.addMessageListener(MessageType.PRIVATE_UUID, (client_info) => {
//         let info: PeerInfo = JSON.parse(client_info);
//         let manager_instnace = ClientConnectionManager.getInstance();

//         let client: ClientConnection = new ClientConnection(cws, info.peer_uuid, info.peer_name);
//         manager_instnace.registerClient(client);
//         Logger.getIns().logInfo("Client " + client.getSessionUUID() + " joined.");


//         client.addCloseListener(() => {
//             manager_instnace.unRegisterClient(client);
//             Logger.getIns().logInfo("Client " + client.getSessionUUID() + " left.");
//         });
//         client.addMessageListener(MessageType.TEST, (data) => console.log("Recieved test message: " + data));
//         client.send(MessageType.PUBLIC_UUID, client.getSessionUUID());

//     })

//     connection.send(MessageType.PRIVATE_UUID_REQ, "");
// })