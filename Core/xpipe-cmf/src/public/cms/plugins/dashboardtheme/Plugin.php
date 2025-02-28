<?php

namespace Sindria\DashboardTheme;

class Plugin
{
    /**
     * Property $ui_screens
     * Defines ID
     * Defines the views within which to queue the js and css scripts
     * @var array $ui_screens string[]
     */
    private $ui_screens = array();

    /**
     * Plugin constructor
     *
     * It associates some custom functions with the corresponding functions in the wordpress stream,
     * through the WP function "add_action".
     *
     *  @see https://developer.wordpress.org/reference/functions/add_action/    Official Documentation
     *
     */
    public function __construct()
    {
        //add_filter('admin_head', array($this, 'registerStyles'), 10);
        //add_action('wp_enqueue_scripts', [ $this, 'maybe_register_assets' ]);
        add_action( 'admin_enqueue_scripts', [ $this, 'registerAssets' ] );
    }

    /**
     *  Register and enqueue scripts and styles
     *
     *  Register and enqueue scripts and styles to printout in selected views through some WP functions.
     *
     *  @see https://developer.wordpress.org/reference/functions/wp_register_script/    Official Documentation
     *  @see https://developer.wordpress.org/reference/functions/wp_register_style/     Official Documentation
     *  @see https://developer.wordpress.org/reference/functions/wp_enqueue_script/     Official Documentation
     *  @see https://developer.wordpress.org/reference/functions/wp_enqueue_style/      Official Documentation
     *
     * @return void
     */
    public function registerStyles() : void
    {
        wp_register_style('dashboardtheme_app_css', plugin_dir_url( __FILE__ ) . '/static/css/app.css');
    }

    public function registerScripts() : void
    {
        wp_register_script('dashboardtheme_app_js', plugin_dir_url( __FILE__ ) . '/static/js/app.js');
    }

    public function registerAssets() : void
    {
        //wp_enqueue_style( 'dashboardtheme_app', plugin_dir_url( __FILE__ ) . '/static/css/style.css', '','', 'all' );

        //wp_enqueue_style( 'dashboardtheme_app', plugin_dir_url( __FILE__ ) . '/static/css/app.css', '','', 'all' );
        wp_enqueue_script( 'dashboardtheme_app', plugin_dir_url( __FILE__ ) . '/static/js/app.js', '', '', true );
    }

    /**
     * Remove styles and scripts before the garbage collector erase
     *
     * @return void
     */
    public function __destruct()
    {
        wp_dequeue_script('dashboardtheme_app');
        //wp_dequeue_style('dashboardtheme_app');
    }
}
