<nav class="topbar">
    <a class="app-title" href="/">Cooldrop</a>
    <div class="links">
        <a href="/seb/">Home</a>
        <a href="https://github.com/sjingoi/cooldrop-2">Github</a>
        <a href="mailto:sebi.jingoi@gmail.com">Contact</a>
    </div>
</nav>

<style>

    .topbar {
        position: sticky;
        top: 0;
        justify-content: space-between;
        display: flex;
        background-color: var(--nav-bg-color);
        padding: 12px;
        border: 0;
        border-bottom: 2px;
        border-style: solid;
        border-color: var(--nav-border-color);
        backdrop-filter: blur(64px);
    }

    a {
        font-size: 20px;
        text-decoration: none;
        color: var(--main-text-color);
        margin: 0;
        padding: 0;
    }

    .app-title {
        font-family: D-DIN-Exp;
        position: relative;
    }

    .links {
        display: flex;
        gap: 2rem;
    }
</style>

<slot></slot>