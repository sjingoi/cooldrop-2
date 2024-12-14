package com.example.cooldrop.filetransfer

import com.example.cooldrop.filetransfer.connection.peer.P2PConnection
import java.util.UUID

class PeerClass(val publicUuid: UUID) {
    var name: String = "";
    var connection: P2PConnection? = null
    val peerInfo: PeerInfo
        get() { return PeerInfo(publicUuid, name) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PeerClass) return false
        if (publicUuid == other.publicUuid) return true
        return false
    }

    override fun hashCode(): Int {
        return publicUuid.hashCode()
    }
}