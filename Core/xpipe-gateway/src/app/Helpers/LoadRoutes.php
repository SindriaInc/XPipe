<?php

namespace App\Helpers;

class LoadRoutes
{

    /**
     * Load all micro-services routes api from generated routes
     *
     * @return mixed|void
     */
    public static function loadAllServicesApi()
    {
        if (file_exists(storage_path('app/generated/generated_routes.php'))) {
            return include storage_path('app/generated/generated_routes.php');
        }
    }


}