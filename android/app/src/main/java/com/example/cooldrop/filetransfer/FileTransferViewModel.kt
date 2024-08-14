package com.example.cooldrop.filetransfer

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.example.cooldrop.CooldropIOClient
import com.example.cooldrop.P2PConnection
import com.example.cooldrop.P2PConnectionObserver
import com.example.cooldrop.User
import kotlinx.serialization.Serializable
import org.webrtc.PeerConnectionFactory
import org.webrtc.PeerConnectionFactory.InitializationOptions
import org.webrtc.SessionDescription
import java.io.InputStream


@Serializable
data class FileHeader(
    val type: String,
    val filename: String,
    val filetype: String,
    val filesize: Long,
    val chunksize: Int,
    val lastchunksize: Int,
    val chunkcount: Long,
)

class FileTransferViewModel(application: Application) : AndroidViewModel(application) {


    val user: User
        get() = io.user

    private var _peers: MutableList<P2PConnection> = mutableStateListOf()
    val peers: List<P2PConnection>
        get() = _peers

    val launchedEffectKey = 0

    private val io = CooldropIOClient(
        "ws://192.168.0.60:8080",
        _peers,
        { _peers = mutableStateListOf() },
        mutableSetOf(
            CooldropIOObserver(this )
        )
    )

    private class CooldropIOObserver (val vm: FileTransferViewModel) : CooldropIOClient.Observer {

        val observer = object : P2PConnectionObserver {
            override fun onOpen() {
                TODO("Not yet implemented")
            }

            override fun onClose() {
                TODO("Not yet implemented")
            }

            override fun onError() {
                TODO("Not yet implemented")
            }

        }

        override fun onSDPOffer(sessionDescription: SessionDescription, peerInfo: PeerInfo) {
            vm.addPeer(P2PConnection(
                vm.io,
                observer,
                PeerConnectionFactory.builder().createPeerConnectionFactory(),
                peerInfo,
                sessionDescription
            ))
            println("Added to peer list: ${vm._peers}")
        }

        override fun onPeerDisconnect(peerInfo: PeerInfo) {
            vm.removePeer(peerInfo)
        }

        override fun onSDPOfferReq(peerInfo: PeerInfo) {
            val newPeerConnection = P2PConnection(
                vm.io,
                observer,
                PeerConnectionFactory.builder().createPeerConnectionFactory(),
                peerInfo,
                null
            )
        }

    }

    init {
        PeerConnectionFactory.initialize(InitializationOptions.builder(application.applicationContext).createInitializationOptions())
    }

    fun connectToServer() {
        io.connect()
    }

    fun removePeer(peerInfo: PeerInfo) {
        val peerToRemove = _peers.find { it.peerInfo.publicUuid == peerInfo.publicUuid }
        _peers.remove(peerToRemove)
    }

    fun addPeer(peer: P2PConnection) {
        _peers.add(peer)
    }

    fun setName(newName: String) {
        io.user = User(newName, user.publicUuid, user.privateUuid)
    }

    fun onPeerClicked(peer: PeerInfo) {
        println("Peer clicked")
    }

    fun onPeerUris(peer: PeerInfo, uris: List<Uri>) {
        val chunksize: Int = 64*1024

        println("Peer $peer opened files $uris")

        val projection = arrayOf(
            OpenableColumns.DISPLAY_NAME,
            OpenableColumns.SIZE,
        )

        if (uris.size > 0) {
            val uri = uris[0];
            val contentResolver = getApplication<Application>().applicationContext.contentResolver;
            val cursor = contentResolver.query(uri, projection, null, null, null);

            val fileName: String;
            val fileSize: Long;

            cursor?.use {
                if (cursor.moveToFirst()) {
                    val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

                    fileName = cursor.getString(displayNameIndex);
                    fileSize = cursor.getLong(sizeIndex);

                    contentResolver.openInputStream(uri)?.let { inputStream: InputStream ->

                        var chunkcount = (fileSize / chunksize)
                        val lastchunksize = (fileSize % chunksize).toInt();
                        if (lastchunksize != 0) {
                            chunkcount ++;
                        }
                        val fileHeader = FileHeader(
                            type = "header",
                            filename = fileName,
                            filetype = "",
                            filesize = fileSize,
                            chunksize = chunksize,
                            lastchunksize = lastchunksize,
                            chunkcount = chunkcount
                        )

                        inputStream.close();
                    }
                }
            } ?: run {
                println("Failed to open cursor")
                return
            }
        }
    }
}

//private fun getPeers() = List(5) {
//    Peer(UUID.randomUUID(), "Seb's Device")
//}