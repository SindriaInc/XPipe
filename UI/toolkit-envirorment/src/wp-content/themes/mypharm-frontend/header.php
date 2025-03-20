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
<!--    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">-->
<!--    <link href="/static/css/lib/font-awesome.min.css" rel="stylesheet">-->

    <!-- Custom fonts for this template -->
    <link href="https://fonts.googleapis.com/css?family=Raleway:100,100i,200,200i,300,300i,400,400i,500,500i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css?family=Lora:400,400i,700,700i" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="<?= get_bloginfo('template_directory'); ?>/static/css/form.css" rel="stylesheet" type="text/css">
    <link href="<?= get_bloginfo('template_directory'); ?>/static/css/add-product.css" rel="stylesheet" type="text/css">


    <?php wp_head();?>
</head>

<body>

<!--<h1 class="site-heading text-center text-white d-none d-lg-block">-->
<!--    <span class="site-heading-upper text-description mb-3">--><?php //= get_bloginfo( 'description' ); ?><!--</span>-->
<!--    <span class="site-heading-lower">--><?php //= get_bloginfo( 'name' ); ?><!--</span>-->
<!--</h1>-->


<div class="content-wrapper">
