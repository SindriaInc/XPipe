<?php
/**
 * @package Dashboard Operator
 */
/*
Plugin Name: Dashboard Operator
Plugin URI: https://sindria.org
Description: Create dashboard operator role with capabilities.
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: dashboardoperator
*/

use Illuminate\Support\Facades\Http;

/**
 * Create operator function
 *
 * Create a new user role named "operator" and assign capabilities to him
 * @see https://developer.wordpress.org/reference/functions/add_role/
 *
 * @return void
 */
function createOperator()
{
    remove_role( 'operator' );
    add_role('operator', 'Operator', array(
        //'admin_filemanager'         => true,
        'read'                      => true,
        //'edit_plugin'               => true,
        //'activate_plugins'          => true,
        //'edit_files'                => true,
        //'edit_users'                => true,
        //'upload_files'              => true,
        //'manage_options'            => true,
        //'edit_pages'                => true,
        //'edit_posts'                => true,
        //'edit_private_pages'        => true,
        //'edit_private_posts'        => true,
        //'edit_published_pages'      => true,
        //'edit_published_posts'      => true,
        //'publish_posts'             => true,
        //'read_private_pages'        => true,
        //'read_private_posts'        => true,
    ) );
};

add_action('init', 'createOperator');




function createPolicy()
{
    remove_role( 'policy' );
    add_role('policy', 'Policy', array(
        //'admin_filemanager'         => true,
        'read'                      => true,
        //'edit_plugin'               => true,
        //'activate_plugins'          => true,
        //'edit_files'                => true,
        //'edit_users'                => true,
        //'upload_files'              => true,
        //'manage_options'            => true,
        //'edit_pages'                => true,
        //'edit_posts'                => true,
        //'edit_private_pages'        => true,
        //'edit_private_posts'        => true,
        //'edit_published_pages'      => true,
        //'edit_published_posts'      => true,
        //'publish_posts'             => true,
        //'read_private_pages'        => true,
        //'read_private_posts'        => true,
    ) );
};

add_action('init', 'createPolicy');

/**
 * Add capability to Admin
 *
 * Assign capability 'admin_filemanager' to administrators
 * @see https://developer.wordpress.org/reference/classes/wp_role/add_cap/
 *
 * @return void
 *
 */
function addCapabilitiesToAdmin() {
    $role = get_role('administrator');
    $role->add_cap('read_analytics', true);
    $role->add_cap('xdev_cli', true);
    $role->add_cap('xdev_gui', true);
    $role->add_cap('read_policies', true);
    $role->add_cap('write_policies', true);
    $role->add_cap('admin_policies', true);
    $role->add_cap('read_users', true);
    $role->add_cap('write_users', true);
    $role->add_cap('admin_users', true);

}
add_action( 'init', 'addCapabilitiesToAdmin', 11 );

/**
 * Remove bundle content
 *
 * Remove unnecessary content such as menu items, navbar elements and dashboard meta boxes for 'operators' user role.
 * @see https://developer.wordpress.org/reference/functions/remove_menu_page/
 * @see https://developer.wordpress.org/reference/functions/show_admin_bar/
 * @see https://developer.wordpress.org/reference/functions/remove_meta_box/
 *
 * @return void
 */
function removeBundleContent() {
    if (current_user_can('admin_filemanager') && !is_super_admin()) {
            remove_menu_page('edit.php');
            remove_menu_page('upload.php');
            remove_menu_page('plugins.php');
            remove_menu_page('tools.php');
            remove_menu_page('profile.php');
            remove_menu_page('import.php');
            remove_menu_page('edit.php?post_type=page');
            remove_menu_page('edit-comments.php');
            remove_menu_page('export-personal-data.php');

            //show_admin_bar(false);
            //remove_meta_box( 'dashboard_incoming_links', 'dashboard', 'normal' );
            //remove_meta_box( 'dashboard_plugins', 'dashboard', 'normal' );
            //remove_meta_box( 'dashboard_primary', 'dashboard', 'side' );
            //remove_meta_box( 'dashboard_secondary', 'dashboard', 'normal' );
            //remove_meta_box( 'dashboard_quick_press', 'dashboard', 'side' );
            //remove_meta_box( 'dashboard_recent_drafts', 'dashboard', 'side' );
            //remove_meta_box( 'dashboard_activity', 'dashboard', 'normal');
        }
}
//add_action('admin_menu', 'removeBundleContent',10);



//add_action('ssokeycloak_login_success_after_user_session', 'dashboardoperator_set_default_policies');

function dashboardoperator_set_default_policies() {

    $user_id = get_current_user_id();
    $hasUuid = metadata_exists( 'user', $user_id, 'uuid');

    if ($hasUuid) {

        $uuid = get_user_meta($user_id, 'uuid', true);

        $token = current_access_token();
        $url = api_gateway_url() . '/api/v1/policies/default/attach/' . $uuid;
        $response = Http::withToken($token)->get($url);
        $result = $response->body();
        $resource = json_decode($result);

        if ($resource->success) {

        }

    }

}



add_action('ssokeycloak_login_success_before_redirect', 'dashboardoperator_get_capabilities');

function dashboardoperator_get_capabilities() {

    $user_id = get_current_user_id();

    $hasUuid = metadata_exists( 'user', $user_id, 'uuid');

    if ($hasUuid) {

        $uuid = get_user_meta($user_id, 'uuid', true);

        $token = current_access_token();
        $url = api_gateway_url() . '/api/v1/policies/capabilities/user/' . $uuid;
        $response = Http::withToken($token)->get($url);
        $result = $response->body();
        $resource = json_decode($result);

        if ($resource->success) {

            $user = new WP_User($user_id);

            // Remove existing capabilities
            $user->remove_all_caps();

            // Set updated caps with idempotence
            foreach ($resource->data->capabilities as $capability) {
                $user->remove_cap($capability);
                $user->add_cap($capability);
            }

        }

    }

}
