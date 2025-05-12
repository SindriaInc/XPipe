<?php
/**
Plugin Name: Admin Theme - Firstr
Plugin URI: http://codecanyon.net/user/Flatfull/portfolio
Description: Change Wordpress admin bar, menu, login, footer, icon and colors
Version: 4.0
Author: Flatfull.com
Author URI: www.flatfull.com
Text Domain: ffl_admin_theme
*/

class FFL_Admin_Theme_Firstr {

	function __construct() {
		$this->init();
	}

	function init(){

		$dir = dirname(__FILE__);
		require $dir . '/modules/setting/setting.php';
		//require $dir . '/modules/nav/nav.php';
		require $dir . '/modules/color/color.php';
		//require $dir . '/modules/login/login.php';
		//require $dir . '/modules/footer/footer.php';

		$arg = array(
		     'page_title'   => 'Firstr Admin Theme'
		    ,'menu_title'	=> 'Firstr Admin'
		    ,'menu_slug'	=> 'admin-firstr'
		    ,'setting_name' => 'admin_theme_firstr_option'
			,'plugin_file'  => __FILE__
		);

		$setting =
		new FFL_Admin_Theme_Setting($arg);
		//new FFL_Admin_Theme_Nav($setting);
		new FFL_Admin_Theme_Color($setting);
		//new FFL_Admin_Theme_Footer($setting);
		//new FFL_Admin_Theme_Login($setting);

		require $dir . '/modules/demo/demo.php';
		new FFL_Admin_Theme_Demo($setting);

	}

}

new FFL_Admin_Theme_Firstr;
