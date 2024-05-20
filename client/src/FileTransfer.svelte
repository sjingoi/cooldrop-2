<script lang="ts">

    import { io } from "socket.io-client";
    import { v4 as uuidv4 } from 'uuid';
    import { onMount } from "svelte";

    import { ServerMessageType } from "./lib-ft/ServerTypes";
    import { FilePeerConnection, PeerConnection, PeerConnectionEvents } from "./lib-ft/PeerConnection";
    import type { IceCandidate, PeerInfo, SDP, SDPEvent } from "./lib-ft/types";


    import Peer from "./lib-ft/Peer.svelte";
    import NavBar from "./lib-seb/NavBar.svelte";
    import { BACKEND_URL } from "./urls";
    import { CooldropIOClientSocket } from "./lib-ft/CooldropIOClient";
    
    let display_name: string = "";
    let session_uuid: string = "";
    let private_uuid: string = "";
    let peers: FilePeerConnection[] = [];

    let nav_links = [{
            title: "Home",
            link: "/seb/"
        },
        {
            title: "Github",
            link: "https://github.com/sjingoi/cooldrop-2"
        },
        {
            title: "Contact",
            link: "mailto:sebi.jingoi@gmail.com"
        },
    ]

    
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
        
        const socket = new CooldropIOClientSocket(BACKEND_URL);
        
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

<NavBar title="Cooldrop 2 (beta)" links={nav_links}/>

<div class="info-panel">
    <p on:click={() => {window.location.href = "/nameselect/"}}>Name: {display_name}</p>
    <p>UUID: {session_uuid}</p>
</div>
<div class="peers-area">
    {#each peers as peer}
        <Peer peer={peer}/>
    {/each}
    {#if peers.length == 0}
    <div class="no-clients">
        <p>No peers are currently connected. Open Cooldrop on another device, or wait for others to join.</p>
        <p class="tip">Tip: If you are having issues connecting, try refreshing the page.</p>       
    </div>
    {/if}
</div>

<style>
    .info-panel {
        height: 150px;
        padding: 8px;
    }

    .peers-area {
        display: flex;
        flex-direction: row;
        justify-content: center;
        flex-wrap: wrap;
        gap: 16px;
    }

    .no-clients {
        display: flex;
        flex-direction: column;
    }

    .no-clients p {
        text-align: center;
    }

    .no-clients .tip {
        color: #888888;
    }
</style>