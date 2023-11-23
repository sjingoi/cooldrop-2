export interface ServerMessage {
    type: MessageType;
    data: string;
}

export interface SDP {
    origin_uuid: string;
    recipient_uuid: string;
    sdp: string;
}

export interface PeerInfo {
    peer_uuid: string;
    peer_name: string;
}

export enum MessageType {
    TEST = "test",
    PRIVATE_UUID = "private-uuid",
    PUBLIC_UUID = "public-uuid",
    PRIVATE_UUID_REQ = "private-uuid-req",
    SDP_OFFER = "sdp-offer",
    SDP_ANSWER = "sdp-answer",
    SDP_OFFER_REQ = "sdp-offer-req",
}


