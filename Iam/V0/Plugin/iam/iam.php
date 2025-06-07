<?php
/**
 * @package IAM
 */
/*
Plugin Name: IAM
Plugin URI: https://sindria.org
Description: Add identity access management (iam) integration.
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: iam
*/

// WP_List_Table is not loaded automatically so we need to load it in our application
if( ! class_exists( 'WP_List_Table' ) ) {
    require_once( ABSPATH . 'wp-admin/includes/class-wp-list-table.php' );
}

// Toolkit - BaseView
if( ! class_exists( 'Sindria\Toolkit\BaseView' ) ) {
    require_once WP_PLUGIN_DIR . '/toolkit/BaseView.php';
}

// Toolkit - BaseService
if( ! class_exists( 'Sindria\Toolkit\BaseService' ) ) {
    require_once WP_PLUGIN_DIR . '/toolkit/BaseService.php';
}

// Toolkit - BaseHelper
if( ! class_exists( 'Sindria\Toolkit\BaseHelper' ) ) {
    require_once WP_PLUGIN_DIR . '/toolkit/BaseHelper.php';
}

// Toolkit - DataTable
if( ! class_exists( 'Sindria\Toolkit\Datatable\DataTable' ) ) {
    require_once WP_PLUGIN_DIR . '/toolkit/datatable/DataTable.php';
}

// Toolkit - Form
if( ! class_exists( 'Sindria\Toolkit\Datatable\Form' ) ) {
    require_once WP_PLUGIN_DIR . '/toolkit/form/Form.php';
}

// Toolkit - Infotable
if( ! class_exists( 'Sindria\Toolkit\Datatable\Infotable' ) ) {
    require_once WP_PLUGIN_DIR . '/toolkit/infotable/InfoTable.php';
}

// Toolkit - Modal
if( ! class_exists( 'Sindria\Toolkit\Datatable\Modal' ) ) {
    require_once WP_PLUGIN_DIR . '/toolkit/modal/Modal.php';
}

// Core
require_once 'functions.php';
require_once 'routes.php';
require_once 'Plugin.php';

// MVVM
require_once 'Controller.php';
require_once 'View.php';
require_once 'Service.php';
require_once 'Helper.php';

// View Models
require_once 'viewmodel/UsersViewModel.php';
require_once 'viewmodel/UserInfoViewModel.php';
require_once 'viewmodel/UserAddViewModel.php';
require_once 'viewmodel/UserShowViewModel.php';

require_once 'viewmodel/PoliciesViewModel.php';
require_once 'viewmodel/PolicyInfoViewModel.php';
require_once 'viewmodel/PolicyAddViewModel.php';
//require_once 'viewmodel/PolicyShowViewModel.php';
require_once 'viewmodel/PolicyAttachViewModel.php';
require_once 'viewmodel/PolicyDetachViewModel.php';

// Datatables
require_once 'datatable/UsersDataTable.php';
require_once 'datatable/PoliciesDataTable.php';
require_once 'datatable/UserPoliciesDataTable.php';

// Infotables
require_once 'infotable/UserInfoTable.php';
require_once 'infotable/PolicyInfoTable.php';

// Forms
require_once 'form/AddUserForm.php';
require_once 'form/ShowUserForm.php';

require_once 'form/AddPolicyForm.php';
//require_once 'form/ShowPolicyForm.php';
require_once 'form/AttachPolicyForm.php';
require_once 'form/DetachPolicyForm.php';

// Modals
require_once 'modal/WarningModal.php';

// Init plugin
add_action('init', 'iam', 9, 0);
