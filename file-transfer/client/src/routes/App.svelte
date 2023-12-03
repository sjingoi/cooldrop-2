<script lang="ts">

    import InfoPanel from "../lib/InfoPanel.svelte";
    import PeersArea from "../lib/PeersArea.svelte";
    import { v4 as uuidv4 } from 'uuid';
    import { onMount } from "svelte";

    import { ServerMessageType, ServerConnection } from "./serverconnection";
    import { LocalPeerConnection, PeerConnection, PeerConnectionEvents, RemotePeerConnection } from "../lib/peerconnection";
    import { Peer } from "../lib/peer";
    import type { IceCandidate, PeerInfo, SDP, SDPEvent } from "../lib/types";
    import { UserInfo } from "../lib/userinfo";
    import { setupServerEventHandlers } from "./serverevents";
    
    
    onMount(() => {

        UserInfo.name = localStorage.getItem("name") || "";
        UserInfo.private_uuid = localStorage.getItem("uuid") || "";
        
        if (UserInfo.private_uuid === "") {
            UserInfo.private_uuid = uuidv4();
            localStorage.setItem("uuid", UserInfo.private_uuid);
        }

        const serverconnection: ServerConnection = new ServerConnection("192.168.0.46:8080");

        setupServerEventHandlers(serverconnection);

        return () => {
            serverconnection.close();
        };

    })

</script>
<InfoPanel name={UserInfo.name} public_uuid={UserInfo.session_uuid}/>
<PeersArea peers={UserInfo.peers}/>