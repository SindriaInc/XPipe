<?php
/**
 * @package Version Footer
 */
/*
Plugin Name: Version Footer
Plugin URI: https://sindria.org
Description: Override wp admin version footer
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: versionfooter
*/

require_once('functions.php');

// Override WP Admin version footer
add_filter( 'update_footer', 'sindria_change_footer_version', 9999 );
