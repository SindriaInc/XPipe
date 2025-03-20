<?php

use Illuminate\Http\Request;
//use Sindria\Toolkit\BaseHelper;


///**
// * Load routes from routes.php file into array
// *
// * @return array
// */
//function iam_load_routes(): array
//{
//    return include 'routes.php';
//}

///**
// * Micro MVVM router
// *
// * @return mixed
// */
//function iam_router()
//{
//    $routes = iam_load_routes();
//
//    foreach ($routes['routes'] as $key => $value) {
//
//        if (isset($_GET['page'])) {
//            if ($_GET['page'] == $key) {
//                $controllerAction = explode('@', $value);
//                $controllerClass = $controllerAction[0];
//                $method = $controllerAction[1];
//            }
//        }
//
//        if (isset($_POST['page'])) {
//            if ($_POST['page'] == $key) {
//                $controllerAction = explode('@', $value);
//                $controllerClass = $controllerAction[0];
//                $method = $controllerAction[1];
//            }
//        }
//    }
//
//
//    $controller = new $controllerClass(new \Sindria\Iam\View(), new \Sindria\Iam\Service());
//    return $controller->$method(Request::capture());
//}

/**
 * Init Plugin
 *
 * @return void
 */
function toolkit(): void
{
    if (!session_id()) {
        session_start();
    }

    if (class_exists('\Sindria\Toolkit\Plugin')) {
        $plugin = new \Sindria\Toolkit\Plugin();
    }
}
