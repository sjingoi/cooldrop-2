import { LocalPeerConnection, PeerConnection } from "./peerconnection";

export enum ConnectionType {
    LOCAL = "local",
    REMOTE = "remote",
}

export class Peer {
    private name;
    private uuid;


    public connection: PeerConnection;
    
    constructor(name: string, uuid: string) {

        this.name = name;
        this.uuid = uuid;

        this.connection = new LocalPeerConnection();

    }
    
}