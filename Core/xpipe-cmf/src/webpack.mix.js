const mix = require('laravel-mix');

/*
 |--------------------------------------------------------------------------
 | Mix Asset Management
 |--------------------------------------------------------------------------
 |
 | Mix provides a clean, fluent API for defining some Webpack build steps
 | for your Laravel applications. By default, we are compiling the CSS
 | file for the application as well as bundling up all the JS files.
 |
 */

// Default Laravel 8
// mix.js('resources/js/app.js', 'public/js')
//     .postCss('resources/css/app.css', 'public/css', [
//         //
//     ]);

mix.js('resources/js/app.js', 'public/cms/plugins/dashboardtheme/static/js')
    .sass('resources/sass/app.scss', 'public/cms/plugins/dashboardtheme/static/css')
    .copy('node_modules/font-awesome/fonts', 'public/cms/plugins/dashboardtheme/static/fonts');
