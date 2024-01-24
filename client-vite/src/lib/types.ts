export interface PeerInfo {
    peer_uuid: string;
    peer_name: string;
}

export interface SDP {
    origin_uuid: string;
    origin_name: string | null;
    recipient_uuid: string;
    sdp: string;
}

export interface IceCandidate {
    origin_uuid: string;
    recipient_uuid: string;
    ice: string;
}

export interface SDPEvent extends Event {
    detail: {
        sdp: string;
    }
}
