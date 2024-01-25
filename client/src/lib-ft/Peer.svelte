<script lang="ts">
    import type { FormEventHandler } from "svelte/elements";
    import { PeerConnectionEvents, type FilePeerConnection, type PeerConnection } from "./PeerConnection";

    export let peer: FilePeerConnection;

    let progress = 0;
    peer.addEventListener(PeerConnectionEvents.FILE_PROGRESS, (updated_progress: any) => {progress = updated_progress.detail.progress});

    let files: FileList;

    $: if (files) {
        console.log(files);
        peer.sendFile(files[0]);
    }

</script>

<div>
    <h3>{peer.name}</h3>
    <p>{peer.uuid}</p>
    <label for="file">Send File</label>
    <input bind:files type="file" multiple/>
    <progress value={progress}></progress>
</div>