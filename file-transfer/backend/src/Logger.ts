class Logger {

    private constructor() {}
    
    private static instance: Logger;

    public verbose: Boolean = false;
    
    public static getIns() {
        return Logger.instance || (Logger.instance = new Logger());
    }

    public logInfo(message: string) {
        console.log("[Info] " + message)
    } 

    public logError(message: string) {
        console.log("[Error] " + message);
    }

    public logVerbose(message: string) {
        if (this.verbose) {
            this.logInfo(message);
        }
    }

}

export default Logger;