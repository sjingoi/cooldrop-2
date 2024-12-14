package com.example.cooldrop.filetransfer.connection.peer

import io.ktor.util.moveToByteArray
import org.webrtc.DataChannel
import org.webrtc.MediaStream
import org.webrtc.PeerConnectionFactory
import java.nio.ByteBuffer

class WebRTCDataConnection(
    val observer: Observer,
    peerConnectionFactory: PeerConnectionFactory,
) : WebRTCConnection(peerConnectionFactory, observer),
    DataConnection {

    private var dataChannel: DataChannel? = null;
    private val dataObservers = mutableListOf<DataConnection.Observer>()

    override fun openConnection() {
        if (connectionType == RTCConnectionType.LOCAL) {
            this.dataChannel = rtcConnection.createDataChannel("channel", DataChannel.Init())
            this.dataChannel?.registerObserver(dataChannelObserver)
        }
    }

    override fun sendData(byteArray: ByteArray): Boolean {
        val bytes = ByteBuffer.wrap(byteArray)
        return dataChannel?.send(DataChannel.Buffer(bytes, true)) ?: false
    }

    override fun sendText(string: String): Boolean {
        val bytes = ByteBuffer.wrap(string.toByteArray())
        return dataChannel?.send(DataChannel.Buffer(bytes, false)) ?: false
    }

    override fun registerObserver(observer: DataConnection.Observer) {
        dataObservers.add(observer)
    }

    override fun onDataChannelCreated(dataChannel: DataChannel) {
        dataChannel.registerObserver(dataChannelObserver)
        this.dataChannel = dataChannel;
        observer.onOpen()
    }
    override fun onStreamAdded(mediaStream: MediaStream) {}
    override fun onStreamRemoved(mediaStream: MediaStream) {}

    private val dataChannelObserver = object : DataChannel.Observer {
        override fun onBufferedAmountChange(previousAmount: Long) {}
        override fun onStateChange() {}
        override fun onMessage(buffer: DataChannel.Buffer?) {
            buffer?.data?.moveToByteArray()?.let {
                if (buffer.binary) {
                    dataObservers.forEach { observer ->
                        observer.onReceiveData(it)
                    }
                } else {
                    dataObservers.forEach { observer ->
                        observer.onReceiveText(String(it))
                    }
                }
            }
        }
    }

}