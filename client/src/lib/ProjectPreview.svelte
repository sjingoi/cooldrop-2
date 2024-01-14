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
            <h2>{name}</h2>
            <p>{description}</p>
        </div>
    </div>
</a>


<style>
    .container {
        width: 400px;
        overflow: hidden;
        margin: 0;
        padding: 0;
        padding-right: 1rem;
        display: flex;
        flex-direction: row;
        justify-content: left;
        gap: 1rem;
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
</style>