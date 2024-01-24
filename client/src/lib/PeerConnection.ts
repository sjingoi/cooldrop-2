import { Peer } from "$lib/Peer";
import { ServerMessageType, type ServerConnection } from "../routes/ft/ServerConnection";
import type { IceCandidate, SDP } from "./types";
import { FileData, type FileHeader } from "./File";

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
    protected signalling_server: ServerConnection;
    protected display_name: string;
    protected session_uuid: string;
    public connected: boolean;

    constructor(peer_uuid: string, peer_name: string | null, display_name: string, session_uuid: string, signalling_server: ServerConnection, remote_offer?: string) {
        super(peer_uuid, peer_name);
        this.display_name = display_name;
        this.session_uuid = session_uuid;
        this.signalling_server = signalling_server;
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
            origin_uuid: this.session_uuid,
            recipient_uuid: this.getUUID(),
            ice: JSON.stringify(event.candidate),
        }
        this.signalling_server.send(ServerMessageType.ICE_CANDIDATE, JSON.stringify(ice_candidate));
        
    }

    protected onSDP(sdp: RTCSessionDescriptionInit) {

        if (sdp === null) {
            console.log("SDP is null.");
            return;
        }
        this.rtc_connection.setLocalDescription(sdp)
        
        let sdp_offer: SDP = {
            origin_uuid: this.session_uuid,
            origin_name: this.display_name,
            recipient_uuid: this.getUUID(),
            sdp: JSON.stringify(sdp),
        }

        if (sdp.type == "offer") {
            this.signalling_server.send(ServerMessageType.SDP_OFFER, JSON.stringify(sdp_offer));
        } else if (sdp.type == "answer") {
            this.signalling_server.send(ServerMessageType.SDP_ANSWER, JSON.stringify(sdp_offer));
        }

        let sdpEvent: Event = new CustomEvent(PeerConnectionEvents.SDP, { detail: { sdp: JSON.stringify(this.rtc_connection.localDescription) } });
        this.dispatchEvent(sdpEvent);

    }

}



export class FilePeerConnection extends PeerConnection {

    private chunk_size: number = 64*1024;
    private current_file: FileData | null = null;

    constructor(uuid: string, name: string | null, display_name: string, session_uuid: string, server_connection: ServerConnection, remote_offer?: string) {
        super(uuid, name, display_name, session_uuid, server_connection, remote_offer);
        this.addEventListener("message", (e: Event) => {
            const customEvent  = e as CustomEvent;
            this.handleMessage(customEvent.detail);
        })
    }

    public sendFile(file: File) {
        console.log("Sending file");
        this.sendHeader(file);
        this.sendFileData(file);
    }

    private handleMessage(message: any) {
        const dataChannel = this.rtc_datachannel;

        if (dataChannel === undefined) {
            console.log("Data channel not initialized.");
            return;
        }
        //console.log("Type of data: " + typeof(message.data));
    
        if (typeof(message) !== "string") {
            if (!this.current_file) {
                console.log("Recieving data without file header!");
                return;
            }
            const chunk: any = message;
            this.current_file.addChunk(chunk);
            //console.log(fileHeader.numChunks);
            //console.log(dataChannel);
            dataChannel.send(JSON.stringify({
                type: 'progress',
                progress: this.current_file.getProgress()
            }))

            // this.on_progess(this.chunks.length / this.current_file_header.chunkcount);
            console.log("Progress: " + this.current_file.getProgress());
            
            if (this.current_file.getProgress() == 1) {
                this.current_file.download();
                this.current_file = null;
            }
        } else {
            var msg: any = JSON.parse(message)
    
            switch (msg.type) {
                case 'text':
                    // recieveBox.textContent = message.data
                    break;
                case 'header':
                    let header: FileHeader = msg;
                    this.current_file = new FileData(header);
                    //console.log(this.file_header);
                    break;
                case 'progress':
                    // this.on_progess(msg.progress);
                    console.log("Progress: " + msg.progress);
                    break;
                default:
                    console.log('unknown message type: ' + msg.type);
            }
    
        }
    }

    private sendHeader(file: File) {
        if (this.rtc_datachannel === undefined) {
            console.error("Tried to send header but datachannel is not initialized.");
            return;
        }
        console.log("Sending header");
        const numChunks = Math.ceil(file.size / this.chunk_size);
        this.rtc_datachannel.send(JSON.stringify({
            type: 'header',
            filename: file.name,
            filetype: file.type,
            filesize: file.size,
            chunksize: this.chunk_size,
            lastchunksize: (file.size % this.chunk_size),
            chunkcount: numChunks
        }))
    }

    private sendFileData(file: File, chunk_size: number = this.chunk_size, offset: number = 0) {
        const chunk: Blob = file.slice(offset, offset + chunk_size);
        const reader: FileReader = new FileReader();

        const this_connection = this;
        const dataChannel = this.rtc_datachannel;
        if (dataChannel === undefined) {
            console.error("Tried to send file data but datachannel is not initialized.");
            return;
        }
        reader.onload = function(event) {
            if (dataChannel.bufferedAmount + chunk_size >= 16 * 1024 * 1024) {
                console.log("Waiting for buffer to clear...");
                setTimeout(() => {
                    this_connection.sendFileData(file, chunk_size, offset);
                }, 100)
            } else if (offset <= file.size){
                if (event.target !== null && event.target.result !== null && typeof(event.target.result) !== 'string') {
                    dataChannel.send(event.target.result);
                }
                this_connection.sendFileData(file, chunk_size, offset + chunk_size);
            } else {
                console.log("Done sending!");
            }
            
        }
        reader.readAsArrayBuffer(chunk);
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