<?php
/**
 * @package Pipelines
 */
/*
Plugin Name: Pipelines
Plugin URI: https://sindria.org
Description: Add pipelines integration.
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: pipelines
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
add_action('init', 'pipelines', 9, 0);
