<script>
    import NavBar from "./lib-ft/NavBar.svelte";

    let name = "";

    const submitName = () => {
        localStorage.setItem("name", name.trim());
        window.location.href = "/";
    }



</script>


<NavBar/>
<div class="name-select">
    <h1>Welcome to Cooldrop</h1>
    <p>To get started, please specify a name that will help others identify your device:</p>
    <input type='text' class='name-input' bind:value={name}>
    <hr style="width: 10rem">
    <button class='submit-button' id='submit-name-btn' on:click={submitName} disabled={name.trim() === ""}>Submit</button>
</div>

<style> 
    .submit-button {

        font-size: 16px;
        width: 180px;
        height: 60px;
        margin: 16px;
        padding: 0;
        background-color: var(--nav-bg-color);
        border: 2px;
        border-style: solid;
        border-color: var(--nav-border-color);
        border-radius: 8px;
        transition: 100ms;

    }

    .submit-button:hover {
        background-color: var(--nav-bg-color-hover);
    }

    button {
        font-family: D-DIN-Bold;
    }

    .name-input {
        text-decoration: none;
        align-self: center;
        margin-top: 0.5rem;
        background-color: transparent;
        text-align: center;
        font-family: D-DIN-Regular;
        font-weight: 500;
        transition: 500ms;

        

        font-size: 36px;
        height: 70px;
        width: 90%;
        color: white;

        border: none;
        overflow: auto;
        outline: none;

        -webkit-box-shadow: none;
        -moz-box-shadow: none;
        box-shadow: none;

        resize: none;
    }

    .name-select {
        display: flex;
        align-items: center;
        flex-direction: column;
    }
</style>