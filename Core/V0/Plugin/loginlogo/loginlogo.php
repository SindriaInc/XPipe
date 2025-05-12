<?php
/**
 * @package Login Logo
 */
/*
Plugin Name: Login Logo
Plugin URI: https://sindria.org
Description: Override wp login logo
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: loginlogo
*/

require_once('functions.php');

// Override login logo
add_action( 'login_enqueue_scripts', 'sindria_login_logo' );

// Override Login Logo Link URL
add_filter( 'login_headerurl', 'sindria_login_logo_url' );

// Override Login Logo's Title
add_filter( 'login_headertext', 'sindria_login_logo_title' );
