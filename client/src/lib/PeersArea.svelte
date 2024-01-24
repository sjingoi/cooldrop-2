<script lang="ts">
    import Peer from "$lib/Peer.svelte";
    import type { FilePeerConnection } from "./PeerConnection";
    /**
     * @type {Array<any>}
     */
    export let peers: FilePeerConnection[];
</script>

<div>
    {#each peers as peer}
        <Peer peer={peer} />
    {/each}
</div>

<style>
    div {
        display: flex;
        flex-direction: row;
        align-items: center;
        gap: 1rem;
    }
</style>