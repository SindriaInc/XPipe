<?php

namespace Sindria\Iam;

class Plugin
{
    /**
     *  Plugin constructor
     *
     *  It associates some custom functions with the corresponding functions in the wordpress stream,
     *  through the WP function "add_action".
     *
     *  @see https://developer.wordpress.org/reference/functions/add_action/    Official Documentation
     *
     */
    function __construct()
    {
        add_action('admin_menu', array($this, 'createMenu'), 10, 0);
    }

    /**
     *  Create the "File Manager" Menu Item
     *
     *  Create the "File Manager" Menu Item, returning the page suffix, through the WP function "add_menu_page".
     *
     *  @See https://developer.wordpress.org/reference/functions/add_menu_page/ Official Documentation
     *
     * @return void
     */
    function createMenu() : void
    {
        /**
         * Global $ui_screens
         *
         * Defines the views within which to queue the js and css scripts
         * @global array $ui_screens string[]
         */
        global $ui_screens;

        // IAM
        $ui_screens[] = add_menu_page( 'IAM', 'IAM', 'read_users', 'iam', 'iam_router', 'dashicons-privacy', 4);

        // Users
        $ui_screens[] = add_submenu_page('iam', trans('iam.users.title'), trans('iam.users.title'), 'read_users', 'iam', 'iam_router', 4 );
        // Users Search
        $ui_screens[] = add_submenu_page(null, 'Users Search', 'Users Search', 'read_users', 'users-search', 'iam_router', 4 );
        // Users Export
        $ui_screens[] = add_submenu_page(null, 'Users Export', 'Users Export', 'read_users', 'users-export', 'iam_router', 4 );
        // Details User
        $ui_screens[] = add_submenu_page(null, 'Details User', 'Details User', 'read_users', 'details-user', 'iam_router', 4 );
        // Add User
        $ui_screens[] = add_submenu_page(null, 'Add User', 'Add User', 'write_users', 'add-user', 'iam_router', 4 );
        // Store User
        $ui_screens[] = add_submenu_page(null, 'Store User', 'Store User', 'write_users', 'store-user', 'iam_router', 4 );
        // Show User
        $ui_screens[] = add_submenu_page(null, 'Show User', 'Show User', 'write_users', 'show-user', 'iam_router', 4 );
        // Edit User
        $ui_screens[] = add_submenu_page(null, 'Edit User', 'Edit User', 'write_users', 'edit-user', 'iam_router', 4 );
        // Delete User
        $ui_screens[] = add_submenu_page(null, 'Delete User', 'Delete User', 'admin_users', 'delete-user', 'iam_router', 4 );

        // Manage Policies - Users view
        $ui_screens[] = add_submenu_page(null, 'Manage Policies', 'Manage Policies', 'read_policies', 'manage-policies', 'iam_router', 4 );


        // Policies
        $ui_screens[] = add_submenu_page('iam', trans('iam.policies.title'), trans('iam.policies.title'), 'read_policies', 'policies', 'iam_router', 4 );
        // Policies Search
        $ui_screens[] = add_submenu_page(null, 'Policies Search', 'Policies Search', 'read_policies', 'policies-search', 'iam_router', 4 );
        // Policies Export
        $ui_screens[] = add_submenu_page(null, 'Policies Export', 'Policies Export', 'read_policies', 'policies-export', 'iam_router', 4 );
        // Details Policy
        $ui_screens[] = add_submenu_page(null, 'Details Policy', 'Details Policy', 'read_policies', 'details-policy', 'iam_router', 4 );
        // Add Policy
        $ui_screens[] = add_submenu_page(null, 'Add Policy', 'Add Policy', 'write_policies', 'add-policy', 'iam_router', 4 );
        // Store Policy
        $ui_screens[] = add_submenu_page(null, 'Store Policy', 'Store Policy', 'write_policies', 'store-policy', 'iam_router', 4 );
        // Show Policy
        $ui_screens[] = add_submenu_page(null, 'Show Policy', 'Show Policy', 'write_policies', 'show-policy', 'iam_router', 4 );
        // Edit Policy
        $ui_screens[] = add_submenu_page(null, 'Edit Policy', 'Edit Policy', 'write_policies', 'edit-policy', 'iam_router', 4 );
        // Delete Policy
        $ui_screens[] = add_submenu_page(null, 'Delete Policy', 'Delete Policy', 'admin_policies', 'delete-policy', 'iam_router', 4 );
        // Attach Policy
        $ui_screens[] = add_submenu_page(null, 'Attach Policy', 'Attach Policy', 'admin_policies', 'attach-policy', 'iam_router', 4 );
        // Attach Store Policy
        $ui_screens[] = add_submenu_page(null, 'Attach Store Policy', 'Attach Store Policy', 'admin_policies', 'attach-store', 'iam_router', 4 );
        // Detach Policy
        $ui_screens[] = add_submenu_page(null, 'Detach Policy', 'Detach Policy', 'admin_policies', 'detach-policy', 'iam_router', 4 );
        // Detach Store Policy
        $ui_screens[] = add_submenu_page(null, 'Detach Store Policy', 'Detach Store Policy', 'admin_policies', 'detach-store', 'iam_router', 4 );
    }
}
