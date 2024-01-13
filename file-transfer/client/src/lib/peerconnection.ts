import { Peer } from "$lib/Peer";
import { ServerMessageType, type ServerConnection } from "../routes/ServerConnection";
import { get, type Writable } from "svelte/store";
import { UserInfo } from "./userinfo";
import type { IceCandidate, SDP } from "./types";
import { session_uuid } from "../stores/stores";

const SERVERS = { iceServers: [ { urls:["stun:stun1.l.google.com:19302"], } ] }

export enum PeerConnectionEvents {
    OPEN = "open",
    CLOSE = "close",
    MESSAGE = "message",
    SDP = "sdp",
}

export class PeerConnection extends Peer { //extends typedEventTarget {

    protected rtc_connection: RTCPeerConnection;
    protected rtc_datachannel?: RTCDataChannel;
    protected server_connection: ServerConnection;
    public connected: boolean;

    constructor(uuid: string, name: string | null, server_connection: ServerConnection, remote_offer?: string) {
        super(uuid, name);
        this.server_connection = server_connection;
        this.rtc_connection = new RTCPeerConnection(SERVERS);
        this.rtc_connection.addEventListener("icecandidate", (ice_event) => this.onIce(ice_event));
        this.connected = false;

        if (remote_offer) {
            this.construct_remote(remote_offer)
        } else {
            this.construct_local();
        }
    }

    private construct_remote(remote_offer: string) {
        this.rtc_connection.setRemoteDescription(JSON.parse(remote_offer))
            this.rtc_connection.ondatachannel = (event) => {
                this.rtc_datachannel = event.channel;
                this.setDatachannelHandlers();
            }
        this.rtc_connection.createAnswer().then((sdp) => this.onSDP(sdp));
    }

    private construct_local() {
        this.rtc_datachannel = this.rtc_connection.createDataChannel("channel");
        this.setDatachannelHandlers();
        this.rtc_connection.createOffer().then((sdp) => this.onSDP(sdp));
    }

    public setRemote(remote_offer: string) {
        console.log("Setting remote description");
        this.rtc_connection.setRemoteDescription(JSON.parse(remote_offer));
    }

    public addIceCandidate(ice_candidate: string) {
        console.log("Adding ice candidate");
        this.rtc_connection.addIceCandidate(JSON.parse(ice_candidate));
    }

    protected setDatachannelHandlers() {
        if (this.rtc_datachannel) {
            this.rtc_datachannel.addEventListener("open", (event) => this.onOpen(event));
            this.rtc_datachannel.addEventListener("message", (event) => this.onMessage(event));
            this.rtc_datachannel.addEventListener("close", (event) => this.onClose(event));
        } else {
            throw new Error("Data channel not defined yet.");
        }
    }

    protected onOpen(event: Event) {
        console.log("OPENED")
        this.connected = true;
        this.dispatchEvent(new CustomEvent("open"));
    }

    protected onClose(event: Event) {
        console.log("CLOSED")
        this.connected = false;
        this.dispatchEvent(new CustomEvent("close"));
    }

    protected onMessage(message: MessageEvent) {
        console.log("MESSAGE")
        this.dispatchEvent(new CustomEvent("message", { detail: message.data }));
    }

    protected onIce(event: RTCPeerConnectionIceEvent) {

        if (event.candidate === null) return;

        let ice_candidate: IceCandidate = {
            origin_uuid: UserInfo.session_uuid,
            recipient_uuid: this.getUUID(),
            ice: JSON.stringify(event.candidate),
        }
        this.server_connection.send(ServerMessageType.ICE_CANDIDATE, JSON.stringify(ice_candidate));
        
    }

    protected onSDP(sdp: RTCSessionDescriptionInit) {

        if (sdp === null) {
            console.log("SDP is null.");
            return;
        }
        this.rtc_connection.setLocalDescription(sdp)
        
        let sdp_offer: SDP = {
            origin_uuid: UserInfo.session_uuid,
            origin_name: UserInfo.name,
            recipient_uuid: this.getUUID(),
            sdp: JSON.stringify(sdp),
        }

        if (sdp.type == "offer") {
            this.server_connection.send(ServerMessageType.SDP_OFFER, JSON.stringify(sdp_offer));
        } else if (sdp.type == "answer") {
            this.server_connection.send(ServerMessageType.SDP_ANSWER, JSON.stringify(sdp_offer));
        }

        let sdpEvent: Event = new CustomEvent(PeerConnectionEvents.SDP, { detail: { sdp: JSON.stringify(this.rtc_connection.localDescription) } });
        this.dispatchEvent(sdpEvent);

    }

}

// export class LocalPeerConnection extends PeerConnection {
//     constructor(uuid: string, name: string | null, server_connection: ServerConnection) {
//         super(uuid, name, server_connection);
//         this.rtc_datachannel = this.rtc_connection.createDataChannel("channel");
//         this.setDatachannelHandlers();
//         this.rtc_connection.createOffer().then((sdp) => this.onSDP(sdp));
//     }
// }

// export class RemotePeerConnection extends PeerConnection {
//     constructor(uuid: string, name: string | null, server_connection: ServerConnection, remote_offer: string) {
//         super(uuid, name, server_connection);
//         this.rtc_connection.setRemoteDescription(JSON.parse(remote_offer))
//         this.rtc_connection.ondatachannel = (event) => {
//             this.rtc_datachannel = event.channel;
//             this.setDatachannelHandlers();
//         }
//         this.rtc_connection.createAnswer().then((sdp) => this.onSDP(sdp));
//     }

//     // private onAnswer(answer: RTCSessionDescriptionInit) {
//     //     this.onSDP(answer);
//     // }
// }