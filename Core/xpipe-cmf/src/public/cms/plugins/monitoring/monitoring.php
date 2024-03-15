<?php
/**
 * @package Monitoring
 */
/*
Plugin Name: Monitoring
Plugin URI: https://sindria.org
Description: Add monitoring integration.
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: monitoring
*/

// Core
require_once 'functions.php';
require_once 'routes.php';
require_once 'Plugin.php';

// MVVM
require_once 'Controller.php';
require_once 'View.php';
require_once 'Helper.php';

// View Models
require_once 'viewmodel/IndexViewModel.php';

// Init plugin
add_action('init', 'monitoring', 9, 0);
