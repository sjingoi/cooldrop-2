<script>
    export let name = "Project";
    export let description = "This is a description of the project.";
    export let url = "/";
    export let image = "https://via.placeholder.com/300x200";
</script>

<a href={url}>
    <div class="container">
        <img class="thumbnail" src={image} alt="Project">
        <div class="text">
            <h3>{name}</h3>
            <p>{description}</p>
        </div>
    </div>
</a>


<style>
    .container {
        text-wrap: nowrap;
        width: max(25vw, 375px);
        overflow: hidden;
        margin: 0;
        padding: 0;
        display: flex;
        flex-direction: row;
        justify-content: left;
        gap: 16px;
        background-color: var(--nav-bg-color);
        border: 2px;
        border-style: solid;
        border-color: var(--nav-border-color);
        border-radius: 8px;
        transition: 100ms;
    }

    .container:hover {
        background-color: var(--nav-bg-color-hover);
    }

    .thumbnail {
        width: 100px;
        height: 100px;
        object-fit: cover;
    }

    a {
        text-decoration: none;
        color: var(--main-text-color);
    }

    h3 {
        font-size: 26px;
        margin: 0px;
        margin-bottom: 8px;
    }

    p {
        margin: 0px;
        font-size: 18px;
        font-weight: normal;
    }

    .text {
        display: flex;
        flex-direction: column;
        justify-content: center;
        gap: 8px;
    }

    
    @media (max-width: 720px) {
        .container {
            width: 100%;
            height: 75px;
            gap: 8px;
        }

        .thumbnail {
            width: 75px;
            height: 75px;
        }

        p {
            margin: 0;
        }

        h3 {
            font-size: 20px;
            margin-top: 12px;
            margin-bottom: 8px;
        }
    }

    @media (max-width: 380px) {
        .thumbnail {
            visibility: collapse;
            width: 0;
        }
    }


</style>