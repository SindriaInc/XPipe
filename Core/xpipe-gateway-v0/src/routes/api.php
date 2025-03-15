<?php

use Illuminate\Support\Facades\Route;
use App\Helpers\LoadRoutes;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/


// Public Auth Routes
Route::group(['prefix' => 'auth'], function () {

    // Login
    Route::post('login', 'Api\AuthController@login')->name('api.auth.login');

    // Signup
    Route::post('signup', 'Api\AuthController@signup')->name('api.auth.signup');

    // Signup Activation
    Route::get('signup/activate/{token}', 'Api\AuthController@signupActivate')->name('api.auth.signup.activation');

    // Refresh access token
    Route::put('refresh', 'Api\AuthController@refresh')->name('api.auth.refresh');
});


// Private Auth API Routes
Route::group(['middleware' => 'keycloak', 'prefix' => 'auth'], function () {

    // Logout
    Route::get('logout', 'Api\AuthController@logout')->name('api.auth.logout');

    // Logged User
    Route::get('user', 'Api\AuthController@user')->name('api.auth.user');
});


// Private Users API
Route::group(['middleware' => 'keycloak', 'prefix' => 'users'], function () {

    // Get all users
    Route::get('/', 'Api\UsersController@index')->name('api.users');

    // Get a user by username
    Route::post('/show', 'Api\UsersController@showUserByUsername')->name('api.users.showUserByUsername');

    // Get a user by id
    Route::get('/show/{id}', 'Api\UsersController@showUserById')->name('api.users.showUserById');

    // Search users
    Route::post('/search', 'Api\UsersController@search')->name('api.users.search');

    // Store a user
    Route::post('/store', 'Api\UsersController@store')->name('api.users.store');

    // Edit a user
    Route::put('/edit', 'Api\UsersController@edit')->name('api.users.edit');

    // Delete a user
    Route::delete('/delete', 'Api\UsersController@delete')->name('api.users.delete');
});


// Private Swagger API
Route::group(['middleware' => 'keycloak', 'prefix' => 'swagger'], function () {

    // Upload swagger file
    Route::post('/upload', 'Api\SwaggerController@upload')->name('api.swagger.upload');
});

// Private Admin API
Route::group(['middleware' => 'keycloak', 'prefix' => 'admin'], function () {

    // Get all realms with meta
    Route::get('/', 'Api\AdminController@index')->name('api.admin.index');

    // Get all realms
    Route::get('/realms', 'Api\AdminController@realms')->name('api.admin.realms');
});


//// Private Terminal API
//Route::group(['middleware' => 'keycloak', 'prefix' => 'terminal'], function () {
//
//    // Upload swagger file
//    Route::post('/upload', 'Api\TerminalController@upload')->name('api.swagger.upload');
//});


// Micro-services routes
LoadRoutes::loadAllServicesApi();