<?php
/**
 * @package Clean Dashboard
 */
/*
Plugin Name: Clean Dashboard
Plugin URI: https://sindria.org
Description: This plugin clean wp admin dashboard
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: cleandashboard
*/

require_once('functions.php');

// Clean page title
add_filter('admin_title', 'sindria_admin_title', 10, 2);

// Hide admin toolbar frontend when user logged
add_filter('show_admin_bar', 'sindria_admin_bar');

// Remove screen options and help tabs from dashboard
add_filter('contextual_help', 'sindria_remove_help_tabs', 999, 3 );
add_filter('screen_options_show_screen', '__return_false');

// Register widget area
//add_action('widgets_init', 'sindria_widgets_init');

// Remove entire sidebar wp-admin
//add_action('admin_head', 'sindria_sidebar');

// Hide the Toolbar(adminbar) in the back-end using CSS
//add_action('admin_head', 'sindria_adminbar_dashboard');

// Hide update notifications
add_filter('pre_site_transient_update_core','sindria_core_updates'); //hide updates for WordPress itself
add_filter('pre_site_transient_update_plugins','sindria_core_updates'); //hide updates for all plugins
add_filter('pre_site_transient_update_themes','sindria_core_updates'); //hide updates for all themes

// Add notification area at the top of the dashboard
add_action('admin_notices', 'sindria_dashboard_notifications');


// customize admin bar css
function override_admin_bar_css() {

    ?>


        <style type="text/css">

            @media screen and (max-width: 782px)
                #wp-toolbar > ul > li {
                    /*display: flex;*/
                }

        </style>

    <?php

}

// on backend area
add_action( 'admin_head', 'override_admin_bar_css' );
