import Logger from "./Logger";
import { ServerMessage, MessageType, SDP } from "./ServerMessage";
import WebSocket = require("ws");

export class Connection {

    protected socket: WebSocket;
    private listeners: Set<[MessageType, (data: string) => any]> = new Set();

    public constructor(socket: WebSocket) {
        this.socket = socket;
        this.socket.addEventListener("message", (event) => {
            let message: ServerMessage = JSON.parse(event.data.toString());
            this.notifyListeners(message);
        })
    }

    private notifyListeners(message: ServerMessage) {
        Logger.getIns().logInfo("Recieved message " + message.type);
        this.listeners.forEach( (pair) => {
            const type: MessageType = pair[0];
            const x: (data: string) => any = pair[1];
            if (type === message.type) {
                x(message.data);
            }
        })
    }

    public addMessageListener(type: MessageType, listener: (data: string) => any) {
        this.listeners.add([type, listener]);
    }

    public addOpenListener(listener: () => any) {
        this.socket.addEventListener("open", listener);
    }

    public addCloseListener(listener: () => any) {
        this.socket.addEventListener("close", listener);
    }

    public send(type: MessageType, data: string) {
        let message: ServerMessage = {
            type: type,
            data: data,
        }
        this.socket?.send(JSON.stringify(message));
        Logger.getIns().logInfo("Sending " + type + ": " + data)
    }

    
}