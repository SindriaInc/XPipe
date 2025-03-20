<?php


// WP_List_Table is not loaded automatically so we need to load it in our application
//if( ! class_exists( 'WP_List_Table' ) ) {
//    require_once( ABSPATH . 'wp-admin/includes/class-wp-list-table.php' );
//}

// Toolkit - BaseView
if( ! class_exists( 'Sindria\Toolkit\BaseView' ) ) {
    require_once WP_PLUGIN_DIR . '/toolkit/BaseView.php';
}

// Toolkit - BaseService
//if( ! class_exists( 'Sindria\Toolkit\BaseService' ) ) {
//    require_once WP_PLUGIN_DIR . '/toolkit/BaseService.php';
//}

// Toolkit - BaseHelper
if( ! class_exists( 'Sindria\Toolkit\BaseHelper' ) ) {
    require_once WP_PLUGIN_DIR . '/toolkit/BaseHelper.php';
}

// Toolkit - DataTable
//if( ! class_exists( 'Sindria\Toolkit\Datatable\DataTable' ) ) {
//    require_once WP_PLUGIN_DIR . '/toolkit/datatable/DataTable.php';
//}

// Toolkit - Form
if( ! class_exists( 'Sindria\Toolkit\Datatable\Form' ) ) {
    require_once WP_PLUGIN_DIR . '/toolkit/form/Form.php';
}

// Toolkit - Infotable
//if( ! class_exists( 'Sindria\Toolkit\Datatable\Infotable' ) ) {
//    require_once WP_PLUGIN_DIR . '/toolkit/infotable/InfoTable.php';
//}

// Toolkit - Modal
//if( ! class_exists( 'Sindria\Toolkit\Datatable\Modal' ) ) {
//    require_once WP_PLUGIN_DIR . '/toolkit/modal/Modal.php';
//}

// Core
//require_once 'functions.php';
//require_once 'routes.php';
//require_once 'Plugin.php';

// MVVM
//require_once 'Controller.php';
require_once 'View.php';
//require_once 'Service.php';
require_once 'Helper.php';

// View Models
//require_once 'viewmodel/UsersViewModel.php';
//require_once 'viewmodel/UserInfoViewModel.php';
require_once 'viewmodel/UserAddViewModel.php';
//require_once 'viewmodel/UserShowViewModel.php';

//require_once 'viewmodel/PoliciesViewModel.php';
//require_once 'viewmodel/PolicyInfoViewModel.php';
//require_once 'viewmodel/PolicyAddViewModel.php';
//require_once 'viewmodel/PolicyShowViewModel.php';
//require_once 'viewmodel/PolicyAttachViewModel.php';
//require_once 'viewmodel/PolicyDetachViewModel.php';

// Datatables
//require_once 'datatable/UsersDataTable.php';
//require_once 'datatable/PoliciesDataTable.php';
//require_once 'datatable/UserPoliciesDataTable.php';

// Infotables
//require_once 'infotable/UserInfoTable.php';
//require_once 'infotable/PolicyInfoTable.php';

// Forms
require_once 'form/AddUserForm.php';
//var_dump(require_once 'form/AddUserForm.php');
//require_once 'form/ShowUserForm.php';
//
//require_once 'form/AddPolicyForm.php';
//require_once 'form/ShowPolicyForm.php';
//require_once 'form/AttachPolicyForm.php';
//require_once 'form/DetachPolicyForm.php';

// Modals
//require_once 'modal/WarningModal.php';


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

function gesu() {
    $view = new View();

    $data = new \stdClass();

    $viewModel = \viewmodel\UserAddViewModel::getInstance();
    $viewModel($data);


    return $view->render('add-user');

}



