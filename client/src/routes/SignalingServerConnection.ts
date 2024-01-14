// import { LocalPeerConnection, PeerConnection, RemotePeerConnection } from "../lib/PeerConnection";
// import { ServerMessageType, ServerConnection } from "./ServerConnection";
// import type { IceCandidate, PeerInfo, SDP } from "../lib/types";
// import { UserInfo } from "../lib/userinfo";
// import { v4 as uuidv4 } from 'uuid';
// import { peers as peers_store } from "../stores/stores"
// import { get } from "svelte/store";

// export class SignalingServerConnection extends ServerConnection {

//     public session_uuid: string;

//     public constructor(url: string) {
//         super(url)
//         this.setupServerEventHandlers();
//         this.session_uuid = "";
//     }

//     private setupServerEventHandlers() {

//         this.addMessageListener(ServerMessageType.PUBLIC_UUID, (data) => {
//             this.session_uuid = data;
//         });
    
//         this.addMessageListener(ServerMessageType.PRIVATE_UUID_REQ, (data) => {
//             let info: PeerInfo = {
//                 peer_uuid: this.session_uuid,
//                 peer_name: UserInfo.name,
//             }
//             this.send(ServerMessageType.PRIVATE_UUID, JSON.stringify(info));
//         });
    
//         this.addMessageListener(ServerMessageType.SDP_OFFER_REQ, (data) => {
//             console.log("Creating new peer");
//             let peerInfo: PeerInfo = JSON.parse(data);
//             let peer = new LocalPeerConnection(peerInfo.peer_uuid, peerInfo.peer_name, this);
//             peers_store.update(peers => ( [ ... peers, peer ]))
//             // UserInfo.peers = [ ...UserInfo.peers, peer ];
//         });
    
//         this.addMessageListener(ServerMessageType.SDP_OFFER, (data) => {
//             let sdp_offer: SDP = JSON.parse(data);
//             let peer = new RemotePeerConnection(sdp_offer.origin_uuid, sdp_offer.origin_name, this, sdp_offer.sdp);
//             peers_store.update(peers => ( [ ... peers, peer ]));
//             // UserInfo.peers = [ ...UserInfo.peers, peer];
    
//         });
    
//         this.addMessageListener(ServerMessageType.SDP_ANSWER, (data) => {
//             let sdp_answer: SDP = JSON.parse(data);
//             const predicate = (peer: PeerConnection) => { return peer.getUUID() == sdp_answer.origin_uuid; }
//             let peer_connection = get(peers_store).find(predicate);//= UserInfo.peers.find(predicate);
//             if (peer_connection) {
//                 peer_connection.setRemote(sdp_answer.sdp);
//             } else {
//                 console.error("Could not find peer " + (sdp_answer.origin_uuid));
//             }
//         });
    
//         this.addMessageListener(ServerMessageType.ICE_CANDIDATE, (data) => {
//             console.log("Adding ice");
//             let candidate: IceCandidate = JSON.parse(data);
//             const predicate = (peer: PeerConnection) => { return peer.getUUID() == candidate.origin_uuid; }
//             let peer_connection = get(peers_store).find(predicate);
//             if (peer_connection) {
//                 peer_connection.addIceCandidate(candidate.ice);
//             } else {
//                 console.error("Could not find peer " + (candidate.origin_uuid));
//             }
//         });
//     }
// }