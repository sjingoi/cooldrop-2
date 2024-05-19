interface IOSocketMessage {
    type: string,
    data: any,
}

type CooldropIOClientCallback = (data: any) => void

interface CallbackCollection {
    [type: string] : CooldropIOClientCallback;
} 

export class CooldropIOClientSocket {

    protected cws: WebSocket;
    private callbacks: CallbackCollection = {}

    constructor(url: string) {
        this.cws = new WebSocket(url)
        this.cws.onmessage = messageEvent => {
            let data = messageEvent.data
            let message: IOSocketMessage = JSON.parse(data.toString()) as IOSocketMessage
            let callback: CooldropIOClientCallback = this.callbacks[message.type]
            if (callback) {
                callback(message.data)
            } else {
                console.log(this.callbacks)
            }
        }
    }

    public on(message_type: string, callback: CooldropIOClientCallback) {
        this.callbacks[message_type] = callback
    }

    public onClose(callback: ((event: CloseEvent) => void) | null) {
        this.cws.onclose = callback
    }

    public onError(callback: ((event: Event) => void) | null) {
        this.cws.onerror = callback
    }

    public close() {
        this.cws.close()
    }

    public emit(type: string, data: any) {
        let message: IOSocketMessage = {
            type: type,
            data: data,
        }
        this.cws.send(JSON.stringify(message))
    }

}