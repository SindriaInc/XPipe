<?php

/**
 * Override wp-admin footer signature
 */
function sindria_change_footer_version() {
    $version = file_get_contents(plugin_dir_path( __FILE__ ) . 'data/version.txt');
    return 'Version ' . $version;
}
