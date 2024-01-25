<script lang="ts">
    import type { FormEventHandler } from "svelte/elements";
    import { PeerConnectionEvents, type FilePeerConnection, PeerConnection } from "./PeerConnection";

    export let peer: FilePeerConnection;
    let connected: boolean = false;

    let progress = 0;
    peer.addEventListener(PeerConnectionEvents.FILE_PROGRESS, (updated_progress: any) => {progress = updated_progress.detail.progress});
    peer.addEventListener(PeerConnectionEvents.OPEN, () => {connected = true})

    let files: FileList;

    $: if (files) {
        console.log(files);
        peer.sendFile(files[0]);
    }

</script>

<div>
    <h3>{peer.name}</h3>
    <p>{peer.uuid}</p>
    {#if connected}
        <label for="file">Send File</label>
        <input bind:files type="file" multiple/>
        <progress value={progress}></progress>
    {:else}
        <p>Connecting to peer...</p>
    {/if}
    
    
</div>