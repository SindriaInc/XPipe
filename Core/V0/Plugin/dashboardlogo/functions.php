<?php

/**
 * Remove comments icon from admin navbar
 */
function dashboardlogo_comments() {
    global $wp_admin_bar;
    $wp_admin_bar->remove_menu('comments');
}


function dashboardlogo($admin_bar) {

    ?>

    <style>
        img#sindria-brand-logo {
            height: 37px;
            position: absolute;
            /*top: 0px;*/
            left: 25px;
        }

        #wpadminbar .ab-top-menu>li.hover>.ab-item, #wpadminbar.nojq .quicklinks .ab-top-menu>li>.ab-item:focus, #wpadminbar:not(.mobile) .ab-top-menu>li:hover>.ab-item, #wpadminbar:not(.mobile) .ab-top-menu>li>.ab-item:focus {
            background: none;
        }

        span#sindria-brand-text {
            margin-left: 45px;
            font-size: 16px;
        }

        /* Responsive */

        @media screen and (max-width: 782px) {

            #wpadminbar .ab-sub-wrapper, #wpadminbar ul, #wpadminbar ul li {
                background: 0 0;
                clear: none;
                list-style: none;
                margin: 0;
                padding: 0;
                position: relative;
                text-indent: 0;
                z-index: 99999;
            }


            #wp-toolbar>ul>li {
                display: block;
            }

            #wp-toolbar>ul>li#wp-admin-bar-lang {
                display: none;
            }

            #wp-toolbar>ul>li#wp-admin-bar-xdev {
                display: none;
            }

            img#sindria-brand-logo {
                margin: 7px 0px 8px 0px;
                left: 0px;
            }

            span#sindria-brand-text {
                margin-left: 30px;
            }


        }

    </style>

    <?php

    $html = "<img id='sindria-brand-logo' src='" . plugin_dir_url( __FILE__ ) . "/static/images/sindria-logo.png' class='sindria-brand-logo' height='50px' alt='".esc_html__(get_bloginfo('name'))."'> <span id='sindria-brand-text'>" . esc_html__(get_bloginfo('name')) . "</span>";

    $args = array(
        'id'        => 'admin_header',
        'title'     =>  $html,
        'parent'    =>  '',
        'href'      =>  home_url(),
        'group'     =>  '',
        'meta'      =>  array(
            'html'      => '',
            'class'     =>  '',
            'rel'       =>  '',
            'lang'      =>  '',
            'dir'       =>  '',
            'onclick'   =>  '',
            'title'     =>  get_bloginfo('name')
        ),
    );

    $admin_bar->remove_node('wp-logo');
    $admin_bar->remove_node('site-name');
    $admin_bar->remove_node('new-content');
    $admin_bar->remove_node('updates');
    $admin_bar->add_node($args);

}
