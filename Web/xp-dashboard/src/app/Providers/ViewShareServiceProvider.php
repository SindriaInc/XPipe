<?php

namespace App\Providers;

use Illuminate\Support\ServiceProvider;
use Illuminate\Support\Facades\View;

class ViewShareServiceProvider extends ServiceProvider
{
    /**
     * Bootstrap services.
     *
     * @return void
     */
    public function boot()
    {
        // Link sidebar
        View::share('sidebar', [
            'dashboard'                   => ['key' => 'sidebar.dashboard',        'icon' => 'dashboard'],
            //'dashboard.blog'              => ['key' => 'sidebar.blog',             'icon' => 'newspaper-o'],
            //'dashboard.gallery'           => ['key' => 'sidebar.gallery',          'icon' => 'photo'],
            //'dashboard.pages'             => ['key' => 'sidebar.pages',            'icon' => 'file-word-o'],
            //'dashboard.pages.structure'   => ['key' => 'sidebar.pages.structure',  'icon' => 'wrench'],
            //'dashboard.alerts'            => ['key' => 'sidebar.alerts',            'icon' => 'bell'],
            'dashboard.pipelines'         => ['key' => 'sidebar.pipelines',         'icon' => 'rocket'],
            'dashboard.users'             => ['key' => 'sidebar.users',             'icon' => 'users'],
            'dashboard.policies'          => ['key' => 'sidebar.policies',          'icon' => 'wrench'],
            'dashboard.settings'          => ['key' => 'sidebar.settings',          'icon' => 'cog'],
        ]);

        // Link sidebar only admin
        View::share('sidebar_admin', [
            //'admin.cms'       => ['key' => 'sidebar.cms',       'icon' => 'list'],
            //'admin.settings'  => ['key' => 'sidebar.settings',  'icon' => 'cog'],
        ]);


        // Link subscribers
        View::share('subscribers', [
            // Subscribers
            //'dashboard.subscribers.export'  => ['key' => 'customers.add',  'icon' => 'user','color' => 'grey'],
            //'dashboard.subscribers.list' => ['key' => 'customers.list', 'icon' => 'list','color' => 'grey'],
            //'dashboard.subscribers.delete' => ['key' => 'customers.delete', 'icon' => 'user',       'color' => 'danger'],
        ]);



        //View::share('settings', []);


    }

    /**
     * Register services.
     *
     * @return void
     */
    public function register()
    {
        //
    }
}
