<!doctype html>
<html lang="{{ app()->getLocale() }}">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- CSRF Token -->
    <meta name="csrf-token" content="{{ csrf_token() }}">

    <title>Home | {{ config('app.name') }}</title>

    <!-- Fonts -->
    <link href="https://fonts.googleapis.com/css?family=Raleway:100,600" rel="stylesheet" type="text/css">

    <!-- Styles -->
    <link href="{{ asset('css/app.css') }}" rel="stylesheet">

    <!-- Styles -->
    <style>
        html, body {
            background-color: #fff;
            color: #636b6f;
            font-family: 'Raleway', sans-serif;
            font-weight: 100;
            height: 100vh;
            margin: 0;
        }

        .full-height {
            height: 100vh;
        }

        .flex-center {
            align-items: center;
            display: flex;
            justify-content: center;
        }

        .position-ref {
            position: relative;
        }

        .top-right {
            position: absolute;
            right: 10px;
            top: 18px;
        }

        .content {
            text-align: center;
        }

        .title {
            font-size: 84px;
        }

        .links > a {
            color: #636b6f;
            padding: 0 25px;
            font-size: 12px;
            font-weight: 600;
            letter-spacing: .1rem;
            text-decoration: none;
            text-transform: uppercase;
        }

        .versioninfo {
            color: #636b6f;
            padding: 0 25px;
            font-size: 12px;
            font-weight: 600;
            letter-spacing: .1rem;
            text-decoration: none;
        }

        .framwork_title {
            font-weight: 600;
            padding-top: 20px;
        }


        .m-b-md {
            margin-bottom: 30px;
        }
    </style>
</head>
<body>

{{-- Styles --}}
<style>
    html, body {
        background-color: #f2f2f2;
        color: #333;
        font-family: 'Raleway', sans-serif;
        font-weight: 100;
        height: 100vh;
        margin: 0;
    }

    .full-height {
        /*height: 100vh;*/
    }

    .flex-center {
        align-items: center;
        display: flex;
        justify-content: center;
    }

    .position-ref {
        position: relative;
    }

    .content {
        text-align: center;
    }

    .title {
        font-size: 84px;
    }

    .subtitle {
        font-size: 42px;
    }

    @media(max-width: 1024px) {
        .title {
            font-size: 64px;
        }

        .subtitle {
            font-size: 32px;
        }
    }

    @media(max-width: 768px) {
        .title {
            font-size: 42px;
        }

        .subtitle {
            font-size: 21px;
        }
    }

    #logo {
        display: block;
        margin-left: auto;
        margin-right: auto;
    }

    .contacts {
        color: #fff;
    }
</style>


<div class="flex-center position-ref full-height">
    <div class="content">
        <div class="title">
            Sito in manutenzione
        </div>
        <div class="subtitle">

        </div>
    </div>
</div>


<!-- Scripts -->
<script src="{{ asset('js/app.js') }}"></script>

</body>
</html>






