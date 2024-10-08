package com.example.cooldrop.filetransfer.connection.peer

import com.example.cooldrop.filetransfer.PeerInfo
import io.ktor.util.moveToByteArray
import org.webrtc.DataChannel
import org.webrtc.PeerConnectionFactory
import org.webrtc.SessionDescription

class WebRTCDataConnection(
    peerInfo: PeerInfo,
    sessionDescription: SessionDescription?,
    override val observer: Observer,
    peerConnectionFactory: PeerConnectionFactory,
) : WebRTCConnection(peerInfo, sessionDescription, observer, peerConnectionFactory),
    DataConnection {

    protected var dataChannel: DataChannel? = null;

    protected val dataObservers = mutableListOf<DataConnection.Observer>()

    override fun openConnection() {
        super.openConnection()
        if (!remoteConnection) {
            dataChannel = rtcConnection.createDataChannel("channel", DataChannel.Init())
            dataChannel?.registerObserver(dataChannelObserver)
        }
    }

    override fun sendData(byteArray: ByteArray): Boolean {
        TODO("Not yet implemented")
    }

    override fun sendText(string: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun registerObserver(observer: DataConnection.Observer) {
        dataObservers.add(observer)
    }

    override fun closeConnection() {
        TODO("Not yet implemented")
    }

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