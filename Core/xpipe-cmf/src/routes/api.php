<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

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

//Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
//    return $request->user();
//});

// API V1 Routes
Route::group(['prefix' => '/v1'], function () {

    // API Routes
    Route::group(['prefix' => '/cms'], function () {

//        // File Manager API
//        Route::group(['prefix' => '/filemanager'], function () {
//            Route::get('/', 'Api\FileManagerController@index')->name('api.v1.cms.filemanager');
//            Route::get('/last', 'Api\FileManagerController@last')->name('api.v1.cms.filemanager.last');
//            Route::get('/meta', 'Api\FileManagerController@meta')->name('api.v1.cms.filemanager.meta');
//            Route::post('/audio/store', 'Api\FileManagerController@storeAudio')->name('api.v1.cms.filemanager.audio.store');
//            Route::post('/video/store', 'Api\FileManagerController@storeVideo')->name('api.v1.cms.filemanager.video.store');
//            Route::post('/image/store', 'Api\FileManagerController@storeImage')->name('api.v1.cms.filemanager.image.store');
//            Route::post('/attachment/store', 'Api\FileManagerController@storeAttachment')->name('api.v1.cms.filemanager.attachment.store');
//            Route::get('/show/{id}', 'Api\FileManagerController@show')->name('api.v1.cms.filemanager.show');
//            Route::put('/edit/{id}', 'Api\FileManagerController@edit')->name('api.v1.cms.filemanager.edit');
//            Route::delete('/delete/{id}', 'Api\FileManagerController@delete')->name('api.v1.cms.filemanager.delete');
//            Route::get('/test', 'Api\FileManagerController@test')->name('api.v1.cms.filemanager.test');
//
//
//            // File Manager Settings API
//            Route::group(['prefix' => '/settings'], function () {
//                Route::post('/import/points/upload', 'Api\FileManagerSettingsController@importPointsUpload')->name('api.v1.cms.filemanager.settings.import.points.upload');
//                Route::get('/import/points', 'Api\FileManagerSettingsController@importPoints')->name('api.v1.cms.filemanager.settings.import.points');
//            });
//
//            Route::group(['prefix' => '/helpers'], function() {
//                Route::group(['prefix' => '/filesystem'], function() {
//                    Route::post('meta', 'Api\FileManagerHelpersController@meta')->name('api.v1.cms.filemanager.helpers.filesystem.meta');
//                });
//            });
//
//        });

    });

});
