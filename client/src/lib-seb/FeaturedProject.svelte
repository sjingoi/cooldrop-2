<script>
    import CooldropAllDevices from "./product-screenshots/All Devices.png"
    import GooglePlay from "./coming-soon-playstore.png"
</script>

<div class="content">
    <h1>Featured Project: Cooldrop</h1>
    <p>The fastest way to send files to any device at any location.</p>
    <div class="buttons">
        <a href="https://github.com/sjingoi/cooldrop-2">Learn More</a>
        <a href="https://cooldrop.cc">Try it out &gt</a>
    </div>
    <!-- <img style="width: 150px; align-self: center" src={GooglePlay}/> -->
    <img class="project-img" src={CooldropAllDevices}>
</div>

<style>
    .project-img {
        width: 100%;
    }

    .content {
        display: flex;
        flex-direction: column;
        /* justify-content: space-between; */
        justify-content: center;
        margin: 0;
        padding: 5% 5% 5% 5%;
        background-size: cover;
        background-position: center;
        background-blend-mode: multiply;
        background-color: var(--nav-bg-color);
    }

    h1 {
        letter-spacing: normal;
        margin-bottom: 0;
    }

    p {
        text-align: center;
    }

    .buttons {
        display: flex;
        flex-direction: row;
        justify-content:center;
        /* margin-bottom: 32px; */
        gap: 16px;
    }

    
    @media (max-width: 720px) {
        h1 {
            font-size: 36px;
        }

    }
</style>