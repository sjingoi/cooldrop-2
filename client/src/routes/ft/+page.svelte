<script lang="ts">

    import InfoPanel from "$lib/InfoPanel.svelte";
    import PeersArea from "$lib/PeersArea.svelte";
    import { v4 as uuidv4 } from 'uuid';
    import { onMount } from "svelte";

    import { ServerMessageType, ServerConnection } from "./ServerConnection";
    import { FilePeerConnection, PeerConnection } from "$lib/PeerConnection";
    import type { IceCandidate, PeerInfo, SDP, SDPEvent } from "$lib/types";
    import { UserInfo } from "$lib/userinfo";
    
    import { session_uuid, peers } from "../../stores/stores";
    import { get } from "svelte/store";
    import Peer from "$lib/Peer.svelte";
    
    
    onMount(() => {

        UserInfo.name = localStorage.getItem("name") || "";
        UserInfo.private_uuid = localStorage.getItem("uuid") || "";
        
        if (UserInfo.private_uuid === "") {
            UserInfo.private_uuid = uuidv4();
            localStorage.setItem("uuid", UserInfo.private_uuid);
        }

        const serverconnection: ServerConnection = new ServerConnection("craft.jesus.si:8080");

        serverconnection.addMessageListener(ServerMessageType.PUBLIC_UUID, (data) => {
            UserInfo.session_uuid = data;
            session_uuid.set(data);
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
            let peer = new FilePeerConnection(peerInfo.peer_uuid, peerInfo.peer_name, serverconnection);
            peers.update(list => [ ...list, peer ]);
        });

        serverconnection.addMessageListener(ServerMessageType.SDP_OFFER, (data) => {
            let sdp_offer: SDP = JSON.parse(data);
            let peer = new FilePeerConnection(sdp_offer.origin_uuid, sdp_offer.origin_name, serverconnection, sdp_offer.sdp);
            peers.update(list => [ ...list, peer ]);
        });

        serverconnection.addMessageListener(ServerMessageType.SDP_ANSWER, (data) => {
            let sdp_answer: SDP = JSON.parse(data);
            const predicate = (peer: FilePeerConnection) => { return peer.getUUID() == sdp_answer.origin_uuid; }
            let peer_connection = get(peers).find(predicate);
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
            let peer_connection = get(peers).find(predicate);
            if (peer_connection) {
                peer_connection.addIceCandidate(candidate.ice);
            } else {
                console.error("Could not find peer " + (candidate.origin_uuid));
            }
        });

        return () => {
            serverconnection.close();
            peers.update(peers => {
                peers.forEach(peer => {
                    //close connection
                })
                return [];
            })
        };

    })

</script>

<InfoPanel name={UserInfo.name} public_uuid={$session_uuid}/>
<div>
    {#each $peers as peer}
        <Peer peer={peer} />
    {/each}
</div>
<!-- <PeersArea peers={$peers}/> -->