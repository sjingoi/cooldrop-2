package com.example.cooldrop.filetransfer

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cooldrop.User
import com.example.cooldrop.filetransfer.connection.peer.ConnectionObserver
import com.example.cooldrop.filetransfer.connection.peer.DataConnection
import com.example.cooldrop.filetransfer.connection.peer.DataConnectionObserver
import com.example.cooldrop.filetransfer.connection.peer.P2PConnection
import com.example.cooldrop.filetransfer.connection.peer.PeerMessage
import com.example.cooldrop.filetransfer.connection.server.CooldropIOClient
import com.example.cooldrop.filetransfer.files.FileHeader
import com.example.cooldrop.filetransfer.files.FileWriter
import com.example.cooldrop.filetransfer.files.readFile
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.webrtc.PeerConnectionFactory
import org.webrtc.PeerConnectionFactory.InitializationOptions
import org.webrtc.SessionDescription

class FileTransferViewModel(application: Application) : AndroidViewModel(application) {


    val user: User
        get() = io.user

    private var _peers: MutableList<P2PConnection> = mutableStateListOf()
    val peers: List<P2PConnection>
        get() = _peers

    var currentFile: FileWriter? = null

    val launchedEffectKey = 0

    private val io = CooldropIOClient(
        "ws://192.168.0.60:8080",
        _peers,
        CooldropIOObserver(this, application)
    )

    private class CooldropIOObserver(val vm: FileTransferViewModel, val application: Application) :
        CooldropIOClient.Observer {

        val observer = object : ConnectionObserver {
            override fun onOpen() {
                println("Opened connection")
            }

            override fun onClose() {
                println("Closed connection")
            }

            override fun onError() {
                TODO("Not yet implemented")
            }

        }

        val dataObserver = object : DataConnectionObserver {
            override fun onReceiveData(byteArray: ByteArray) {
                vm.currentFile?.let {
                    it.addChunk(byteArray)
                    if (it.progress == 1F) {
                        it.close()
                        vm.currentFile = null;
                        println("Finished sending file!")
                    }
                    println("New progress: ${it.progress}")
                } ?: println("Received file data without header!")
            }

            override fun onReceiveText(string: String) {
                try {
                    val message: PeerMessage = Json.decodeFromString(string)
                    when (message.type) {
                        "header" -> {
                            val fileHeader: FileHeader = Json.decodeFromString(message.data)
                            println("Downloading file ${fileHeader.filename}")
                            val contentResolver = application.applicationContext.contentResolver
                            val contentValues = ContentValues().apply {
                                put(MediaStore.Downloads.DISPLAY_NAME, fileHeader.filename)
                                put(
                                    MediaStore.Downloads.RELATIVE_PATH,
                                    Environment.DIRECTORY_DOWNLOADS
                                )
                            }
                            val uri = contentResolver.insert(
                                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                                contentValues
                            )
                            if (uri == null) {
                                println("Unable to get uri for file writing")
                                return
                            }
                            val outputStream = contentResolver.openOutputStream(uri)
                            if (outputStream == null) {
                                println("Unable to open outputstream")
                                return
                            }
                            vm.currentFile = FileWriter(outputStream, fileHeader)
                        }

                        else -> {
                            println("Unknown message type: ${message.type}")
                        }
                    }
                } catch (e: IllegalArgumentException) {
                    println("Peer message formatted incorrectly")
                }
            }
        }

        override fun onPeerJoin(peer: PeerInfo, sessionDescription: SessionDescription?) {
            val newPeer = P2PConnection(
                vm.io,
                observer,
                dataObserver,
                PeerConnectionFactory.builder().createPeerConnectionFactory(),
                peer,
                sessionDescription
            )
            vm.addPeer(newPeer)
            newPeer.openConnection()
        }

        override fun onPeerLeave(peerInfo: PeerInfo) {
            vm.removePeer(peerInfo)
        }

        override fun onClose() {
            vm._peers = mutableStateListOf()
        }

    }

    init {
        PeerConnectionFactory.initialize(
            InitializationOptions.builder(application.applicationContext)
                .createInitializationOptions()
        )
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
        val dataConnection = peers.find({ it.peerInfo == peer }) ?: return
        val contentResolver = getApplication<Application>().applicationContext.contentResolver;
        viewModelScope.launch {
            uris.forEach { uri ->
                readFile(
                    uri,
                    contentResolver,
                    { onHeader(dataConnection, it) },
                    { onChunk(dataConnection, it) }
                )
            }
        }
    }

    private fun onHeader(connection: DataConnection, header: FileHeader) {
        println("Sending ${header}")
        val message = PeerMessage(
            type = "header",
            data = Json.encodeToString(header)
        )
        connection.sendText(Json.encodeToString(message))
    }

    private fun onChunk(connection: DataConnection, byteArray: ByteArray): Boolean {
        if (!connection.sendData(byteArray)) {
            println("SEND FAIL!!!!!!!!!!!!!!!!")
            return false
        }
        return true
    }
}