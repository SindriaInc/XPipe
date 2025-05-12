<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>{{ config('app.name') }} | Service Unavailable</title>

        <link rel="stylesheet" href="https://sindria.org/css/app.css" type="text/css">

        <link rel="icon" type="image/png" href="https://sindria.org/icons/favicon-32x32.png" sizes="32x32">

        <link rel="icon" type="image/png" href="https://sindria.org/icons/favicon-16x16.png" sizes="16x16">

        <link rel="shortcut icon" href="https://sindria.org/icons/favicon.ico">

        <meta name="msapplication-config" content="https://sindria.org/icons/browserconfig.xml">

        <meta name="theme-color" content="#ffffff">
        <!-- Fonts -->
        <link href="https://fonts.googleapis.com/css?family=Raleway:100,600" rel="stylesheet" type="text/css">

        
    </head>

    <body>

        {{-- Styles --}}
        <style>
            html, body {
                background-color: #333;
                color: #f2f2f2;
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
                    <h2>Welcome to {{ config('app.name') }}<br />WEBSITE & REMOTE SUPPORT</h2>
                    <h3>Contact us</h3>
                    <p class="contacts">Email: <a class="contacts" href="mailto:support@sindria.org">support@sindria.org</a><br />
                    Telegram: <a class="contacts" href="tg://resolve?domain=sindriasupport">@sindriasupport</a></p>
                </div>
            </div>

        <div class="flex-center position-ref full-height">
            <div class="content">
                <img id="logo" src="img/logo.png">
                <a title="Secure Payment" href="https://paypal.me/lucapitzoi"><img id="logo" src="img/paypal.png"></a>
                <div class="title">
                   Maintenance mode on
                </div>
                <div class="subtitle">
                   
                </div>
            </div>
        </div>
    </body>
</html>

