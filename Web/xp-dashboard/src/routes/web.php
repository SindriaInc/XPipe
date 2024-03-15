<?php

use Illuminate\Support\Facades\Route;
use Illuminate\Support\Facades\Auth;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/



// Global group
Route::group(['middleware' => 'locale', 'prefix' => ''], function () {

    // Change locale route
    Route::post('locale', 'LocaleController@changeLocale')->name('locale');

    // Dashboard sidebar toggle
    Route::post('toggle', 'ToggleController@checkToggle')->name('toggle');

    // Dashboard data table pagination quantity
    Route::post('quantity', 'PaginationController@changeQuantity')->name('pagination.quantity');

    Auth::routes(['register' => false]);

    // Auth Routes
    Route::group(['middleware' => 'auth.api', 'prefix' => ''], function () {

        // Dashboard
        Route::get('/', 'Rest\DashboardController@index')->name('dashboard');

        // Pipelines
        Route::group(['prefix' => '/pipelines'], function () {

            Route::get('/', 'Rest\PipelinesController@index')->name('dashboard.pipelines');
            Route::get('/add', 'Rest\PipelinesController@add')->name('dashboard.pipelines.add');
            Route::post('/store', 'Rest\PipelinesController@store')->name('dashboard.pipelines.store');
            Route::get('/details/{username}', 'Rest\PipelinesController@details')->name('dashboard.pipelines.details');
            Route::get('/show/{username}', 'Rest\PipelinesController@show')->name('dashboard.pipelines.show');
            Route::post('/edit/{id}', 'Rest\PipelinesController@edit')->name('dashboard.pipelines.edit');
            Route::post('/destroy/{id}', 'Rest\PipelinesController@destroy')->name('dashboard.pipelines.delete');
            Route::get('/search', 'Rest\PipelinesController@search')->name('dashboard.pipelines.search.get');
            Route::post('/search', 'Rest\PipelinesController@search')->name('dashboard.pipelines.search');
            Route::get('/export', 'Rest\PipelinesController@export')->name('dashboard.pipelines.export');

        });

        // Alerts
        Route::group(['prefix' => '/alerts'], function () {
            Route::get('/', 'Rest\AlertsController@index')->name('dashboard.alerts');
        });

        // Xdev
        Route::group(['prefix' => '/xdev'], function () {
            Route::get('/cli', 'Rest\XdevController@cli')->name('dashboard.xdev.cli');
            Route::get('/gui', 'Rest\XdevController@gui')->name('dashboard.xdev.gui');
        });

        // Users
        Route::group(['prefix' => '/users'], function () {

            Route::get('/', 'Rest\UsersController@index')->name('dashboard.users');
            Route::get('/add', 'Rest\UsersController@add')->name('dashboard.users.add');
            Route::post('/store', 'Rest\UsersController@store')->name('dashboard.users.store');
            Route::get('/details/{username}', 'Rest\UsersController@details')->name('dashboard.users.details');
            Route::get('/show/{username}', 'Rest\UsersController@show')->name('dashboard.users.show');
            Route::post('/edit/{id}', 'Rest\UsersController@edit')->name('dashboard.users.edit');
            Route::post('/destroy/{id}', 'Rest\UsersController@destroy')->name('dashboard.users.delete');
            Route::get('/search', 'Rest\UsersController@search')->name('dashboard.users.search.get');
            Route::post('/search', 'Rest\UsersController@search')->name('dashboard.users.search');
            Route::get('/export', 'Rest\UsersController@export')->name('dashboard.users.export');

        });

        // Policies
        Route::group(['prefix' => '/policies'], function () {

            Route::get('/', 'Rest\PoliciesController@index')->name('dashboard.policies');
            Route::get('/add', 'Rest\PoliciesController@add')->name('dashboard.policies.add');
            Route::post('/store', 'Rest\PoliciesController@store')->name('dashboard.policies.store');
            Route::get('/details/{id}', 'Rest\PoliciesController@details')->name('dashboard.policies.details');
            Route::get('/show/{id}', 'Rest\PoliciesController@show')->name('dashboard.policies.show');
            Route::post('/edit/{id}', 'Rest\PoliciesController@edit')->name('dashboard.policies.edit');
            Route::post('/destroy/{id}', 'Rest\PoliciesController@destroy')->name('dashboard.policies.delete');
            Route::get('/search', 'Rest\PoliciesController@search')->name('dashboard.policies.search.get');
            Route::post('/search', 'Rest\PoliciesController@search')->name('dashboard.policies.search');
            Route::get('/export', 'Rest\PoliciesController@export')->name('dashboard.policies.export');
            Route::get('/attach', 'Rest\PoliciesController@attach')->name('dashboard.policies.attach');
            Route::post('/attach', 'Rest\PoliciesController@attachStore')->name('dashboard.policies.attach.store');
            Route::post('/detach', 'Rest\PoliciesController@detach')->name('dashboard.policies.detach');
        });

//        // Blog
//        Route::group(['prefix' => '/blog'], function () {
//            Route::get('/', 'Rest\BlogController@index')->name('dashboard.blog');
//        });
//
//        // Gallery
//        Route::group(['prefix' => '/gallery'], function () {
//            Route::get('/', 'Rest\GalleryController@index')->name('dashboard.gallery');
//        });
//
//         // Pages
//        Route::group(['prefix' => '/pages'], function () {
//            Route::get('/', 'Rest\PagesController@index')->name('dashboard.pages');
//            Route::get('/structure', 'Rest\PagesController@structure')->name('dashboard.pages.structure');
//        });
//
//        // Cms
//        Route::group(['prefix' => '/cms'], function () {
//            Route::get('/', 'Rest\CmsController@index')->name('dashboard.cms');
//        });

        // Settings
        Route::group(['prefix' => '/settings'], function () {
            Route::get('/', 'Rest\SettingsController@index')->name('dashboard.settings');
            Route::post('/', 'Rest\SettingsController@edit')->name('dashboard.settings.edit');
        });

    });

});
