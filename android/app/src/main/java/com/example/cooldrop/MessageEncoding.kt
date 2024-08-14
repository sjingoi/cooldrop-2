package com.example.cooldrop

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

@Serializable
data class SerializableSDP(
    val sdp: String?,
    val type: String, // SessionDescription.Type canonical form
)

@Serializable
data class SerializableIce(
    val candidate: String,
    val sdpMLineIndex: Int,
    val sdpMid: String,
    val usernameFragment: String? = null
)

fun decodeIce(iceString: String): IceCandidate  {
    val serializableIce = Json.decodeFromString<SerializableIce>(iceString)
    return IceCandidate(serializableIce.sdpMid, serializableIce.sdpMLineIndex, serializableIce.candidate);
}

fun decodeSdp(sdpString: String): SessionDescription {
    val serializableSDP = Json.decodeFromString<SerializableSDP>(sdpString)
    return SessionDescription(SessionDescription.Type.fromCanonicalForm(serializableSDP.type), serializableSDP.sdp)
}

fun encodeIce(ice: IceCandidate): String {
    return Json.encodeToString(SerializableIce(ice.sdp, ice.sdpMLineIndex, ice.sdpMid))
}

fun encodeSdp(sdp: SessionDescription): String {
    return Json.encodeToString(SerializableSDP(sdp.description, sdp.type.canonicalForm()))
}