<?php

namespace App\Providers;

use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     *
     * @return void
     */
    public function register()
    {
        //
    }

    /**
     * Bootstrap any application services.
     *
     * @return void
     */
    public function boot()
    {
        // Fix HTTPS production
        if (env('APP_ENV') !== 'local') {
            $this->app['request']->server->set('HTTPS', true);
        }

        // Fix url() and asset() url generation to match APP_URL (laravel core)
        $urlGenerator = $this->app['url'];
        $root = env('APP_URL');
        $urlGenerator->forceRootUrl($root);

        // Setup session default values
        session()->put('toggled', true);
        session()->put('quantity', 5);
    }
}
