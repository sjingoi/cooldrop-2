export interface FileHeader {
    filename: string,
    filetype: string,
    filesize: number,
    chunksize: number,
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
        this.lastchunksize = this.filesize % this.chunksize;
        this.chunkcount = Math.ceil(this.filesize / this.chunksize);
    }

    public addChunk(chunk: any) {
        this.data.push(chunk);
        console.log("Array Length: " + this.data.length)
        console.log("Chunk count " + this.chunkcount)
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
