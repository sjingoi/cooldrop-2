"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class Logger {
    constructor() {
        this.verbose = false;
    }
    static getIns() {
        return Logger.instance || (Logger.instance = new Logger());
    }
    logInfo(message) {
        console.log("[Info] " + message);
    }
    logError(message) {
        console.log("[Error] " + message);
    }
    logVerbose(message) {
        if (this.verbose) {
            console.log("[Debug] " + message);
        }
    }
}
exports.default = Logger;
