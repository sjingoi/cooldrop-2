import type { PeerConnection } from "./peerconnection";

export enum ConnectionType {
    LOCAL = "local",
    REMOTE = "remote",
}

export class Peer extends EventTarget {

    public name: string | null;
    public uuid: string;
    
    constructor(uuid: string, name: string | null) {
        super();
        this.name = name;
        this.uuid = uuid;

    }

    public getUUID() {
        return this.uuid;
    }

    public getName() {
        return this.name;
    }

    public setName(name: string) {
        this.name = name;
    }
    
}