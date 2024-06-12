<script>
    import ProjectPreview from "./lib-seb/ProjectPreview.svelte";
    import mallapp from "./lib-seb/mallapp.png"
    import cooldrop1 from "./lib-seb/cooldrop1.png"
    import webrtclogo from "./lib-seb/webrtclogo.png"
    import gptbot from "./lib-seb/gptbot.png"
    import hero from "./lib-seb/hero.png"
    import NavBar from "./lib-seb/NavBar.svelte";

    let links = [{ title: "Cooldrop", link: "/"}, 
                 { title: "About", link: "/seb"},
                 { title: "Github", link: "https://github.com/sjingoi/"}, 
                 { title: "Contact", link: "mailto:sebi.jingoi@gmail.com"}, 
                ]
</script>

<NavBar title="Sebastian Jingoi" links={links}/>

<div class="hero">
    <div class="hero-content">
        <h1>Sebastian Jingoi</h1>
        <p>Here you can find all the cool stuff I've created or worked on.</p>
    </div>
</div>

<div class="content">
    <div style="padding-bottom: 5%;">
        <h2>Projects</h2>
        <p style="text-align: left">Both personal and academic projects.</p>
    </div>
    <div class="projects">
        <div class="project-row">
            <ProjectPreview name="Cooldrop File Transfer" description="Cool WebRTC Based File Transfer App" url="https://github.com/sjingoi/cooldrop" image={cooldrop1} />
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
        background-image: url(./lib-seb/hero.png);
        background-size: cover;
        background-position: center;            
        background-blend-mode: multiply;
    }

    .hero h1 {
        color: white;
        padding-left: 5%;
        padding-right: 5%;
    }

    .hero p {
        color: white;
        padding-left: 5%;
        padding-right: 5%;
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
        
        .projects {
            width: fit-content;
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

    @media (max-width: 720px) {
        .hero {
            background-image: url("./lib-seb/seb-galaxy1.jpg");
            height: 50vh;
            display: flex;
            flex-direction: row;
            align-content: center;
            align-items: center;
        }

        .hero-content {
            width: 100%;
            align-items: center;
        }
        .projects {
            width: auto;
        }

    }

    @media (max-width: 500px) {
        
    }



</style>
