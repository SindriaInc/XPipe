<?php

use Illuminate\Http\Request;
use Sindria\Xdev\Helper;

/**
 * Adds the "Xdev" item.
 *
 * @param WP_Admin_Bar $wp_admin_bar The WP_Admin_Bar instance.
 */
function wp_admin_bar_xdev_item($wp_admin_bar) {

    ?>

    <style>

        #wp-toolbar>ul>li#wp-admin-bar-xdev {
            margin: 8px 0px 0px 0px;
        }

        #sindria-xdev-icon {
            border: none;
            background-color: transparent;
            color: #666;
            margin-top: 12px;
        }

        /* Desktop only */
        @media screen and (min-width: 783px) {

            #wpadminbar .quicklinks #sindria-xdev-icon  {
                padding: calc( (var(--bar-height) - 32px)/2 ) 10px !important;
            }

        }


    </style>

    <?php

    $html = <<<EOF
    <i id="sindria-xdev-icon" class="dashicons dashicons-before dashicons-cloud"></i>
    EOF;

    $class = '';

    // Default disabled
    //$cli_url = cms_dashboard_page_route('xdev-cli');

    $wp_admin_bar->add_node(
        array(
            'id'     => 'xdev',
            'parent' => 'top-secondary',
            'title'  => $html,
            'href'   => '',
            'meta'   => array(
                'class' => $class,
            ),
        )
    );

}

/**
 * Adds the "Xdev" submenu items.
 *
 * @param WP_Admin_Bar $wp_admin_bar The WP_Admin_Bar instance.
 */
function wp_admin_bar_xdev_menu($wp_admin_bar) {

    // Submenu group
    $wp_admin_bar->add_group(
        array(
            'parent' => 'xdev',
            'id'     => 'xdev-actions',
        )
    );


    // Submenu Xdev Cli item
    $cli_url = cms_dashboard_page_route('xdev-cli');

    $wp_admin_bar->add_node(
        array(
            'parent' => 'xdev-actions',
            'id'     => 'cli',
            'title'  => 'Xdev Cli',
            'href'   => $cli_url,
        )
    );


    // Submenu Xdev Gui item
    $gui_url = cms_dashboard_page_route('xdev-gui');

    $wp_admin_bar->add_node(
        array(
            'parent' => 'xdev-actions',
            'id'     => 'gui',
            'title'  => 'Xdev Gui',
            'href'   => $gui_url,
        )
    );

}


/**
 * @param WP_Admin_Bar $wp_admin_bar The WP_Admin_Bar instance.
 * @return void
 */
function xdev_remove_menu($wp_admin_bar) {

    $check = xdev_check_capabilities();

    if ( ! $check ) {
        $wp_admin_bar->remove_node( 'xdev' );
    }
}

/**
 * @return bool
 */
function xdev_check_capabilities() {

    $user_id = get_current_user_id();
    $user = new WP_User($user_id);
    $current_capabilities = $user->caps;

    foreach ($current_capabilities as $key => $value) {
        if ($key == 'xdev_cli' || $key == 'xdev_gui') {
            return true;
        }
    }

    return false;
}


/**
 * Load routes from routes.php file into array
 *
 * @return array
 */
function xdev_load_routes(): array
{
    return include 'routes.php';
}

/**
 * Micro MVVM router
 *
 * @return mixed
 */
function xdev_router()
{
    $routes = xdev_load_routes();

    foreach ($routes['routes'] as $key => $value) {

        if (isset($_GET['page'])) {
            if ($_GET['page'] == $key) {
                $controllerAction = explode('@', $value);
                $controllerClass = $controllerAction[0];
                $method = $controllerAction[1];
            }
        }

        if (isset($_POST['page'])) {
            if ($_POST['page'] == $key) {
                $controllerAction = explode('@', $value);
                $controllerClass = $controllerAction[0];
                $method = $controllerAction[1];
            }
        }
    }


    $controller = new $controllerClass(new \Sindria\Xdev\View());
    return $controller->$method(Request::capture());
}

/**
 * Init Plugin
 *
 * @return void
 */
function xdev(): void
{
    if (class_exists('\Sindria\Xdev\Plugin')) {
        $plugin = new \Sindria\Xdev\Plugin();
    }
}
