<script>
    import Peer from "$lib/Peer.svelte";
    /**
     * @type {Array<any>}
     */
    let peers = [
        {
            name: "John",
            uuid: "1234"
        },
        {
            name: "Jane",
            uuid: "5678"
        }
    ];
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