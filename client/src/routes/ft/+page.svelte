<script lang="ts">

    import { v4 as uuidv4 } from 'uuid';
    import { onMount } from "svelte";

    import { ServerMessageType, ServerConnection } from "./ServerConnection";
    import { FilePeerConnection, PeerConnection } from "$lib/PeerConnection";
    import type { IceCandidate, PeerInfo, SDP, SDPEvent } from "$lib/types";

    import Peer from "$lib/Peer.svelte";
    
    let display_name: string = "";
    let session_uuid: string = "";
    let private_uuid: string = "";
    let peers: FilePeerConnection[] = [];

    onMount(() => {

        display_name = localStorage.getItem("name") || "";
        private_uuid = localStorage.getItem("uuid") || "";
        
        if (private_uuid === "") {
            private_uuid = uuidv4();
            localStorage.setItem("uuid", private_uuid);
        }

        const serverconnection: ServerConnection = new ServerConnection("localhost:8080");

        serverconnection.addMessageListener(ServerMessageType.PUBLIC_UUID, (data) => {
            session_uuid = data
        });

        serverconnection.addMessageListener(ServerMessageType.PRIVATE_UUID_REQ, (data) => {
            let info: PeerInfo = {
                peer_uuid: session_uuid,
                peer_name: display_name,
            }
            serverconnection.send(ServerMessageType.PRIVATE_UUID, JSON.stringify(info));
        });

        serverconnection.addMessageListener(ServerMessageType.SDP_OFFER_REQ, (data) => {
            console.log("Creating new peer");
            let peerInfo: PeerInfo = JSON.parse(data);
            let peer = new FilePeerConnection(peerInfo.peer_uuid, peerInfo.peer_name, display_name, session_uuid, serverconnection);
            peers = [ ...peers, peer ];
        });

        serverconnection.addMessageListener(ServerMessageType.SDP_OFFER, (data) => {
            let sdp_offer: SDP = JSON.parse(data);
            let peer = new FilePeerConnection(sdp_offer.origin_uuid, sdp_offer.origin_name, display_name, session_uuid, serverconnection, sdp_offer.sdp);
            peers = [ ...peers, peer ];
        });

        serverconnection.addMessageListener(ServerMessageType.SDP_ANSWER, (data) => {
            let sdp_answer: SDP = JSON.parse(data);
            const predicate = (peer: FilePeerConnection) => { return peer.getUUID() == sdp_answer.origin_uuid; }
            let peer_connection = peers.find(predicate);
            if (peer_connection) {
                peer_connection.setRemote(sdp_answer.sdp);
            } else {
                console.error("Could not find peer " + (sdp_answer.origin_uuid));
            }
        });

        serverconnection.addMessageListener(ServerMessageType.ICE_CANDIDATE, (data) => {
            console.log("Adding ice");
            let candidate: IceCandidate = JSON.parse(data);
            const predicate = (peer: FilePeerConnection) => { return peer.getUUID() == candidate.origin_uuid; }
            let peer_connection = peers.find(predicate);
            if (peer_connection) {
                peer_connection.addIceCandidate(candidate.ice);
            } else {
                console.error("Could not find peer " + (candidate.origin_uuid));
            }
        });

        return () => {
            serverconnection.close();
            peers.forEach(peer => {
                //close connection
            })
            return [];
        };

    })

</script>

<!-- <InfoPanel name={UserInfo.name} public_uuid={session_uuid}/> -->

<p>Name: {display_name}</p>
<p>UUID: {session_uuid}</p>
<div>
    {#each peers as peer}
        <Peer peer={peer} />
    {/each}
</div>
<!-- <PeersArea peers={$peers}/> -->