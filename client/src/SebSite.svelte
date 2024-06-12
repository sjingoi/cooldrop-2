<script>

    import NavBar from "./lib-seb/NavBar.svelte";
    import AboutMeRow from "./lib-seb/AboutMeRow.svelte";
    import ProjectsRow from "./lib-seb/ProjectsRow.svelte";
    import FeaturedProject from "./lib-seb/FeaturedProject.svelte";

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
        <p>Here you can find everything about me and the things I have worked on.</p>
        <div style="height: 20vh;"></div>
    </div>
</div>

<AboutMeRow/>

<FeaturedProject/>

<ProjectsRow/>


<style>

    p {
        text-align: center;
    }

    .hero {
        margin: 0;
        padding-top: 48px;
        width: 100%;
        background-image: url("./lib-seb/seb-galaxy1-rotated.jpg");
        background-size: cover;
        background-position: center;            
        background-blend-mode: multiply;
        height: calc(100vh - 96px);
        display: flex;
        flex-direction: column;
        justify-content: center;
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
            /* height: 50vh; */
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
