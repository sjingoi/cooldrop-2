<script lang="ts">

    import InfoPanel from "./InfoPanel.svelte";
    import PeersArea from "./PeersArea.svelte";
    import { v4 as uuidv4 } from 'uuid';

    import { ServerConnection } from "../routes/serverconnection";
    import { type ServerMessage, MessageType } from "../routes/serverconnection";
    import type { PeerInfo } from "../routes/peerconnection";
    import { Peer } from "../routes/peer";
    import { onMount } from "svelte";

    let uuid: string;
    let public_uuid: string | null;
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
            public_uuid = data;
        })

        serverconnection.addMessageListener(MessageType.PRIVATE_UUID_REQ, (data) => {
            let info: PeerInfo = {
                uuid: uuid,
                name: name,
            }
            serverconnection.send(MessageType.PRIVATE_UUID, JSON.stringify(info));
        })

        serverconnection.addMessageListener(MessageType.SDP_OFFER_REQ, (data) => {
            let peerInfo: PeerInfo = JSON.parse(data);
            let peer: Peer = new Peer(peerInfo.name, peerInfo.uuid);
            peer.connection.addEventListener("open", (event) => console.log("OPENED"));
            peer.connection.addEventListener("close", (event) => console.log("CLOSED"));
            peers = [ ...peers, peer ];
        })

        return () => {
            serverconnection.close();
        };

    })

</script>
<InfoPanel name={name} public_uuid={public_uuid}/>
<PeersArea />