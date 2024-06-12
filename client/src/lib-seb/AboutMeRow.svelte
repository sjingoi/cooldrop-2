<script>
    import SebPortrait from "./Sebastian.jpg"
</script>

<div class="content">
    <img class="portrait-img" src={SebPortrait}>
    <div class="about-text">
        <h2>About Me</h2>
        <p>
            Hi! My name is Sebastian. I am a software developer who is
            passionate about making software that is useful and intuitive to
            use. I am currently studying Computer Science and Software
            Engineering at the University of Toronto.
        </p>
        <p>
            From a very young age, I have been fascinated by the magic of computers.
            Now, I get the opportunity to create software magic myself, and
            to understand the intricacies of software design.
        </p>
        <p>
            Apart from software development, my interests are Skiing, 
            Photography, and Astronomy. The milky way photo above was taken by
            me, roughly 2 hours north-east of Toronto.
        </p>
    </div>
</div>

<style>
    .portrait-img {
        width: 200px;
        height: 200px;
        border-radius: 50%;
    }
    .content {
        display: flex;
        flex-direction: row;
        gap: 5%;
        /* justify-content: space-between; */
        margin: 0;
        padding: 5% 5% 5% 5%;
        background-size: cover;
        background-position: center;
        background-blend-mode: multiply;
        background-color: rgba(0, 0, 0, 0);
    }

    .about-text {
        width: 1000px;
    }

    p {
        line-height: 22px;
    }

    @media (max-width: 560px) {
        .content {
            padding-top: 20%;
            flex-direction: column;
        }
        .about-text {
            width: auto;
        }
        .portrait-img {
            align-self: center;
        }
    }

</style>