<?php


if ( ! function_exists( 'mypharm_frontend_setup' ) ) {
    /**
     * Sets up theme defaults and registers support for various WordPress features.
     *
     * Note that this function is hooked into the after_setup_theme hook, which
     * runs before the init hook. The init hook is too late for some features, such
     * as indicating support for post thumbnails.
     */
    function mypharm_frontend_setup() {
        /*
         * Make theme available for translation.
         * Translations can be filed in the /languages/ directory.
         * If you're building a theme based on Twenty Nineteen, use a find and replace
         * to change 'twentynineteen' to the name of your theme in all the template files.
         */
        load_theme_textdomain( 'mypharm-frontend', get_template_directory() . '/languages' );


        /*
         * Enable support for Post Thumbnails on posts and pages.
         *
         * @link https://developer.wordpress.org/themes/functionality/featured-images-post-thumbnails/
         */
        add_theme_support( 'post-thumbnails' );
        set_post_thumbnail_size( 1568, 9999 );


        /*
         * Switch default core markup for search form, comment form, and comments
         * to output valid HTML5.
         */
        add_theme_support('html5');


        // Add support for full and wide align images.
        add_theme_support( 'align-wide' );

    }
}
add_action( 'after_setup_theme', 'mypharm_frontend_setup' );



if ( ! function_exists( 'get_post_id_by_name' )) {
    /**
     * Get post id using slug url name
     *
     * @param $slug
     * @param string $post_type
     * @return mixed|WP_Post
     */
    function get_post_id_by_name( $slug, $post_type = "post" ) {
        $query = new WP_Query(
            array(
                'name'        => $slug,
                'post_type'   => $post_type,
                'numberposts' => 1,
                'fields'      => 'ids',
            ) );
        $posts = $query->get_posts();
        return array_shift( $posts );
    }
}



