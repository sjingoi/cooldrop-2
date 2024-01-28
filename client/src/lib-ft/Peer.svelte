<script lang="ts">
    import type { FormEventHandler } from "svelte/elements";
    import { PeerConnectionEvents, type FilePeerConnection, PeerConnection } from "./PeerConnection";
    import ComputerIcon from "./peer-computer-icon.svg"

    export let peer: FilePeerConnection;
    let connected: boolean = false;

    let progress = 0;
    peer.addEventListener(PeerConnectionEvents.FILE_PROGRESS, (updated_progress: any) => {progress = updated_progress.detail.progress});
    peer.addEventListener(PeerConnectionEvents.OPEN, () => {connected = true});
    peer.addEventListener(PeerConnectionEvents.CLOSE, () => {connected = false});

    let files: FileList | undefined;
    let fileInput: HTMLInputElement;

    $: if (files) {
        console.log(files);
        peer.sendFile(files[0]);
    }

</script>

<button class="peer" 
    on:click={() => fileInput.click()}
    on:dragover|preventDefault
    on:drop|preventDefault={(e) => files = e.dataTransfer?.files}>
    <h2>{peer.name}</h2>
    <img src={ComputerIcon} alt="Computer Icon" class="peer-icon">
    <p class="uuid">{peer.uuid}</p>
    <div style="height: 32px">
        {#if connected}
        <progress class="progress" value={progress}></progress>
        <input class="file-input" bind:files bind:this={fileInput} type="file" multiple/>
        {:else}
            <p>Connecting to peer...</p>
        {/if}
    </div>
</button>

<style>

    .peer {
        width: 250px;
        height: 250px;
        margin: 0;
        padding: 8px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 16px;
        background-color: var(--nav-bg-color);
        border: 2px;
        border-style: solid;
        border-color: var(--nav-border-color);
        border-radius: 8px;
        transition: 100ms;
        text-align: center;
    }

    .peer:hover {
        background-color: var(--nav-bg-color-hover);
    }

    .peer h2 {
        margin: 0;
    }

    .peer p {
        margin: 0;
        color: #888888;
    }

    .peer-icon {
        width: 5rem;
        height: 5rem;
    }

    button {
        font-family: D-DIN-Regular;
    }
    
    .file-input {
        visibility: hidden;
        height: 0;
        width: 0;
        padding: 0;
        margin-top: -16px;
    }

    .uuid {
        font-size: 12px;
    }

    progress {

        height: 16px;
        width: 200px;
        margin-top: 0.5rem;
        align-self: center;
        

        /* outline-style: solid;
        outline-width: 2px; */
        /* outline-color: aliceblue; */
        overflow: hidden;
        border-radius: 0.3rem;
        box-shadow: 0 0 0 2px var(--nav-border-color);
        -webkit-outline: 2px;
    }

    progress::-webkit-progress-bar {
        background-color: transparent;
        
    }

    :root {
        --progress-color: #aaaaaa
    }



    @media (prefers-color-scheme: dark) {
        :root {
            --progress-color: #68696A
        }
    }	

    progress::-webkit-progress-value {
        background-color: var(--progress-color);
    }

</style>