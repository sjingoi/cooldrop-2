import ClientConnection from "./ClientConnection";
import ClientConnectionManager from "./ClientConnectionManager";
import WebSocket = require("ws");
import { ServerMessage, MessageType, SDP, PeerInfo } from "./Types";
import Logger from "./Logger";
import * as express from 'express';
import * as http from 'http';
import { Server as SocketIOServer } from 'socket.io';
const { v4: uuidv4 } = require('uuid');

// const server = new WebSocket.Server({port: 8080});

const app = express();
const server = http.createServer(app);

const io = new SocketIOServer(server, {
    cors: { origin: "*" }
})




const client_manager: ClientConnectionManager = new ClientConnectionManager();

io.on('connection', (cws) => {
    Logger.getIns().logVerbose("Recieved new connection.");

    cws.on(MessageType.PRIVATE_UUID, (client_info) => {
        let info: PeerInfo = JSON.parse(client_info);

        let client: ClientConnection = new ClientConnection(cws, info.peer_uuid, info.peer_name);
        client_manager.registerClient(client);
        Logger.getIns().logInfo("Client " + client.getSessionUUID() + " joined.");

        client.socket.on("disconnect", () => {
            client_manager.unRegisterClient(client);
            Logger.getIns().logInfo("Client " + client.getSessionUUID() + " left.");
        })

        client.socket.on(MessageType.TEST, (data) => console.log("Recieved test message: " + data));
        client.send(MessageType.PUBLIC_UUID, client.getSessionUUID());

    })

    cws.emit(MessageType.PRIVATE_UUID_REQ, "");
})

app.use(express.static("../client-vite/dist"));

server.listen(8080, "0.0.0.0");