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

    // Default auth routes
    //Auth::routes(['register' => false]);

    // Login
    Route::get('/auth/login', 'Auth\LoginController@login');

    // Logout
    Route::get('/auth/logout', 'Auth\LoginController@logout');

    // Default home route
    //Route::get('/home', [App\Http\Controllers\HomeController::class, 'index'])->name('home');

    Route::any('/', function () {
        return redirect('_');
    });

    Route::get('/notifications/test', 'Ajax\NotificationsController@test')->name('ajax.cms.notifications.test');

    // Ajax routes
    Route::group(['middleware' => 'auth', 'prefix' => '/ajax'], function () {

        // Notifications Routes
        Route::group(['prefix' => '/notifications'], function () {
            Route::get('/', 'Ajax\NotificationsController@index')->name('ajax.cms.notifications');
            Route::get('/markasread', 'Ajax\NotificationsController@markAsRead')->name('ajax.cms.notifications.markasread');
            Route::get('/markasunread', 'Ajax\NotificationsController@markAsUnread')->name('ajax.cms.notifications.markasunread');

            //Route::get('/test', 'Ajax\NotificationsController@test')->name('ajax.cms.notifications.test');
        });

    });



//    // File Manager Export Routes
//    Route::group(['prefix' => '/filemanager'], function () {
//        Route::get('/export/files', 'Client\ExportController@exportFiles')->name('client.cms.filemanager.export.files');
//        Route::get('/export/points', 'Client\ExportController@exportPoints')->name('client.cms.filemanager.export.points');
//    });


});
