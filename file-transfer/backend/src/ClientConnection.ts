import { ServerMessage, MessageType, SDP } from "./ServerMessage";
import { Connection } from "./Connection"
import WebSocket = require("ws");
const { v4: uuidv4 } = require('uuid');

class ClientConnection extends Connection {
    
    private private_uuid: string;
    private session_uuid: string;
    private name: string;
    
    public constructor(socket: WebSocket, uuid: string, name: string) {
        super(socket);
        this.private_uuid = uuid;
        this.name = name;
        this.session_uuid = uuidv4();
        // this.registerSocketHandlers();
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