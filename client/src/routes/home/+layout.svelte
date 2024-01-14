<nav class="topbar">
    <a class="app-title" href="/">Sebastian Jingoi</a>
    <div class="links">
        <a href="/about">About</a>
        <a href="/contact">Posts</a>
        <a href="/contact">Videos</a>
        <a href="/contact">Projects</a>
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