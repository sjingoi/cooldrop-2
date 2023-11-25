import type { PeerConnection } from "./peerconnection";

export enum ConnectionType {
    LOCAL = "local",
    REMOTE = "remote",
}

export class Peer {

    private name: string | null;
    private uuid: string;
    public connection: PeerConnection;
    
    constructor(name: string | null, uuid: string, connection: PeerConnection) {

        this.name = name;
        this.uuid = uuid;
        this.connection = connection;

    }

    public getUUID() {
        return this.uuid;
    }

    public getName() {
        return this.name;
    }
    
}