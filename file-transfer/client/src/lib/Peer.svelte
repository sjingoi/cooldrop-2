<script lang="ts">
    import type { FormEventHandler } from "svelte/elements";
    import type { PeerConnection } from "./PeerConnection";

    export let peer: PeerConnection;

    let files: FileList;

    $: if (files) {
        console.log(files);
    }

    function handleFile(event: any) {
        console.log(event.name);
    }
</script>

<div>
    <h3>{peer.name}</h3>
    <p>{peer.uuid}</p>
    <label for="file">Send File</label>
    <input bind:files type="file" multiple/>
</div>