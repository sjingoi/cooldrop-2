"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.Connection = void 0;
const Logger_1 = require("./Logger");
class Connection {
    constructor(socket) {
        this.listeners = new Set();
        this.socket = socket;
        this.socket.addEventListener("message", (event) => {
            let message = JSON.parse(event.data.toString());
            this.notifyListeners(message);
        });
    }
    notifyListeners(message) {
        Logger_1.default.getIns().logInfo("Recieved message: " + message.type);
        this.listeners.forEach((pair) => {
            const type = pair[0];
            const x = pair[1];
            if (type === message.type) {
                x(message.data);
            }
        });
    }
    addMessageListener(type, listener) {
        this.listeners.add([type, listener]);
    }
    addOpenListener(listener) {
        this.socket.addEventListener("open", listener);
    }
    addCloseListener(listener) {
        this.socket.addEventListener("close", listener);
    }
    send(type, data) {
        var _a;
        let message = {
            type: type,
            data: data,
        };
        (_a = this.socket) === null || _a === void 0 ? void 0 : _a.send(JSON.stringify(message));
        Logger_1.default.getIns().logInfo("Sending " + type);
    }
}
exports.Connection = Connection;
