import { LocalPeerConnection, PeerConnection, RemotePeerConnection } from "../lib/peerconnection";
import { ServerMessageType, type ServerConnection } from "./serverconnection";
import type { IceCandidate, PeerInfo, SDP } from "../lib/types";
import { UserInfo } from "../lib/userinfo";
import { session_uuid as session_uuid_store } from "../stores/stores";

export function setupServerEventHandlers(serverconnection: ServerConnection) {
    
    serverconnection.addMessageListener(ServerMessageType.PUBLIC_UUID, (data) => {
        UserInfo.session_uuid = data;
        session_uuid_store.set(data);
    });

    serverconnection.addMessageListener(ServerMessageType.PRIVATE_UUID_REQ, (data) => {
        let info: PeerInfo = {
            peer_uuid: UserInfo.session_uuid,
            peer_name: UserInfo.name,
        }
        serverconnection.send(ServerMessageType.PRIVATE_UUID, JSON.stringify(info));
    });

    serverconnection.addMessageListener(ServerMessageType.SDP_OFFER_REQ, (data) => {
        console.log("Creating new peer");
        let peerInfo: PeerInfo = JSON.parse(data);
        let peer = new LocalPeerConnection(peerInfo.peer_uuid, peerInfo.peer_name, serverconnection);
        UserInfo.peers = [ ...UserInfo.peers, peer ];
    });

    serverconnection.addMessageListener(ServerMessageType.SDP_OFFER, (data) => {
        let sdp_offer: SDP = JSON.parse(data);
        let peer = new RemotePeerConnection(sdp_offer.origin_uuid, sdp_offer.origin_name, serverconnection, sdp_offer.sdp);
        UserInfo.peers = [ ...UserInfo.peers, peer];

    });

    serverconnection.addMessageListener(ServerMessageType.SDP_ANSWER, (data) => {
        let sdp_answer: SDP = JSON.parse(data);
        const predicate = (peer: PeerConnection) => { return peer.getUUID() == sdp_answer.origin_uuid; }
        let peer_connection = UserInfo.peers.find(predicate);
        if (peer_connection) {
            peer_connection.setRemote(sdp_answer.sdp);
        } else {
            console.error("Could not find peer " + (sdp_answer.origin_uuid));
        }
    });

    serverconnection.addMessageListener(ServerMessageType.ICE_CANDIDATE, (data) => {
        console.log("Adding ice");
        let candidate: IceCandidate = JSON.parse(data);
        const predicate = (peer: PeerConnection) => { return peer.getUUID() == candidate.origin_uuid; }
        let peer_connection = UserInfo.peers.find(predicate);
        if (peer_connection) {
            peer_connection.addIceCandidate(candidate.ice);
        } else {
            console.error("Could not find peer " + (candidate.origin_uuid));
        }
    });
}