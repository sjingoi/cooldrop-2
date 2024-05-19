import * as http from 'http'
import { WebSocketServer, WebSocket, ErrorEvent, CloseEvent } from 'ws';
import Logger from './Logger';

type CooldropIOServerCallback = (cws: CooldropIOSocket) => void

export class CooldropIOServer {

    private wss: WebSocketServer
    private callbacks: Array<CooldropIOServerCallback> = []

    constructor(server: http.Server) {
        this.wss = new WebSocketServer({server});
        Logger.getIns().logVerbose("Created Websocket")
        this.wss.on('connection', (cws) => {this.callbacks.forEach(callback => {
            Logger.getIns().logVerbose("Recieved Connection")
            let socket = new CooldropIOSocket(cws)
            callback(socket)
        })})
    }

    public onConnection(callback: CooldropIOServerCallback) {
        this.callbacks.push(callback)
    }
}

interface IOSocketMessage {
    type: string,
    data: any,
}

type CooldropIOClientCallback = (data: any) => void

interface CallbackCollection {
    [type: string] : CooldropIOClientCallback;
} 

export class CooldropIOSocket {

    protected cws: WebSocket;
    private callbacks: CallbackCollection = {}

    constructor(cws: WebSocket) {
        this.cws = cws
        this.cws.on
        this.cws.on('message', data => {
            let message: IOSocketMessage = JSON.parse(data.toString()) as IOSocketMessage
            let callback: CooldropIOClientCallback = this.callbacks[message.type]
            if (callback) {
                callback(data)
            } else {
                Logger.getIns().logInfo("Unhandled message type: " + message.type)
                console.log(this.callbacks)
            }
        })
    }

    public on(message_type: string, callback: CooldropIOClientCallback) {
        Logger.getIns().logVerbose("Added " + message_type + " callback")
        this.callbacks[message_type] = callback
    }

    public onClose(callback: ((event: CloseEvent) => void) | null) {
        this.cws.onclose = callback
    }

    public onError(callback: ((event: ErrorEvent) => void) | null) {
        this.cws.onerror = callback
    }

    public emit(type: string, data: any) {
        let message: IOSocketMessage = {
            type: type,
            data: data,
        }
        this.cws.send(JSON.stringify(message))
    }

}