<script>
    import ProjectPreview from "./ProjectPreview.svelte";
    import mallapp from "./mallapp.png"
    import cooldrop1 from "./cooldrop1.png"
    import webrtclogo from "./webrtclogo.png"
    import gptbot from "./gptbot.png"
    import hero from "./hero.png"
</script>

<div class="content">
    <div style="padding-bottom: 5%;">
        <h2>All Projects</h2>
        <p style="text-align: left">Both personal and academic projects.</p>
    </div>
    <div class="projects">
        <div class="project-row">
            <ProjectPreview name="Cooldrop File Transfer" description="Cool WebRTC Based File Transfer App" url="https://github.com/sjingoi/cooldrop-2" image={cooldrop1} />
            <ProjectPreview name="Mall Android App" description="Academic Group Project" url="https://github.com/sjingoi/B07Group7Project" image={mallapp} />
        </div>
        <div class="project-row">
            <ProjectPreview name="Manual WebRTC Signaler" description="Simple app to test WebRTC" url="https://github.com/sjingoi/webrtc-tester" image={webrtclogo} />
            <ProjectPreview name="GPTBot" description="Discord Chat Bot using GPT API" url="https://github.com/sjingoi/ai-discord-bot" image={gptbot} />
        </div>
        <a class="see-more" href="https://github.com/sjingoi/">See more on Github →</a>
    </div>
</div>

<style>

    h2 {
        letter-spacing: 1px;
        text-align: left;
        font-size: 32px;
        font-weight: 700;
    }

    .content {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        margin: 0;
        padding: 5% 5% 5% 5%;
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

    @media (max-width: 1280px) {
        .content {
            flex-direction: column;
        }
    }



    @media (max-width: 900px) {

        .project-row {
            flex-direction: column;
        }

        .content {
            flex-direction: column;
        }

        .hero h1 {
            font-size: 42px;
        }


    }
</style>