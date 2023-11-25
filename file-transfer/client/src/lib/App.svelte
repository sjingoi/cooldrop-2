<script lang="ts">

    import InfoPanel from "./InfoPanel.svelte";
    import PeersArea from "./PeersArea.svelte";
    import { v4 as uuidv4 } from 'uuid';

    import { ServerConnection } from "../routes/serverconnection";
    import { type ServerMessage, MessageType, type SDP } from "../routes/serverconnection";
    import { LocalPeerConnection, RemotePeerConnection, type PeerInfo, PeerConnectionEvents } from "../routes/peerconnection";
    import { Peer } from "../routes/peer";
    import { onMount } from "svelte";

    let uuid: string;
    let session_uuid: string = "";
    let name: string;
    let peers: Peer[] = [];
    
    onMount(() => {

        name = localStorage.getItem("name") || "";
        uuid = localStorage.getItem("uuid") || "";
        
        if (uuid === "") {
            uuid = uuidv4();
            localStorage.setItem("uuid", uuid);
        }

        const serverconnection: ServerConnection = new ServerConnection("localhost:8080");

        serverconnection.addMessageListener(MessageType.PUBLIC_UUID, (data) => {
            session_uuid = data;
        });

        serverconnection.addMessageListener(MessageType.PRIVATE_UUID_REQ, (data) => {
            let info: PeerInfo = {
                peer_uuid: session_uuid,
                peer_name: name,
            }
            serverconnection.send(MessageType.PRIVATE_UUID, JSON.stringify(info));
        });

        serverconnection.addMessageListener(MessageType.SDP_OFFER_REQ, (data) => {
            console.log("Creating new peer");
            let peerInfo: PeerInfo = JSON.parse(data);
            let connection = new LocalPeerConnection();
            connection.addEventListener(PeerConnectionEvents.SDP, (event) => {
                let sdp_event = event as SDPEvent;
                let sdp_offer: SDP = {
                    origin_uuid: session_uuid,
                    origin_name: name,
                    recipient_uuid: peerInfo.peer_uuid,
                    sdp: sdp_event.detail.sdp
                }
                serverconnection.send(MessageType.SDP_OFFER, JSON.stringify(sdp_offer));
            })
            connection.addEventListener(PeerConnectionEvents.OPEN, (event) => console.log("OPENED")); // Race condition
            connection.addEventListener(PeerConnectionEvents.CLOSE, (event) => console.log("CLOSED"));

            let peer: Peer = new Peer(peerInfo.peer_name, peerInfo.peer_uuid, connection);
            peers = [ ...peers, peer ];

            
        });

        serverconnection.addMessageListener(MessageType.SDP_OFFER, (data) => {
            let sdp_offer: SDP = JSON.parse(data);
            let connection = new RemotePeerConnection(sdp_offer.sdp);
            connection.addEventListener(PeerConnectionEvents.SDP, (event) => {
                let sdp_event = event as SDPEvent;
                let sdp_answer: SDP = {
                    origin_uuid: session_uuid,
                    origin_name: name,
                    recipient_uuid: sdp_offer.origin_uuid,
                    sdp: sdp_event.detail.sdp,
                }
                serverconnection.send(MessageType.SDP_ANSWER, JSON.stringify(sdp_answer));
            })
            let peer: Peer = new Peer(sdp_offer.origin_name, sdp_offer.origin_uuid, connection);
            peers = [ ...peers, peer];

        });

        serverconnection.addMessageListener(MessageType.SDP_ANSWER, (data) => {
            let sdp_answer: SDP = JSON.parse(data);
            const predicate = (peer: Peer) => { return peer.getUUID() == sdp_answer.origin_uuid; }
            let peer_connection = peers.find(predicate);
            if (peer_connection) {
                peer_connection.connection.setRemote(sdp_answer.sdp);
            } else {
                console.error("Could not find peer " + (sdp_answer.origin_uuid));
            }
        })

        return () => {
            serverconnection.close();
        };

    })

</script>
<InfoPanel name={name} public_uuid={session_uuid}/>
<PeersArea />