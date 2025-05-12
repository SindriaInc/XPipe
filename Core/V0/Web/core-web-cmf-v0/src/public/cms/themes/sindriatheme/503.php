<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="Consegna farmaci a domicilio in gallura">
    <meta name="author" content="Sindria Inc">

    <title>Coming Soon - MyPharm</title>

    <!-- Bootstrap core CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">

    <!-- Custom fonts for this template -->
    <link href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:200,200i,300,300i,400,400i,600,600i,700,700i,900,900i" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css?family=Merriweather:300,300i,400,400i,700,700i,900,900i" rel="stylesheet">

</head>

<body>

<style>

    html{
        height:100%
    }
    body{
        height:100%;
        min-height:35rem;
        position:relative;
        font-family:'Source Sans Pro';
        font-weight:300
    }
    h1,h2,h3,h4,h5,h6{
        font-family:Merriweather;
        font-weight:700
    }
    video{
        position:fixed;
        top:50%;
        left:50%;
        min-width:100%;
        min-height:100%;
        width:auto;
        height:auto;
        -webkit-transform:translateX(-50%) translateY(-50%);
        transform:translateX(-50%) translateY(-50%);
        z-index:0
    }
    @media (pointer:coarse) and (hover:none){
        body{
            background:url(../img/bg-mobile-fallback.jpg) #002e66 no-repeat center center scroll;
            background-position:cover
        }
        body video{
            display:none
        }
    }
    .overlay{
        position:absolute;
        top:0;
        left:0;
        height:100%;
        width:100%;
        background-color:rgba(27, 23, 23, 0.9);
        opacity:.7;
        z-index:1
    }
    .masthead{
        position:relative;
        overflow:hidden;
        padding-bottom:3rem;
        z-index:2
    }
    .masthead .masthead-bg{
        position:absolute;
        top:0;
        bottom:0;
        right:0;
        left:0;
        width:100%;
        min-height:35rem;
        height:100%;
        background-color:#307153;
        -webkit-transform:skewY(4deg);
        transform:skewY(4deg);
        -webkit-transform-origin:bottom right;
        transform-origin:bottom right
    }
    .masthead .masthead-content h1{
        font-size:2.5rem
    }
    .masthead .masthead-content p{
        font-size:1.2rem
    }
    .masthead .masthead-content p strong{
        font-weight:700
    }
    .masthead .masthead-content .input-group-newsletter input{
        height:auto;
        font-size:1rem;
        padding:1rem
    }
    .masthead .masthead-content .input-group-newsletter button{
        font-size:.8rem;
        font-weight:700;
        text-transform:uppercase;
        letter-spacing:1px;
        padding:1rem
    }
    @media (min-width:768px){
        .masthead{
            height:100%;
            min-height:0;
            width:40.5rem;
            padding-bottom:0
        }
        .masthead .masthead-bg{
            min-height:0;
            -webkit-transform:skewX(-8deg);
            transform:skewX(-8deg);
            -webkit-transform-origin:top right;
            transform-origin:top right
        }
        .masthead .masthead-content{
            padding-left:3rem;
            padding-right:10rem
        }
        .masthead .masthead-content h1{
            font-size:3.5rem
        }
        .masthead .masthead-content p{
            font-size:1.3rem
        }
    }
    .social-icons{
        position:absolute;
        margin-bottom:2rem;
        width:100%;
        z-index:2
    }
    .social-icons ul{
        margin-top:2rem;
        width:100%;
        text-align:center
    }
    .social-icons ul>li{
        margin-left:1rem;
        margin-right:1rem;
        display:inline-block
    }
    .social-icons ul>li>a{
        display:block;
        color:#fff;
        background-color:rgba(0,46,102,.8);
        border-radius:100%;
        font-size:2rem;
        line-height:4rem;
        height:4rem;
        width:4rem
    }
    @media (min-width:768px){
        .social-icons{
            margin:0;
            position:absolute;
            right:2.5rem;
            bottom:2rem;
            width:auto
        }
        .social-icons ul{
            margin-top:0;
            width:auto
        }
        .social-icons ul>li{
            display:block;
            margin-left:0;
            margin-right:0;
            margin-bottom:2rem
        }
        .social-icons ul>li:last-child{
            margin-bottom:0
        }
        .social-icons ul>li>a{
            -webkit-transition:all .2s ease-in-out;
            transition:all .2s ease-in-out;
            font-size:2rem;
            line-height:4rem;
            height:4rem;
            width:4rem
        }
        .social-icons ul>li>a:hover{
            background-color:#002e66
        }
    }
    .btn-secondary{
        background-color:#cd9557;
        border-color:#cd9557
    }
    .btn-secondary:active,.btn-secondary:focus,.btn-secondary:hover{
        background-color:#ba7c37!important;
        border-color:#ba7c37!important
    }
    .input{
        font-weight:300!important
    }


</style>

<style>

    .page {
        position: relative;
        overflow: hidden;
    }

    .page .video-bg {
        width: 100%;
        height: 0;
        padding-bottom: 56.25%;/* Aspect ratio */
        position: absolute;
    }

    .page .video-bg iframe {
        border: 0;
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
    }

    .page .content {
        position: relative;
        z-index: 1;
    }

</style>

<div class="page">

    <div style="height: 100vh;" class="content">
        <div class="video-bg">
            <div class="overlay"></div>
            <div id="videoPlayer" data-videoid="FGSqtpYy0oY"></div>
        </div>
        <div class="masthead">
            <div class="masthead-bg"></div>
            <div class="container h-100">
                <div class="row h-100">
                    <div class="col-12 my-auto">
                        <div class="masthead-content text-white py-5 py-md-0">
                            <h1 class="mb-3">Coming Soon!</h1>
                            <p class="mb-5">We're working hard to finish the development of this site. Our target launch date is
                                <strong>2019</strong>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


<!--<video playsinline="playsinline" autoplay="autoplay" muted="muted" loop="loop">-->
<!--    <source src="" wmode="transparent" type="video/mp4">-->
<!--</video>-->







<!-- Bootstrap core JavaScript -->
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

<script type="text/javascript">


    if (!window['YT']) {var YT = {loading: 0,loaded: 0};}if (!window['YTConfig']) {var YTConfig = {'host': 'http://www.youtube.com'};}if (!YT.loading) {YT.loading = 1;(function(){var l = [];YT.ready = function(f) {if (YT.loaded) {f();} else {l.push(f);}};window.onYTReady = function() {YT.loaded = 1;for (var i = 0; i < l.length; i++) {try {l[i]();} catch (e) {}}};YT.setConfig = function(c) {for (var k in c) {if (c.hasOwnProperty(k)) {YTConfig[k] = c[k];}}};var a = document.createElement('script');a.type = 'text/javascript';a.id = 'www-widgetapi-script';a.src = 'https://s.ytimg.com/yts/jsbin/www-widgetapi-vfliGbzFc/www-widgetapi.js';a.async = true;var c = document.currentScript;if (c) {var n = c.nonce || c.getAttribute('nonce');if (n) {a.setAttribute('nonce', n);}}var b = document.getElementsByTagName('script')[0];b.parentNode.insertBefore(a, b);})();}

    jQuery(function () {

        // Youtube player
        window.videoPlayer;

        window.onYouTubeIframeAPIReady = function () {
            var videoPlayerId = $('#videoPlayer').attr('data-videoid');
            window.videoPlayer = new YT.Player('videoPlayer', {
                height: '1080',
                width: '1920',
                videoId: videoPlayerId,
                playerVars: {
                    'controls': 0,
                    'autoplay': 1,
                    'mute': 1,
                    'loop': 1,
                    'showinfo': 0,
                    'modestbranding': 1
                },
                events: {
                    'onReady': onVideoPlayerReady,
                    'onStateChange': onVideoPlayerReady
                }
            });
        }

        function onVideoPlayerReady(event) {
            videoPlayer.playVideo();
        }
    });

</script>

</body>

</html>
