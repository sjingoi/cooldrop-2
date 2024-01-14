<script lang="ts">
    import type { FormEventHandler } from "svelte/elements";
    import type { FilePeerConnection, PeerConnection } from "./PeerConnection";

    export let peer: FilePeerConnection;

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
</div>