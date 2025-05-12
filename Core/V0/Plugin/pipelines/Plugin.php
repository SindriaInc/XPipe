<?php

namespace Sindria\Pipelines;

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

        $ui_screens[] = add_menu_page( 'Pipelines', 'Pipelines', 'read_pipelines', 'pipelines', 'pipelines_router', 'dashicons-beer', 4);

        //$ui_screens[] = add_submenu_page('iam', 'Users', 'Users', 'read', 'users', 'iam_router', 4 );
        //$ui_screens[] = add_submenu_page('iam', 'Policies', 'Policies', 'read', 'policies', 'iam_router', 4 );
    }
}
