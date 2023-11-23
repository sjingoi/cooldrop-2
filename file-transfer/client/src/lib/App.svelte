<script lang="ts">

    import InfoPanel from "./InfoPanel.svelte";
    import PeersArea from "./PeersArea.svelte";
    import { v4 as uuidv4 } from 'uuid';

    import { ServerConnection } from "../routes/serverconnection";
    import { type ServerMessage, MessageType, type SDP } from "../routes/serverconnection";
    import type { PeerInfo } from "../routes/peerconnection";
    import { Peer } from "../routes/peer";
    import { onMount } from "svelte";

    let uuid: string;
    let public_uuid: string = "";
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
        });

        serverconnection.addMessageListener(MessageType.PRIVATE_UUID_REQ, (data) => {
            let info: PeerInfo = {
                uuid: public_uuid,
                name: name,
            }
            serverconnection.send(MessageType.PRIVATE_UUID, JSON.stringify(info));
        });

        serverconnection.addMessageListener(MessageType.SDP_OFFER_REQ, (data) => {
            let peerInfo: PeerInfo = JSON.parse(data);
            let connection = new LocalPeerConnection();
            let peer: Peer = new Peer(peerInfo.name, peerInfo.uuid, connection);
            peer.connection.addEventListener("open", (event) => console.log("OPENED")); // Race condition
            peer.connection.addEventListener("close", (event) => console.log("CLOSED"));
            peer.connection.addEventListener("message", (event) => {})
            peer.connection.addEventListener("sdp", (event) => {
                let sdp_offer: SDP = {
                    origin_uuid: public_uuid,
                    recipient_uuid: peer.getUUID(),
                    sdp: event
                }
                serverconnection.send()
            })
            peers = [ ...peers, peer ];

            serverconnection.addMessageListener(MessageType.SDP_ANSWER, (data) => {
                peer.connection
            })
        });

        serverconnection.addMessageListener(MessageType.SDP_OFFER, (data) => {
            let sdp_offer: SDP = JSON.parse(data);
        })

        return () => {
            serverconnection.close();
        };

    })

</script>
<InfoPanel name={name} public_uuid={public_uuid}/>
<PeersArea />