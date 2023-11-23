"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Connection_1 = require("./Connection");
const { v4: uuidv4 } = require('uuid');
class ClientConnection extends Connection_1.Connection {
    constructor(socket, uuid, name) {
        super(socket);
        this.private_uuid = uuid;
        this.name = name;
        this.session_uuid = uuidv4();
        // this.registerSocketHandlers();
    }
    getSessionUUID() {
        return this.session_uuid;
    }
    getName() {
        return this.name;
    }
    getPrivateUUID() {
        return this.private_uuid;
    }
}
exports.default = ClientConnection;
