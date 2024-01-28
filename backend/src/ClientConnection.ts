import { ServerMessage, MessageType, SDP } from "./Types";
// import { Connection } from "./Connection"
import WebSocket = require("ws");
import { type Socket } from "socket.io"
const { v4: uuidv4 } = require('uuid');

class ClientConnection {
    
    public socket: Socket;
    private private_uuid: string;
    private session_uuid: string;
    private name: string;
    
    public constructor(socket: Socket, uuid: string, name: string) {
        this.socket = socket;
        this.private_uuid = uuid;
        this.name = name;
        this.session_uuid = uuidv4();
        // this.registerSocketHandlers();
    }

    public send(message_type: MessageType, data: string) {
        this.socket.emit(message_type, data);
    }

    public getSessionUUID() {
        return this.session_uuid;
    }

    public getName() {
        return this.name;
    }

    public getPrivateUUID() {
        return this.private_uuid;
    }

}

export default ClientConnection;