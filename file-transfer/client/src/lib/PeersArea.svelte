<script lang="ts">
    import Peer from "$lib/Peer.svelte";
    import type { PeerConnection } from "./PeerConnection";
    /**
     * @type {Array<any>}
     */
    export let peers: PeerConnection[];
</script>

<div>
    {#each peers as peer}
        <Peer name={peer.name} uuid={peer.uuid} />
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