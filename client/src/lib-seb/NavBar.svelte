<script lang="ts">
    let links_height = "0px";
    let links_visibility = "hidden"
    let links_opacity = "0%"
    let padding = "0px"

    export let links: any;
    export let title: string;

    const toggle_ham = () => {
        if (links_visibility === "hidden") {
            links_height = "calc(100vh - 48px)"
            links_visibility = "visible"
            links_opacity = "100%"
            padding = "24px"
            document.body.classList.add('no-scroll');
        } else {
            links_height = "0px"
            links_visibility = "hidden"
            links_opacity = "0%"
            padding = "0px"
            document.body.classList.remove('no-scroll');
        }
    }
</script>

<nav class="topbar">
    <a class="app-title" href="/seb/">{title}</a>
    <div class="links">
        {#each links as entry} 
            <a href={entry.link}>{entry.title}</a>
        {/each}
    </div>
    <button class="ham" on:click={toggle_ham}>≡</button>
</nav>
<div class="spacer"></div>
<li class="links-list" 
    style="visibility: {links_visibility}; height: {links_height}; padding-top: {padding}; padding-bottom: {padding}">
    {#each links as entry} 
        <a style="visibility: {links_visibility}; opacity: {links_opacity}" href={entry.link}>{entry.title}</a> 
    {/each}
</li>

<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">

<style>

    .spacer {
        transition: 500ms;
        height: 48px;
    }

    .topbar {
        height: 48px;
        width: 100%;
        position: fixed;
        top: 0;
        padding: 12px;
        box-sizing: border-box;

        display: flex;
        justify-content: space-between;
        align-items: center;
        
        background-color: var(--nav-bg-color);

        border: 0px;
        border-bottom: 2px;
        border-color: var(--nav-border-color);
        border-style: solid;
        backdrop-filter: blur(48px);
        
        transition: 300ms;
    }

    a {
        font-size: 20px;
        text-decoration: none;
        color: var(--main-text-color);
        margin: 0;
        padding: 0;
        font-weight: normal;
    }

    .app-title {
        font-family: D-DIN-Exp;
        position: relative;
    }

    .links {
        display: flex;
        gap: 2rem;
    }

    .ham {
        all: unset;
        text-align: center;
        font-size: 36px;
        display: none;
        width: 48px;
        height: 48px;
        margin-right: -8px;
    }

    .links-list {
        position: fixed;
        transition: 300ms;
        background-color: var(--nav-bg-color);
        backdrop-filter: blur(16px);
        width: 100vw;
        display: flex;
        flex-direction: column;
        gap: 16px;
        padding: 24px;
        box-sizing: border-box;
        
    }

    .links-list a {
        font-size: 24px;
        transition: 300ms;
    }

    @media (max-width: 720px) {
        .links {
            display: none;
        }

        .ham {
            display: inline;
        }
    }

    :global(.no-scroll) {
        overflow: hidden;
        width: 100vw;
    }
</style>

<slot></slot>