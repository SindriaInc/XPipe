<?php

use Illuminate\Http\Request;
use Sindria\Monitoring\Helper;


/**
 * Load routes from routes.php file into array
 *
 * @return array
 */
function monitoring_load_routes(): array
{
    return include 'routes.php';
}

/**
 * Micro MVVM router
 *
 * @return mixed
 */
function monitoring_router()
{
    $routes = monitoring_load_routes();

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


    $controller = new $controllerClass(new \Sindria\Monitoring\View());
    return $controller->$method(Request::capture());
}

/**
 * Init Plugin
 *
 * @return void
 */
function monitoring(): void
{
    if (class_exists('\Sindria\Monitoring\Plugin')) {
        $plugin = new \Sindria\Monitoring\Plugin();
    }
}
