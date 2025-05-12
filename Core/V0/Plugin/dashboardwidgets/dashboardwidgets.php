<?php
/**
 * @package Dashboard Widgets
 */
/*
Plugin Name: Dashboard Widgets
Plugin URI: https://sindria.org
Description: This plugin remove default widgets box and add sindria default custom widgets.
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: dashboardwidgets
*/

require_once('functions.php');

// Remove all default dashboard widgets box
add_action('wp_dashboard_setup', 'dashboardwidgets_remove_default_box');

// Add default sindria widgets
add_action('wp_dashboard_setup', 'dashboardwidgets_widgets');



// Prevent users from putting anything to first column
// as widget order set by user overrides everything
add_filter(
    'get_user_option_meta-box-order_dashboard',
    function($result, $option, $user) {
        // Force custom widget to 1st column
        if ( ! empty( $result['normal'] ) ) {
            $result['normal'] = 'sindria_analytics_widget';
        }
        return $result;
    },
    10,
    3
);






// Style dashboard widget columns
add_action(
    'admin_head',
    function() {
        echo "<style type='text/css'>
            #dashboard-widgets .postbox-container {width: 33.333333%;}
            #dashboard-widgets #postbox-container-1 {width: 100%;}
        </style>";
    }
);



//// Dashboard widget reordering
//add_action( 'wp_dashboard_setup', function() {
//
//    global $wp_meta_boxes;
//
//    // Move all dashboard wigets from 1st to 2nd column
//    if ( ! empty( $wp_meta_boxes['dashboard']['normal'] ) ) {
//        if ( isset($wp_meta_boxes['dashboard']['side']) ) {
//            $wp_meta_boxes['dashboard']['side'] = array_merge_recursive(
//                $wp_meta_boxes['dashboard']['side'],
//                $wp_meta_boxes['dashboard']['normal']
//            );
//        } else {
//            $wp_meta_boxes['dashboard']['side'] = $wp_meta_boxes['dashboard']['normal'];
//        }
//        unset( $wp_meta_boxes['dashboard']['normal'] );
//    }
//
////    // Add custom dashbboard widget.
////    add_meta_box( 'dashboard_widget_example',
////        __( 'Example Widget', 'example-text-domain' ),
////        'render_example_widget',
////        'dashboard',
////        'normal',
////        'default'
////    );
//
//} );

function render_example_widget() {
    ?>
    <p>Do something.</p>
    <?php
}








// TMP

// Remove all dashboard widgets
//add_action('wp_dashboard_setup', 'cleandashboard_dashboard_widgets', 9999 );

///**
// * Remove all dashboard widgets
// */
//function cleandashboard_dashboard_widgets() {
//    global $wp_meta_boxes;
//
//    //dd($wp_meta_boxes);
//
//    $wp_meta_boxes['dashboard']['normal']['core'] = array();
//    $wp_meta_boxes['dashboard']['side']['core'] = array();
//}


//// remove all default widgets
//function wdv_remove_default_widgets() {
//    unregister_widget('WP_Widget_Media_Gallery');
//    unregister_widget('WP_Widget_Pages');
//    unregister_widget('WP_Widget_Calendar');
//    unregister_widget('WP_Widget_Archives');
//    unregister_widget('WP_Widget_Links');
//    unregister_widget('WP_Widget_Meta');
//    unregister_widget('WP_Widget_Search');
//    unregister_widget('WP_Widget_Text');
//    unregister_widget('WP_Widget_Categories');
//    unregister_widget('WP_Widget_Recent_Posts');
//    unregister_widget('WP_Widget_Recent_Comments');
//    unregister_widget('WP_Widget_RSS');
//    unregister_widget('WP_Widget_Tag_Cloud');
//    unregister_widget('WP_Nav_Menu_Widget');
//    unregister_widget('Twenty_Eleven_Ephemera_Widget');
//    unregister_widget('WP_Widget_Media_Audio');
//    unregister_widget('WP_Widget_Media_Image');
//    unregister_widget('WP_Widget_Media_Video');
//    unregister_widget('WP_Widget_Custom_HTML');
//}
//

//add_action('widgets_init', 'wdv_remove_default_widgets', 11);

//remove_action( 'init', 'wp_widgets_init', 1 );
