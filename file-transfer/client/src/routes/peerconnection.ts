import type { SDP } from "./serverconnection";

const SERVERS = {
    iceServers:[
        {
            urls:["stun:stun1.l.google.com:19302", "stun:stun1.l.google.com:19302", "stun:stun2.l.google.com:19302"],
        }
    ]
}

interface PeerConnectionEventMap {
    "open": Event,
    "close": Event,
    "message": MessageEvent,
    "sdp": SDPEvent,
}

export interface PeerInfo {
    name: string,
    uuid: string,
}

export interface SDPEvent extends Event {
    readonly sdp: SDP,
}


export abstract class PeerConnection extends EventTarget {

    protected rtc_connection: RTCPeerConnection;
    protected rtc_datachannel?: RTCDataChannel;

    constructor() {
        super();
        this.rtc_connection = new RTCPeerConnection(SERVERS);
        this.rtc_connection.onicecandidate = ice_event => this.onIce(ice_event);
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
        this.dispatchEvent(event);
    }

    protected onClose(event: Event) {
        this.dispatchEvent(event);
    }

    protected onMessage(message: MessageEvent) {
        this.dispatchEvent(message);
    }

    protected onIce(event: RTCPeerConnectionIceEvent) {
        if (event.candidate === null) {
            this.onSDP();
        }
    }

    protected onSDP() {
        if (this.rtc_connection.localDescription !== null) {
            let sdpEvent: SDPEvent = new 
            this.dispatchEvent(sdpEvent);
        } else {
            console.log("Could not send local connection as it is null.");
        }
    }

    addEventListener<K extends keyof PeerConnectionEventMap>(type: K, listener: EventListenerOrEventListenerObject): void {
        super.addEventListener(type, listener);
    }


}

export class LocalPeerConnection extends PeerConnection {
    constructor() {
        super();
        this.rtc_datachannel = this.rtc_connection.createDataChannel("channel");
        this.setDatachannelHandlers();
    
        this.rtc_connection.onicecandidate = this.onIce;
        this.rtc_connection.createOffer().then(this.rtc_connection.setLocalDescription);
        
    }
}

export class RemotePeerConnection extends PeerConnection {
    constructor(remote_offer: SDP) {
        super();
        this.rtc_connection.setRemoteDescription(JSON.parse(remote_offer.sdp))
        this.rtc_connection.ondatachannel = (event) => {
            this.rtc_datachannel = event.channel;
            this.setDatachannelHandlers();
        }
        this.rtc_connection.createAnswer().then(this.onAnswer);
    }

    private onAnswer(answer: RTCSessionDescriptionInit) {
        this.rtc_connection.setLocalDescription(answer);
        this.onSDP();
    }
}