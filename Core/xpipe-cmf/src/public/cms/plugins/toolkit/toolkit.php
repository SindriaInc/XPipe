<?php
/**
 * @package Toolkit
 */
/*
Plugin Name: Toolkit
Plugin URI: https://sindria.org
Description: This plugin contain all abstract features needed by all others modules.
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: toolkit
*/

//// WP_List_Table is not loaded automatically so we need to load it in our application
//if( ! class_exists( 'WP_List_Table' ) ) {
//    require_once( ABSPATH . 'wp-admin/includes/class-wp-list-table.php' );
//}

// Core
require_once 'functions.php';
//require_once 'routes.php';
require_once 'Plugin.php';

// MVVM
//require_once 'BaseController.php';
//require_once 'BaseView.php';
require_once 'BaseService.php';
require_once 'BaseHelper.php';

// ViewModel
//require_once 'viewmodel/ViewModel.php';


// Init plugin
add_action('init', 'toolkit', 9, 0);
