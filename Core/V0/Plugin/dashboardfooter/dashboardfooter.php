<?php
/**
 * @package Dashboard Footer
 */
/*
Plugin Name: Dashboard Footer
Plugin URI: https://sindria.org
Description: Add links on wp-admin footer
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: dashboardfooter
*/

require_once('functions.php');


add_action('admin_footer', 'dashboardfooter_links');
