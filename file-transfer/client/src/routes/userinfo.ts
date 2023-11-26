import type { Peer } from "./peer";
import type { PeerConnection } from "./peerconnection";

export class UserInfo {
    public static private_uuid: string;
    public static session_uuid: string = "";
    public static name: string;
    public static peers: PeerConnection[] = [];
}