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
    PEER_DISCONNECT = "peer-disconnect",
}