<?php

/**
 * Remove all default dashboard widgets box
 */
function dashboardwidgets_remove_default_box() {
    remove_meta_box('dashboard_site_health', 'dashboard', 'side');   // Site Health
    remove_meta_box('dashboard_site_health', 'dashboard', 'normal');   // Site Health
    remove_meta_box('dashboard_right_now', 'dashboard', 'side');   // Right Now
    remove_meta_box('dashboard_right_now', 'dashboard', 'normal');   // Right Now
    remove_meta_box('dashboard_activity', 'dashboard', 'side');   // Activity
    remove_meta_box('dashboard_activity', 'dashboard', 'normal');   // Activity
    remove_meta_box('dashboard_recent_comments', 'dashboard', 'side'); // Recent Comments
    remove_meta_box('dashboard_recent_comments', 'dashboard', 'normal'); // Recent Comments
    remove_meta_box('dashboard_incoming_links', 'dashboard', 'side');  // Incoming Links
    remove_meta_box('dashboard_incoming_links', 'dashboard', 'normal');  // Incoming Links
    remove_meta_box('dashboard_plugins', 'dashboard', 'side');   // Plugins
    remove_meta_box('dashboard_plugins', 'dashboard', 'normal');   // Plugins
    remove_meta_box('dashboard_quick_press', 'dashboard', 'side');  // Quick Press
    remove_meta_box('dashboard_quick_press', 'dashboard', 'normal');  // Quick Press
    remove_meta_box('dashboard_recent_drafts', 'dashboard', 'side');  // Recent Drafts
    remove_meta_box('dashboard_recent_drafts', 'dashboard', 'normal');  // Recent Drafts
    remove_meta_box('dashboard_primary', 'dashboard', 'side');   // WordPress blog
    remove_meta_box('dashboard_primary', 'dashboard', 'normal');   // WordPress blog
    remove_meta_box('dashboard_secondary', 'dashboard', 'side');   // Other WordPress News
    remove_meta_box('dashboard_secondary', 'dashboard', 'normal');   // Other WordPress News
    // use 'dashboard-network' as the second parameter to remove widgets from a network dashboard.
}

function dashboardwidgets_widgets() {
    global $wp_meta_boxes;

    wp_add_dashboard_widget('sindria_analytics_widget', 'Analytics', 'sindria_dashboard_analitycs');
}

function sindria_dashboard_analitycs() {

    $check = dashboardwidgets_analitycs_check_capabilities();

    if ($check) {
        echo '<iframe plausible-embed src="https://plausible.io/share/local-demo-xpipe.sindria.org?auth=4Cjn0p4LVqnJ89LS5nrvq&embed=true&theme=light" scrolling="no" frameborder="0" loading="lazy" style="width: 1px; min-width: 100%; height: 1600px;"></iframe><script async src="https://plausible.io/js/embed.host.js"></script>';
    } else {
        echo '<p id="sindria_analytics_widget_message">You don\'t have permission to read data</p>';
    }

}

/**
 * @return bool
 */
function dashboardwidgets_analitycs_check_capabilities() {
    $user_id = get_current_user_id();
    $user = new WP_User($user_id);
    $current_capabilities = $user->caps;

    foreach ($current_capabilities as $key => $value) {
        if ($key == 'read_analytics') {
            return true;
        }
    }

    return false;
}
