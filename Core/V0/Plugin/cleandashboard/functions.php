<?php

/**
 * Clean page title without WordPress string
 *
 * @param $admin_title
 * @param $title
 * @return string
 */
function sindria_admin_title($admin_title, $title)
{
    return get_bloginfo('name').' &bull; '.$title;
}

/**
 * Hide admin toolbar frontend when user logged
 *
 * @return bool
 */
function sindria_admin_bar() {
    return false;
}

/**
 * Remove screen options and help tabs from dashboard
 *
 * @param $old_help
 * @param $screen_id
 * @param $screen
 * @return mixed
 */
function sindria_remove_help_tabs($old_help, $screen_id, $screen){
    $screen->remove_help_tabs();
    return $old_help;
}


/**
 * Register widget area
 *
 */
function sindria_widgets_init() {
   register_sidebar(array());
}


/**
 * Remove entire sidebar wp-admin
 */
function sindria_sidebar(){
    if(!current_user_can('administrator')) {
        echo <<<HTML
        <style type="text/css">
        #wpcontent, #footer { margin-left: 0px; }
        </style>
        <script type="text/javascript">
        jQuery(document).ready( function($) {
            $('#adminmenuback, #adminmenuwrap').remove();
        });
        </script>
HTML;
    }
}


/**
 * Hide the Toolbar(adminbar) in the back-end using CSS
 *
 * @version WP 4.8
 * Read more {@link https://codex.wordpress.org/Roles_and_Capabilities#Capabilities}
 *           {@link https://codex.wordpress.org/Function_Reference/is_admin}
 *           {@link https://codex.wordpress.org/Function_Reference/current_user_can}
 */
function sindria_adminbar_dashboard() {
    if(!current_user_can('administrator')) {
        ?>
        <style>
            #wpadminbar {
                display: none;
            }
            #wpwrap {
                top: -30px;/** change to own preference */
            }
        </style>
        <?php
    }
}


/**
 * Hide update notifications
 *
 * @return object
 */
function sindria_core_updates() {
    global $wp_version;return(object) array('last_checked'=> time(),'version_checked'=> $wp_version,);
}

/**
 * Includes the notifications in the dashboard
 *
 * @return void
 */
function sindria_dashboard_notifications() : void
{
    if ('dashboard' === get_current_screen()->base) {
        include_once WP_PLUGIN_DIR . '/cleandashboard/view/components/messages.php';
    }
}
