<nav class="topbar">
    <a class="app-title" href="/">Sebastian Jingoi</a>
    <div class="links">
        <a href="/ft">Cooldrop</a>
        <a href="/about">About</a>
        <a href="https://github.com/sjingoi/">Github</a>
        <a href="mailto:sebi.jingoi@gmail.com">Contact</a>
    </div>
</nav>

<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">

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