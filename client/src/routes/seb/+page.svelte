<script>
    import ProjectPreview from "$lib/ProjectPreview.svelte";
    import mallapp from "./mallapp.png"
    import cooldrop1 from "./cooldrop1.png"
    import webrtclogo from "./webrtclogo.png"
    import gptbot from "./gptbot.png"
</script>

<div class="hero">
    <h1>Welcome to CoolDrop</h1>
    <p>Here you can find all the cool stuff I've created or worked on.</p>
</div>

<div class="content">
    <div>
        <h2>Projects</h2>
        <p>Here you can find all the cool stuff I've created or worked on.</p>
    </div>
    <div class="projects">
        <div class="project-row">
            <ProjectPreview name="Cooldrop File Transfer" description="Cool WebRTC Based File Transfer App" url="https://github.com/sjingoi/cooldrop" image={cooldrop1} />
            <ProjectPreview name="Mall Android App" description="Academic Group Project" url="https://github.com/sjingoi/B07Group7Project" image={mallapp} />
        </div>
        <div class="project-row">
            <ProjectPreview name="Manual WebRTC Signaler" description="Simple app to test WebRTC" url="/" image={webrtclogo} />
            <ProjectPreview name="GPTBot" description="Discord Chat Bot using GPT API" url="/" image={gptbot} />
        </div>
        <a class="see-more" href="https://github.com/sjingoi/">See more on Github →</a>
    </div>
</div>

<style>

    h1 {
        letter-spacing: 2px;
        text-align: center;
        font-size: 48px;
        font-weight: 700;
    }

    h2 {
        letter-spacing: 1px;
        text-align: left;
        font-size: 32px;
        font-weight: 700;
    }

    p {
        text-align: center;
    }

    .hero {
        margin: 0;
        padding-top: 10%;
        padding-bottom: 10%;
        width: 100%;
        background-image: url($lib/hero.png);
        background-size: cover;
        background-position: center;            
        background-blend-mode: multiply;
    }

    .hero h1 {
        color: white;
    }

    .hero p {
        color: white;
    }

    @media (prefers-color-scheme: dark) {
        .hero {
            background-color: rgba(0, 0, 0, 0.25);
        }
    }

    .content {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        margin: 0;
        padding: 5% 5% 100% 5%;
        background-size: cover;
        background-position: center;
        background-blend-mode: multiply;
        background-color: rgba(0, 0, 0, 0);
    }

    .projects {
        display: flex;
        flex-direction: column;
        gap: 16px;
    }

    .project-row {
        display: flex;
        flex-direction: row;
        gap: 16px;
    }

    .see-more {
        color: var(--sub-text-color);
        text-align: center;
        text-decoration: none;
        transition: 100ms;
    }

    .see-more:hover {
        color: var(--main-text-color);
    }

</style>
