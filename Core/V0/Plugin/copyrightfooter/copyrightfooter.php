<?php
/**
 * @package Copyright Footer
 */
/*
Plugin Name: Copyright Footer
Plugin URI: https://sindria.org
Description: Override wp admin copyright footer
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: copyrightfooter
*/

require_once('functions.php');

// Override Admin footer
add_filter('admin_footer_text', 'sindria_admin_footer');
