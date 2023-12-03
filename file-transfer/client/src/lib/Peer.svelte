<script lang="ts">
     export let name: string | null;
     export let uuid: string | null;
</script>

<div>
    <h3>{name}</h3>
    <p>{uuid}</p>
    <label for="file">Send File</label>
    <input type="file"/>
</div>