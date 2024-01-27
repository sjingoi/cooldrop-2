<script lang="ts">

    import { io } from "socket.io-client";
    import { v4 as uuidv4 } from 'uuid';
    import { onMount } from "svelte";

    import { ServerMessageType } from "./lib-ft/ServerTypes";
    import { FilePeerConnection, PeerConnection, PeerConnectionEvents } from "./lib-ft/PeerConnection";
    import type { IceCandidate, PeerInfo, SDP, SDPEvent } from "./lib-ft/types";


    import Peer from "./lib-ft/Peer.svelte";
    import NavBar from "./lib-ft/NavBar.svelte";
    
    let display_name: string = "";
    let session_uuid: string = "";
    let private_uuid: string = "";
    let peers: FilePeerConnection[] = [];

    
    onMount(() => {
        
        display_name = localStorage.getItem("name") || "";
        if (display_name === "") {
            window.location.href = "/nameselect/";
        }
        private_uuid = localStorage.getItem("uuid") || "";
        
        if (private_uuid === "") {
            private_uuid = uuidv4();
            localStorage.setItem("uuid", private_uuid);
        }
        
        // const serverconnection: ServerConnection = new ServerConnection("localhost:8080");
        
        const socket = io("99.231.153.217:8080");
        
        socket.on(ServerMessageType.PUBLIC_UUID, (data) => {
            session_uuid = data
        });

        socket.on(ServerMessageType.PRIVATE_UUID_REQ, (data) => {
            let info: PeerInfo = {
                peer_uuid: session_uuid,
                peer_name: display_name,
            }
            socket.emit(ServerMessageType.PRIVATE_UUID, JSON.stringify(info));
        });

        socket.on(ServerMessageType.SDP_OFFER_REQ, (data) => {
            console.log("Creating new peer");
            let peerInfo: PeerInfo = JSON.parse(data);
            let new_peer = new FilePeerConnection(peerInfo.peer_uuid, peerInfo.peer_name, display_name, session_uuid, socket);
            peers = [ ...peers, new_peer ];
        });

        socket.on(ServerMessageType.SDP_OFFER, (data) => {
            console.log("Creating new peer");
            let sdp_offer: SDP = JSON.parse(data);
            let new_peer = new FilePeerConnection(sdp_offer.origin_uuid, sdp_offer.origin_name, display_name, session_uuid, socket, sdp_offer.sdp);
            peers = [ ...peers, new_peer ];
        });

        socket.on(ServerMessageType.SDP_ANSWER, (data) => {
            let sdp_answer: SDP = JSON.parse(data);
            const predicate = (peer: FilePeerConnection) => { return peer.getUUID() == sdp_answer.origin_uuid; }
            let peer_connection = peers.find(predicate);
            if (peer_connection) {
                peer_connection.setRemote(sdp_answer.sdp);
            } else {
                console.error("Could not find peer " + (sdp_answer.origin_uuid));
            }
        });

        socket.on(ServerMessageType.ICE_CANDIDATE, (data) => {
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

        socket.on(ServerMessageType.PEER_DISCONNECT, (uuid) => {
            console.log("Peer " + uuid + " left.");
            const predicate = (peer: FilePeerConnection) => { return peer.getUUID() != uuid }
            peers = peers.filter(predicate);
        })

        return () => {
            socket.close();
            peers.forEach(peer => {
                peer.close();
            })
            return [];
        };

    })

</script>

<NavBar/>

<p on:click={() => {window.location.href = "/nameselect/"}}>Name: {display_name}</p>
<p>UUID: {session_uuid}</p>
<div class="peers-area">
    {#each peers as peer}
        <Peer peer={peer}/>
    {/each}
</div>

<style>
    .peers-area {
        display: flex;
        flex-direction: row;
        justify-content: center;
        flex-wrap: wrap;
        gap: 16px
    }
</style>