export interface ServerMessage {
    type: ServerMessageType;
    data: string;
}

export enum ServerMessageType {
    TEST = "test",
    PRIVATE_UUID = "private-uuid",
    PUBLIC_UUID = "public-uuid",
    PRIVATE_UUID_REQ = "private-uuid-req",
    SDP_OFFER = "sdp-offer",
    SDP_ANSWER = "sdp-answer",
    SDP_OFFER_REQ = "sdp-offer-req",
    ICE_CANDIDATE = "ice-candidate",
}

export class ServerConnection {

    public socket: WebSocket | null = null;
    private listeners: Set<[ServerMessageType, (data: string) => any]> = new Set();

    public constructor(url: string) {
        this.socket = new WebSocket("ws://" + url);
        this.socket.addEventListener("message", (event) => {
            let message: ServerMessage = JSON.parse(event.data.toString());
            console.log("Recieved " + message.type);
            this.notifyListeners(message);
        })
    }

    private notifyListeners(message: ServerMessage) {
        this.listeners.forEach( (pair) => {
            const type: ServerMessageType = pair[0];
            const x: (data: string) => any = pair[1];
            if (type === message.type) {
                x(message.data);
            }
        })
    }

    public addMessageListener(type: ServerMessageType, listener: (data: string) => any) {
        this.listeners.add([type, listener]);
    }

    public send(type: ServerMessageType, data: string) {
        console.log("Sending " + type);
        let message: ServerMessage = {
            type: type,
            data: data,
        }
        this.socket?.send(JSON.stringify(message));
    }

    public close() {
        this.socket?.close();
    }

}
