<script>
    import ProjectPreview from "$lib/ProjectPreview.svelte";
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
            <ProjectPreview name="Cooldrop File Transfer" description="Cool WebRTC File Transfer App" url="/" image="https://via.placeholder.com/300x200" />
            <ProjectPreview name="Mall Android App" description="Academic Group Project" url="/" image="https://via.placeholder.com/300x200" />
        </div>
        <div class="project-row">
            <ProjectPreview name="Manual WebRTC Signaler" description="Simple app to test WebRTC" url="/" image="https://via.placeholder.com/300x200" />
            <ProjectPreview name="GPTBot" description="Discord Chat Bot using GPT API" url="/" image="https://via.placeholder.com/300x200" />
        </div>
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
        background-image: url($lib/content.png);
        background-size: cover;
        background-position: center;
        background-blend-mode: multiply;
        background-color: rgba(0, 0, 0, 0);
    }

    .projects {
        display: flex;
        flex-direction: row;
        gap: 16px;
    }

    .project-row {
        display: flex;
        flex-direction: column;
        gap: 16px;
    }

</style>
