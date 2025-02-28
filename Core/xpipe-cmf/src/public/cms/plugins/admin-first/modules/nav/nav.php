<?php
/**
 * @author flatfull.com
 */

class FFL_Admin_Theme_Nav{

	private $setting
			,$menu
			,$submenu
			,$nav
			,$subnav
			,$slugs
			,$icons
			;

	function __construct($setting) {
		$this->setting = $setting;

		$this->icons = [];
        $dir = plugin_dir_path( $this->setting->plugin_file );
        foreach ( (array) glob( $dir . '/theme/icons/*' ) as $file ) {
            $this->icons[] = basename($file);
        }

		add_action( 'admin_bar_menu', array( $this, 'admin_bar'), 9999999 );
		add_action( 'admin_menu', array( $this, 'admin_menu' ), 9999999 );
		add_action( 'admin_enqueue_scripts', array( $this, 'admin_scripts' ) );
		add_action( 'wp_enqueue_scripts', array( $this, 'admin_bar_front' ) );
		add_filter( 'admin_body_class', array( $this, 'add_admin_body_class' ) );

		add_action( 'admin_screen_col_2', array( $this, 'admin_screen' ) );
	}

	function admin_menu() {
		global $menu;
		global $submenu;

		// 0 = menu_title, 1 = capability, 2 = menu_slug, 3 = page_title, 4 = classes, 5 = hookname/id, 6 = icon_url.

		if(empty($menu)) return;

		//$this->nav    = $this->setting->get_setting('menu');
		$this->nav    = [];
		$this->subnav = $this->setting->get_setting('submenu');

		// new menu
		if(!empty($this->nav)){
			$menu_slugs = array_column($menu, 2);
			foreach ($this->nav as $key=>$nav){
				if( !in_array($key, $menu_slugs) && !isset($nav['hide'])){
					$new_menu = array(
						isset($nav['title']) ? $nav['title'] : '',
						isset($nav['capability']) ? $nav['capability'] : 'read',
						isset($nav['url']) ? $nav['url'] : $key,
						'',
						isset($nav['class']) ? $nav['class'] : '',
						isset($nav['id']) ? $nav['id'] : '',
						isset($nav['icon']) ? $nav['icon'] : ''
					);
					$new_menu[13] = 'new';
					$menu[] = $new_menu;
				}
			};
		}

		// reorder $menu
		foreach ($menu as $k=>&$v){
			// get parent slug
			if ( false === strpos( $v[4], 'wp-menu-separator' ) ) {
				$m = explode(' <span', $v[0]);
				$this->slugs[$v[2]] = $m[0];
			}
			$v[10] = $k;
			if(isset($this->nav[$v[2]]['index'])){
				$v[10] = (int)$this->nav[$v[2]]['index'];
			}
		}

		// reorder submenu
		foreach ($submenu as $k=>&$v){
			// new submenu
			if(isset($this->subnav[$k])){
				$submenu_slugs = array_column($v, 2);
				foreach ($this->subnav[$k] as $key=>$nav){
					if( !in_array($key, $submenu_slugs) && isset($nav['url']) && !isset($nav['hide'])){
						$new_menu = array(
							isset($nav['title']) ? $nav['title'] : '',
							isset($nav['capability']) ? $nav['capability'] : 'read',
							isset($nav['url']) ? $nav['url'] : $key
						);
						$new_menu[13] = 'new';
						$submenu[$k][] = $new_menu;
					}
				};
			}
			$i = 0;
			foreach ($v as $key=>&$val){
				$val[10] = $i;
				$i++;

				$filter = 'return';
				if(false !== strpos( $val[2], $filter )){
					$url = preg_replace('~(\?|&)'.$filter.'=[^&]*~', '$1', $val[2]);
					$val[2] = rtrim($url, '?');
				}

				if(isset($this->subnav[$k][$val[2]])){
					$parent = $this->subnav[$k][$val[2]]['parent'];
					if(isset($this->subnav[$k][$val[2]]['index'])){
						$val[10] = (int)$this->subnav[$k][$val[2]]['index'];
					}
					if($k !== $parent){
						if(isset($this->subnav[$k][$val[2]]['hide'])){
							$val[11] = $this->subnav[$k][$val[2]]['hide'];
						}
						// set parent
						if(isset( $this->slugs[$k] )){
							// title
							$val[7] = $this->slugs[$k];
						}
						// parent
						$val[8] = $k;
						// child
						$val[9] = $val[2];
						$slug = $this->get_admin_submenu_item_url($k, $val[2]);
						$val[2] = $slug;
						$submenu[$parent][] = $val;

						unset($v[$key]);
					}
				}
			}
		}

		// sort
		usort($menu, array($this, 'sort_menu'));
		foreach ($submenu as $k=>&$v){
			usort($v, array($this, 'sort_menu'));
		}

		$this->menus = $this->array_copy($menu);
		$this->submenus = $this->array_copy($submenu);

		// update menu
		end( $menu );
		end( $submenu );

		foreach ($menu as $k=>&$v){
			$slug = $v[2];
			if($slug != NULL && isset( $this->nav[$slug] )){
				// hide
				if( isset($this->nav[$slug]['hide']) && $this->nav[$slug]['hide'] ){
					unset($menu[$k]);
				}else{
					// title
					if( isset($this->nav[$slug]['title']) && $this->nav[$slug]['title'] != ''){
						$t = explode(' <span', $v[0]);
						$v[0] = $this->nav[$slug]['title']. ( isset($t[1]) ? ' <span '.$t[1] : '' );
					}

					// class
					if( isset( $this->nav[$slug]['class'] ) ){
						$v[4] = $this->nav[$slug]['class'].' menu-top';
					}

					// id
					if( isset( $this->nav[$slug]['id'] ) ){
						$v[5] = $this->nav[$slug]['id'];
					}

					// icon
					if( isset( $this->nav[$slug]['icon'] ) && $this->nav[$slug]['icon'] != ''){
						$v[6] = $this->nav[$slug]['icon'];
					}
				}
			}
		}

		foreach ($submenu as $k=>&$v){
			foreach($v as $key=>&$val){
				$slug = $k;
				$subslug = $val[2];
				if( isset($val[11]) ){
					unset( $submenu[$k][$key] );
					continue;
				}
				if( isset($val[8]) ){
					$slug = $val[8];
				}
				if( isset($val[9]) ){
					$subslug = $val[9];
				}
				if($subslug != NULL && isset( $this->subnav[$slug][$subslug]['title'] ) && $this->subnav[$slug][$subslug]['title'] !=''){
					$t = explode(' <span', $val[0]);
					$val[0] = $this->subnav[$slug][$subslug]['title']. ( isset($t[1]) ? ' <span '.$t[1] : '' );
				}
			}
		}
	}

	// sort menu
	function sort_menu($a, $b) {
	    if ($a[10] == $b[10]) {
	        return 0;
	    }
	    return ($a[10] < $b[10]) ? -1 : 1;
	}

	function get_slug_options($value){
		asort($this->slugs);
		$html = '';
		foreach ( $this->slugs as $option => $name ) {
	        $selected = selected( $option, $value, false );
	        $html    .= '<option value="' . esc_attr( $option ) . '" ' . $selected . '>' . esc_html( $name ) . '</option>';
        }
        return $html;
	}

	function get_cap_options($value){
		$user = wp_get_current_user();
	    $caps = array_keys( $user->allcaps );
	    asort($caps);

		$html = '';
		foreach ( $caps as $option ) {
	        $selected = selected( $option, $value, false );
	        $html    .= '<option value="' . esc_attr( $option ) . '" ' . $selected . '>' . esc_html( $option ) . '</option>';
        }
        return $html;
	}

	// admin bar
	function admin_bar(){
		global $wp_admin_bar;
		$all_toolbar_nodes = $wp_admin_bar->get_nodes();

		$site = array();
		foreach ( $all_toolbar_nodes as $key=>$node ) {
			$args = $node;
			if( ((!is_admin() && $this->setting->get_setting('bar_front')) || is_admin()) && ($args->id == "site-name" || $args->id == "visit-site")){
				$logo = $this->setting->get_setting('bar_logo') ? sprintf('<img src="%s">', $this->setting->get_setting('bar_logo')) : '';
				$hide = $this->setting->get_setting('bar_name_hide') ? "hide" : "";
				$name = $this->setting->get_setting('bar_name') ? $this->setting->get_setting('bar_name') : $args->title;
				$args->title = sprintf('%s <span class="%s">%s</span>', $logo, $hide, $name);
				$this->setting->get_setting('bar_name_link') && ($args->href = $this->setting->get_setting('bar_name_link'));
			}
			if($args->id == "my-sites"){
				$site = $node;
			}
			// update the Toolbar node
			$wp_admin_bar->add_node( $args );
		}
		// remove the wordpress logo
		$wp_admin_bar->remove_node( 'wp-logo' );
		$wp_admin_bar->remove_node( 'view-site' );

		$wp_admin_bar->remove_node( 'my-sites' );

        // XPipe
        //$wp_admin_bar->add_node( $site );

		if(is_admin() && $this->setting->get_setting('enable-dark-mode')){
			$wp_admin_bar->add_menu(
	            array(
					'id'    => 'admin-dark-mode',
					'title' => '<input type="checkbox" name="dark-mode" id="admin-dark-mode-switch" class="switch">',
					'href'  => '#dark-mode',
	            )
	        );
		}

		if($this->setting->get_setting('bar_updates_hide')){
				$wp_admin_bar->remove_node('updates');
		}
		if($this->setting->get_setting('bar_comments_hide')){
				$wp_admin_bar->remove_node('comments');
		}
		if($this->setting->get_setting('bar_new_hide')){
				$wp_admin_bar->remove_node('new-content');
		}
		if($this->setting->get_setting('bar_site_hide')){
				$wp_admin_bar->remove_node('my-sites');
		}

	}

	function array_copy($arr) {
	    $newArray = array();
	    foreach($arr as $key => $value) {
	        if(is_array($value)) $newArray[$key] = $this->array_copy($value);
	        else if(is_object($value)) $newArray[$key] = clone $value;
	        else $newArray[$key] = $value;
	    }
	    return $newArray;
	}

	function add_admin_body_class( $classes ) {
		$class = '';
		if( $this->setting->get_setting('menu_collapse') ) {
			$class = ' folded';
		}

		if( $this->setting->get_setting('menu_collapse_hide') ) {
			$class .= ' hide-collapse-link';
		}

		if( $this->setting->get_setting('menu_h') ) {
			$class .= ' admin-menu-h';
		}

	    return $classes.$class;
	}

	function admin_scripts() {
		if ( is_customize_preview() ) return;
		wp_enqueue_media();
		wp_enqueue_style( 'wp-color-picker' );
		wp_enqueue_script( 'wp-color-picker' );
		wp_enqueue_script( 'ffl-admin-theme-dropdown', $this->setting->plugin_url.( "assets/js/dropdown.js" ) );
		wp_enqueue_script( 'ffl-admin-theme-main', $this->setting->plugin_url.( "assets/js/main.js" ) );
		wp_enqueue_script( 'ffl-admin-theme-sortable', $this->setting->plugin_url.( "assets/js/html.sortable.min.js" ) );
		wp_enqueue_style( 'ffl-admin-theme-style', $this->setting->plugin_url.( "assets/css/style.css" ) );

		foreach ( $this->icons as $icon ) {
            wp_enqueue_style( 'ffl-admin-theme-icon-'.$icon, $this->setting->plugin_url.( "theme/icons/".$icon."/icon.css" ) );
        }

	}

	function admin_bar_front() {
		if( is_admin_bar_showing() && $this->setting->get_setting('bar_front') ){
			wp_enqueue_style( 'ffl-admin-theme-variables', $this->setting->plugin_url.( "theme/color.variables.css" ) );
			wp_enqueue_style( 'ffl-admin-theme-bar', $this->setting->plugin_url.( "theme/admin.css" ) );
			wp_enqueue_style( 'ffl-admin-theme-admin', $this->setting->plugin_url.( "theme/color.css" ) );
		}
	}

	function admin_screen() {
		$icons = $this->icons;
		include 'tpl.php';
	}

	function get_admin_submenu_item_url( $slug, $sub_slug ) {
		$menu_file = $slug;
		$pos       = strpos( $menu_file, '?' );

		if ( false !== $pos ) {
			$menu_file = substr( $menu_file, 0, $pos );
		}

		$menu_hook = get_plugin_page_hook( $sub_slug, $slug );

		// return menu slug
		if(empty($sub_slug)){
			$menu_hook = get_plugin_page_hook( $slug, 'admin.php' );
			if ( ! empty( $menu_hook )
				|| ( ( 'index.php' !== $slug )
					&& file_exists( WP_PLUGIN_DIR . "/$menu_file" )
					&& ! file_exists( ABSPATH . "/wp-admin/$menu_file" ) )
			) {
				$url = 'admin.php?page='.$slug;
			} else {
				$url = $slug;
			}
			return $url;
		}

		// return submenu slug
		$sub_file  = $sub_slug;
		$pos       = strpos( $sub_file, '?' );
		if ( false !== $pos ) {
			$sub_file = substr( $sub_file, 0, $pos );
		}

		if ( ! empty( $menu_hook )
			|| ( ( 'index.php' !== $sub_slug )
				&& file_exists( WP_PLUGIN_DIR . "/$sub_file" )
				&& ! file_exists( ABSPATH . "/wp-admin/$sub_file" ) )
		) {
			// If admin.php is the current page or if the parent exists as a file in the plugins or admin directory.
			if ( ( file_exists( WP_PLUGIN_DIR . "/$menu_file" ) && ! is_dir( WP_PLUGIN_DIR . "/{$slug}" ) ) || file_exists( $menu_file ) ) {
				$url = add_query_arg( array( 'page' => $sub_slug ), $slug );
			} else {
				$url = add_query_arg( array( 'page' => $sub_slug ), 'admin.php' );
			}
		} else {
			$url = $sub_slug;
		}
		return $url;
	}

}
