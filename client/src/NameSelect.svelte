<script>
    import NavBar from "./lib-seb/NavBar.svelte";

    let nav_links = [{
            title: "Home",
            link: "/seb/"
        },
        {
            title: "Github",
            link: "https://github.com/sjingoi/cooldrop-2"
        },
        {
            title: "Contact",
            link: "mailto:sebi.jingoi@gmail.com"
        },
    ]

    let name = "";

    const submitName = () => {
        if (name.trim() !== ""){
            localStorage.setItem("name", name.trim());
            window.location.href = "/";
        }
    }



</script>


<NavBar title="Cooldrop 2 (beta)" links={nav_links}/>
<div class="name-select">
    <h1>Welcome to Cooldrop</h1>
    <p>To get started, please specify a name that will help others identify your device:</p>
    <input type='text' class='name-input' bind:value={name} on:keydown={(e) => {if (e.key === 'Enter') submitName()}}>
    <hr style="width: 10rem">
    <button class='submit-button' id='submit-name-btn' on:click={submitName} disabled={name.trim() === ""}>Submit</button>
</div>

<style> 

    h1 {
        letter-spacing: 2px;
        text-align: center;
        font-size: 48px;
        font-weight: 700;
    }

    p {
        text-align: center;
    }

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

        border: none;
        overflow: auto;
        outline: none;

        -webkit-box-shadow: none;
        -moz-box-shadow: none;
        box-shadow: none;

        resize: none;
    }

    .name-select {
        margin-top: 15vh;
        padding: 16px;
        display: flex;
        align-items: center;
        flex-direction: column;
        box-sizing: border-box;
    }

    .submit-button:disabled:hover {
        background-color: var(--nav-bg-color);
        cursor: not-allowed;
        
    }
</style>