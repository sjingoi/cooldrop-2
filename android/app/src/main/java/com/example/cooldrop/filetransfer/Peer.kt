package com.example.cooldrop.filetransfer

import org.webrtc.DataChannel
import org.webrtc.PeerConnection
import java.util.UUID

data class Peer(val publicUuid: UUID, val name: String, val rtcConnection: PeerConnection, val dataChannel: DataChannel, var connected: Boolean = false) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Peer) return false
        if (publicUuid == other.publicUuid) return true
        return false
    }

    override fun hashCode(): Int {
        return publicUuid.hashCode()
    }
}