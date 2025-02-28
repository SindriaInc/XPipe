<!DOCTYPE html>
<html <?php language_attributes(); ?>>

<head>

    <meta charset="<?php bloginfo( 'charset' ); ?>">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="<?= get_bloginfo( 'description' ); ?>">
    <meta name="author" content="Sindria Inc">

    <title><?= get_bloginfo( 'name' ); ?> | <?= get_bloginfo( 'description' ); ?></title>

    <!-- Favicon -->
    <link rel="shortcut icon" href="<?= get_bloginfo('template_directory'); ?>/static/img/favicon.png">

    <!-- Bootstrap core CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <link href="<?= get_bloginfo('template_directory'); ?>/static/css/lib/font-awesome.min.css" rel="stylesheet">

    <!-- Custom fonts for this template -->
    <link href="https://fonts.googleapis.com/css?family=Raleway:100,100i,200,200i,300,300i,400,400i,500,500i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css?family=Lora:400,400i,700,700i" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="<?= get_bloginfo('template_directory'); ?>/static/css/main.css" rel="stylesheet" type="text/css">


    <?php wp_head();?>
</head>

<body>

<h1 class="site-heading text-center text-white d-none d-lg-block">
    <span class="site-heading-upper text-description mb-3"><?= get_bloginfo( 'description' ); ?></span>
    <span class="site-heading-lower"><?= get_bloginfo( 'name' ); ?></span>
</h1>

<!-- Navigation -->
<nav class="navbar navbar-expand-lg navbar-dark py-lg-4" id="mainNav">
    <div class="container">
        <a class="navbar-brand text-uppercase text-expanded font-weight-bold d-lg-none" href="<?= get_bloginfo( 'wpurl' );?>"><?= get_bloginfo( 'name' ); ?></a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarResponsive">
            <ul class="navbar-nav mx-auto">
                <li class="nav-item active px-lg-4">
                    <a class="nav-link text-uppercase text-expanded" href="<?= get_bloginfo( 'wpurl' );?>">Home
                        <span class="sr-only">(current)</span>
                    </a>
                </li>
                <?php //wp_list_pages( '&title_li=' ); ?>
                <li class="nav-item px-lg-4">
                    <a class="nav-link text-uppercase text-expanded" href="<?= get_bloginfo( 'wpurl' );?>/about">Chi siamo</a>
                </li>
                <li class="nav-item px-lg-4">
                    <a class="nav-link text-uppercase text-expanded" href="<?= get_bloginfo( 'wpurl' );?>/services">Servizi</a>
                </li>
                <li class="nav-item px-lg-4">
                    <a class="nav-link text-uppercase text-expanded" href="<?= get_bloginfo( 'wpurl' );?>/contacts">Contatti</a>
                </li>
                <!--<li class="nav-item px-lg-4">
                    <a class="nav-link text-uppercase text-expanded" data-toggle="modal" data-target="#loginModal" href="#">Login/Register</a>
                </li>-->
            </ul>
        </div>
    </div>
</nav>

<div class="content-wrapper">