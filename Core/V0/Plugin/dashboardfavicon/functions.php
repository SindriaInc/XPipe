<?php

function dashboardfavicon() {
    printf('<link href="%s" rel="apple-touch-icon" sizes="57x57" />', plugin_dir_url( __FILE__ ) . 'static/favicon/apple-icon-57x57.png');
    printf('<link href="%s" rel="apple-touch-icon" sizes="60x60" />', plugin_dir_url( __FILE__ ) . 'static/favicon/apple-icon-60x60.png');
    printf('<link href="%s" rel="apple-touch-icon" sizes="72x72" />', plugin_dir_url( __FILE__ ) . 'static/favicon/apple-icon-72x72.png');
    printf('<link href="%s" rel="apple-touch-icon" sizes="76x76" />', plugin_dir_url( __FILE__ ) . 'static/favicon/apple-icon-76x76.png');
    printf('<link href="%s" rel="apple-touch-icon" sizes="114x114" />', plugin_dir_url( __FILE__ ) . 'static/favicon/apple-icon-114x114.png');
    printf('<link href="%s" rel="apple-touch-icon" sizes="120x120" />', plugin_dir_url( __FILE__ ) . 'static/favicon/apple-icon-120x120.png');
    printf('<link href="%s" rel="apple-touch-icon" sizes="144x144" />', plugin_dir_url( __FILE__ ) . 'static/favicon/apple-icon-144x144.png');
    printf('<link href="%s" rel="apple-touch-icon" sizes="152x152" />', plugin_dir_url( __FILE__ ) . 'static/favicon/apple-icon-152x152.png');
    printf('<link href="%s" rel="apple-touch-icon" sizes="180x180" />', plugin_dir_url( __FILE__ ) . 'static/favicon/apple-icon-180x180.png');
    printf('<link href="%s" rel="icon" type="image/png" sizes="192x192" />', plugin_dir_url( __FILE__ ) . 'static/favicon/android-icon-192x192.png');
    printf('<link href="%s" rel="icon" type="image/png" sizes="32x32" />', plugin_dir_url( __FILE__ ) . 'static/favicon/favicon-32x32.png');
    printf('<link href="%s" rel="icon" type="image/png" sizes="96x96" />', plugin_dir_url( __FILE__ ) . 'static/favicon/favicon-96x96.png');
    printf('<link href="%s" rel="icon" type="image/png" sizes="16x16" />', plugin_dir_url( __FILE__ ) . 'static/favicon/favicon-16x16.png');
    printf('<link href="%s" rel="manifest" />', plugin_dir_url( __FILE__ ) . 'static/favicon/manifest.json');
    echo '<meta name="msapplication-TileColor" content="#ffffff" />';
    printf('<meta name="msapplication-TileImage" content="%s" />', plugin_dir_url( __FILE__ ) . 'static/favicon/ms-icon-144x144.png');
    echo '<meta name="theme-color" content="#ffffff" />';
}
