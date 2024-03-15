<?php

/**
 * Override login logo image
 */
function sindria_login_logo() { ?>
    <style type="text/css">
        #login h1 a, .login h1 a {
            background-image: url(<?= plugin_dir_url( __FILE__ ) . '/static/images/sindria-logo.png' ?>);
            padding-bottom: 18px;
            background-size: contain;
            min-height: 134px;
            min-width: 270px;
        }
    </style>
<?php }


/**
 * Override Login Logo Link URL
 */
if ( !function_exists('sindria_login_logo_url') ) {
    function sindria_login_logo_url() {
        return home_url();
    }
}


/**
 * Override Login Logo's Title
 */
if ( !function_exists('sindria_login_logo_title') ) {
    function sindria_login_logo_title( $headertext ) {
        $headertext = esc_html__( get_bloginfo('name'), 'plugin-textdomain' );
        return $headertext;
    }
}
