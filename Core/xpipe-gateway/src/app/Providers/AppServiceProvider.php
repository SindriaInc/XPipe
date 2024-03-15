<?php

namespace App\Providers;

use Illuminate\Support\Collection;
use Illuminate\Support\Facades\Schema;
use Illuminate\Support\ServiceProvider;
use Illuminate\Support\Str;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Bootstrap any application services.
     *
     * @return void
     */
    public function boot()
    {
        // Fix migration error 1071
        Schema::defaultStringLength(191);

        //if (env('APP_ENV') !== 'local') {
            $this->app['request']->server->set('HTTPS', true);
        //}

        /**
         * Add getPublicRoutes method on the fly in collection object
         */
        Collection::macro('getPublicRoutes', function () {
            return $this->map(function ($value) {
                if ($value->getIsPublic()) {
                 return $value;
                }
                return false;
            });
        });

        /**
         * Add getPrivateRoutes method on the fly in collection object
         */
        Collection::macro('getPrivateRoutes', function () {
            return $this->map(function ($value) {
                if (! $value->getIsPublic()) {
                    return $value;
                }
                return false;
            });
        });
    }

    /**
     * Register any application services.
     *
     * @return void
     */
    public function register()
    {
    }
}
