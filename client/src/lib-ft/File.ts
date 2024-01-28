export interface FileHeader {
    type: 'header',
    filename: string,
    filetype: string,
    filesize: number,
    chunksize: number,
    lastchunksize: number,
    chunkcount: number
}

export class FileData {

    public filename: string;
    public filesize: number;
    public filetype: string;
    public chunksize: number;
    public lastchunksize: number;
    public chunkcount: number;

    private data: Array<any> = [];

    constructor(fileheader: FileHeader) {
        this.filename = fileheader.filename;
        this.filesize = fileheader.filesize;
        this.filetype = fileheader.filetype;
        this.chunksize = fileheader.chunksize;
        this.lastchunksize = fileheader.lastchunksize;
        this.chunkcount = fileheader.chunkcount;
    }

    public addChunk(chunk: any) {
        this.data.push(chunk);
    }

    public getProgress() : number {
        return this.data.length / this.chunkcount;
    }

    public download() {
        const blob = new Blob(this.data);

        const downloadLink = document.createElement('a');
        downloadLink.href = URL.createObjectURL(blob);
        downloadLink.download = this.filename;
        downloadLink.click();
        URL.revokeObjectURL(downloadLink.href);
    }
}
