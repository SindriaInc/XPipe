	<h3 class="m-b"><span><?php esc_html_e('Bar', 'ffl_admin_theme'); ?></span></h3>
	<p class="no-m-t"><?php esc_html_e('Change the admin bar on the top', 'ffl_admin_theme'); ?></p>
	<p>
		<label>
			<input name="<?php esc_attr_e($this->setting->setting_name); ?>[bar_front]" type="checkbox" <?php if ($this->setting->get_setting('bar_front') == true) echo 'checked="checked" '; ?>> 
			<?php esc_html_e('Apply style on front-end', 'ffl_admin_theme'); ?>
		</label>
	</p>
	<div class="box">
		<h4><span><?php esc_html_e('Logo &amp; name', 'ffl_admin_theme'); ?></span></h4>
		<div class="box-body b-t hide">
			<p>
				<label>
					<?php esc_html_e('logo image', 'ffl_admin_theme'); ?>
					<br>
					<input name="<?php esc_attr_e($this->setting->setting_name); ?>[bar_logo]" type="text" value="<?php esc_attr_e( $this->setting->get_setting('bar_logo') ); ?>">
					<button type="button" class="button-secondary upload-btn"><?php esc_html_e('Upload', 'ffl_admin_theme'); ?></button>
				</label>
			</p>
			<p>
				<label>
					<?php esc_html_e('Link', 'ffl_admin_theme'); ?>
					<input name="<?php esc_attr_e($this->setting->setting_name); ?>[bar_name_link]" type="text" value="<?php esc_attr_e( $this->setting->get_setting('bar_name_link') ); ?>" class="widefat">
				</label> 
			</p>
			<p>
				<label>
					<?php esc_html_e('Name', 'ffl_admin_theme'); ?>
					<input name="<?php esc_attr_e($this->setting->setting_name); ?>[bar_name]" type="text" value="<?php esc_attr_e( $this->setting->get_setting('bar_name') ); ?>" class="widefat">
				</label> 
			</p>
			<p>
				<label>
					<input name="<?php esc_attr_e($this->setting->setting_name); ?>[bar_name_hide]" type="checkbox" <?php if ( $this->setting->get_setting('bar_name_hide') == true ) echo 'checked="checked" '; ?>> 
					<?php esc_html_e('Hide "Name"', 'ffl_admin_theme'); ?>
				</label>
			</p>
		</div>
	</div>
	<div class="box">
		<h4><span><?php esc_html_e('Quick links', 'ffl_admin_theme'); ?></span></h4>
		<div class="box-body b-t hide">
			<p>
				<fieldset>
					<label>
						<input name="<?php esc_attr_e($this->setting->setting_name); ?>[bar_updates_hide]" type="checkbox" <?php if ($this->setting->get_setting('bar_updates_hide') == true) echo 'checked="checked" '; ?>> 
						<?php esc_html_e('Remove "Updates"', 'ffl_admin_theme'); ?>
					</label>
					<br>
					<label>
						<input name="<?php esc_attr_e($this->setting->setting_name); ?>[bar_comments_hide]" type="checkbox" <?php if ($this->setting->get_setting('bar_comments_hide') == true) echo 'checked="checked" '; ?>> 
						<?php esc_html_e('Remove "Comments"', 'ffl_admin_theme'); ?>
					</label>
					<br>
					<label>
						<input name="<?php esc_attr_e($this->setting->setting_name); ?>[bar_new_hide]" type="checkbox" <?php if ($this->setting->get_setting('bar_new_hide') == true) echo 'checked="checked" '; ?>> 
						<?php esc_html_e('Remove "New"', 'ffl_admin_theme'); ?>
					</label>
					<?php if ( is_multisite() && get_current_blog_id() == 1 && current_user_can( 'manage_options' ) ) { ?>
					<br>
					<label>
						<input name="<?php esc_attr_e($this->setting->setting_name); ?>[bar_site_hide]" type="checkbox" <?php if ($this->setting->get_setting('bar_site_hide') == true) echo 'checked="checked" '; ?>> 
						<?php esc_html_e('Remove "My sites"', 'ffl_admin_theme'); ?>
					</label>
					<?php } ?>
				</fieldset>
			</p>
		</div>
	</div>
	<h3 class="m-b"><span><?php esc_html_e('Menu', 'ffl_admin_theme'); ?></span></h3>
	<p class="no-m-t"><?php esc_html_e('Change the menu on the left.', 'ffl_admin_theme'); ?></p>
	<p>
		<label>
			<input name="<?php esc_attr_e($this->setting->setting_name); ?>[menu_collapse]" type="checkbox" <?php if ($this->setting->get_setting('menu_collapse') == true) echo 'checked="checked" '; ?>> 
			<?php esc_html_e('Collapse', 'ffl_admin_theme'); ?>
		</label>
		<br>
		<label>
			<input name="<?php esc_attr_e($this->setting->setting_name); ?>[menu_collapse_hide]" type="checkbox" <?php if ($this->setting->get_setting('menu_collapse_hide') == true) echo 'checked="checked" '; ?>> 
			<?php esc_html_e('Hide collapse link', 'ffl_admin_theme'); ?>
		</label>
		<br>
		<label>
			<input name="<?php esc_attr_e($this->setting->setting_name); ?>[menu_h]" type="checkbox" <?php if ($this->setting->get_setting('menu_h') == true) echo 'checked="checked" '; ?>> 
			<?php esc_html_e('Horizontal', 'ffl_admin_theme'); ?>
		</label>
	</p>
	<div class="admin-menus">
			<?php
				foreach ($this->menus as $k=>$v){
					$slug = $v[2];
					if($slug != NULL){
						$t = explode(' <span', $v[0]);
						$title = isset( $this->nav[$slug]['title'] ) && $this->nav[$slug]['title'] != '' ? $this->nav[$slug]['title'] : $t[0];
						$icon  = isset( $this->nav[$slug]['icon'] ) && $this->nav[$slug]['icon'] != '' ? $this->nav[$slug]['icon'] : NULL;
						$url  = isset( $this->nav[$slug]['url'] ) && $this->nav[$slug]['url'] != '' ? $this->nav[$slug]['url'] : $v[2];
						$hide  = isset( $this->nav[$slug]['hide'] ) && $this->nav[$slug]['hide'] != '' ? $this->nav[$slug]['hide'] : NULL;
						$index = isset( $this->nav[$slug]['index'] ) && $this->nav[$slug]['index'] != '' ? $this->nav[$slug]['index'] : $v[10];
						$class = isset( $this->nav[$slug]['class'] ) && $this->nav[$slug]['class'] != '' ? $this->nav[$slug]['class'] : $v[4];
						$id = isset( $this->nav[$slug]['id'] ) && $this->nav[$slug]['id'] != '' ? $this->nav[$slug]['id'] : ($t[0] ? $v[5] : NULL);
						$new = isset($v[13]) ? true : false;
			?>
			<div data-menu-slug='<?php echo esc_attr($slug); ?>' data-default-title="<?php echo esc_attr('Custom menu', 'ffl_admin_theme'); ?>" data-default-slug="custom_url" class="box bg admin-menu-item <?php if ( false !== strpos( $v[4], 'wp-menu-separator' ) ) { echo esc_attr('admin-menu-separator');} ?>">

				<h4 class="<?php if($t[0] == NULL){ esc_attr_e( 'separator' ); }?>" >
					<?php if($t[0]){ ?>
					<i class="<?php esc_attr_e( $icon ? $icon : $v[6] ); ?>" data-target="#dropdown" data-toggle="dropdown"></i>
					<input name="<?php esc_attr_e( $this->setting->setting_name.'[menu]['.$slug.'][icon]'); ?>" value="<?php esc_attr_e( $icon ); ?>" type="text" hidden>
					<span class="pull-right text-muted">
						<?php if($title !== $t[0]) esc_html_e( $t[0] ); ?>
					</span>
					<span class="admin-menu-title"><?php esc_html_e( $title ? $title : $t[0] ); ?></span>
					<?php } ?>
				</h4>

				<div class="box-body b-t hide">
					<div class="admin-menu-atom">
						<input data-index name="<?php esc_attr_e( $this->setting->setting_name.'[menu]['.$slug.'][index]' ); ?>" value="<?php esc_attr_e( $index ); ?>" type="text" hidden>
						<?php if($t[0]){ ?>
						<p>
							<label>
								<?php esc_html_e('Title', 'ffl_admin_theme'); ?>
								<input data-title name="<?php esc_attr_e( $this->setting->setting_name.'[menu]['.$slug.'][title]' ); ?>" value="<?php esc_attr_e( $title ); ?>" type="text" class="widefat">
							</label>
						</p>
						<p>
							<label>
								<?php esc_html_e('Url', 'ffl_admin_theme'); ?>
								<input <?php echo esc_attr($new ? '' : 'disabled="disabled"'); ?> data-url name="<?php esc_attr_e( $this->setting->setting_name.'[menu]['.$slug.'][url]' ); ?>" value="<?php esc_attr_e( $url ); ?>" type="text" class="widefat">
							</label>
						</p>
						<p>
							<label>
								<?php esc_html_e('Capability', 'ffl_admin_theme'); ?>
								<select <?php echo esc_attr($new ? '' : 'disabled="disabled"'); ?> data-capability name="<?php esc_attr_e( $this->setting->setting_name.'[menu]['.$slug.'][capability]' ); ?>" class="widefat">
									<?php echo $this->get_cap_options($v[1]); ?>
								</select>
							</label>
						</p>
						<p>
							<label>
								<?php esc_html_e('ID attribute', 'ffl_admin_theme'); ?>
								<input data-id name="<?php esc_attr_e( $this->setting->setting_name.'[menu]['.$slug.'][id]' ); ?>" value="<?php esc_attr_e( $id ); ?>" type="text" class="widefat">
							</label>
						</p>
						<?php } ?>
						<p>
							<label>
								<?php esc_html_e('CSS classes', 'ffl_admin_theme'); ?>
								<input data-class name="<?php esc_attr_e( $this->setting->setting_name.'[menu]['.$slug.'][class]' ); ?>" value="<?php esc_attr_e( $class ); ?>" type="text" class="widefat">
							</label>
						</p>
						<p>
							<label>
								<input data-remove name="<?php esc_attr_e( $this->setting->setting_name.'[menu]['.$slug.'][hide]' ); ?>" <?php if ($hide) echo 'checked="checked" '; ?> type="checkbox"> 
								<?php esc_html_e('Remove from menu', 'ffl_admin_theme'); ?>
							</label>
						</p>
					</div>

					<?php
						if(isset($this->submenus[$v[2]])){
					?>

					<div class="admin-submenus-list">
						<input type="checkbox" name="submenus-check" id="<?php echo esc_attr('submenus-check-'.$k); ?>">
						<p>
							<label for="<?php echo esc_attr('submenus-check-'.$k); ?>"><?php esc_html_e('Submenu', 'ffl_admin_theme'); ?></label>
						</p>
						
						<div class="admin-menus admin-submenus">
							<?php
								$sub = isset($this->submenus[$v[2]]) ? $this->submenus[$v[2]] : array() ;
								foreach ($sub as $key=>$val){
									$slug = $v[2];
									$subslug = $val[2];
									if($subslug != NULL){
										if( isset($val[8]) ){
											$slug = $val[8];
										}
										if( isset($val[9]) ){
											$subslug = $val[9];
										}
										$t = explode(' <span', $val[0]);
										$title = isset( $this->subnav[$slug][$subslug]['title'] ) && $this->subnav[$slug][$subslug]['title'] != '' ? $this->subnav[$slug][$subslug]['title'] : $t[0];
										$hide  = isset( $this->subnav[$slug][$subslug]['hide'] ) ? TRUE : FALSE;
										$index = isset( $this->subnav[$slug][$subslug]['index'] ) && $this->subnav[$slug][$subslug]['index'] != '' ? $this->subnav[$slug][$subslug]['index'] : $val[10];
										$parent = isset( $this->subnav[$slug][$subslug]['parent'] ) && $this->subnav[$slug][$subslug]['parent'] != '' ? $this->subnav[$slug][$subslug]['parent'] : $slug;
										$url  = isset( $this->subnav[$slug][$subslug]['url'] ) && $this->subnav[$slug][$subslug]['url'] != '' ? $this->subnav[$slug][$subslug]['url'] : $val[2];
										$new = isset($val[13]) ? true : false;
							?>
							<div data-menu-slug='<?php echo esc_attr($subslug); ?>'  class="box admin-menu-item admin-submenu-item admin-submenu-atom" data-default-title="<?php echo esc_attr('Custom submenu', 'ffl_admin_theme'); ?>" data-default-slug="custom_sub_url">
								<h4 class="sm">
									<span class="pull-right text-muted">
										<?php if($title !== $t[0]) esc_html_e( $t[0] ); ?>
									</span>
									<span class="admin-menu-title"><?php esc_html_e( $title ? $title : $t[0] ); ?></span>
								</h4>
								<div class="box-body b-t hide">
									<input data-index name="<?php esc_attr_e( $this->setting->setting_name.'[submenu]['.$slug.']['.$subslug.'][index]' ); ?>" value="<?php esc_html_e( $index ); ?>" type="text" hidden>
									<p>
										<label>
											<?php esc_html_e('Title', 'ffl_admin_theme'); ?>
											<input data-title name="<?php esc_attr_e( $this->setting->setting_name.'[submenu]['.$slug.']['.$subslug.'][title]' ); ?>" value="<?php esc_attr_e( $title ); ?>" type="text" class="widefat">
										</label>
									</p>
									<p>
										<label>
											<?php esc_html_e('Parent', 'ffl_admin_theme'); ?> 
											<small class="text-muted"> <?php if(isset($val[7])){ echo '('.esc_html($val[7]).')'; } ?></small>
										</label>
										<select name="<?php esc_attr_e( $this->setting->setting_name.'[submenu]['.$slug.']['.$subslug.'][parent]' ); ?>" class="widefat">
											<?php echo $this->get_slug_options($parent); ?>
										</select>
									</p>
									<p>
										<label>
											<?php esc_html_e('Url', 'ffl_admin_theme'); ?>
											<input data-url <?php echo esc_attr($new ? '' : 'disabled="disabled"'); ?> name="<?php esc_attr_e( $this->setting->setting_name.'[submenu]['.$slug.']['.$subslug.'][url]' ); ?>" value="<?php esc_attr_e( $url ); ?>" type="text" class="widefat">
										</label>
									</p>
									<p>
										<label>
											<?php esc_html_e('Capability', 'ffl_admin_theme'); ?>
											<select <?php echo esc_attr($new ? '' : 'disabled="disabled"'); ?> data-capability name="<?php esc_attr_e( $this->setting->setting_name.'[submenu]['.$slug.']['.$subslug.'][capability]' ); ?>" class="widefat">
												<?php echo $this->get_cap_options($val[1]); ?>
											</select>
										</label>
									</p>
									<p>
										<label>
											<input name="<?php esc_attr_e( $this->setting->setting_name.'[submenu]['.$slug.']['.$subslug.'][hide]' ); ?>" <?php if ( $hide ) echo 'checked="checked" '; ?> type="checkbox"> 
											<?php esc_html_e('Remove from menu', 'ffl_admin_theme'); ?>
										</label>
									</p>
								</div>
							</div>
							<?php } }?>

							<button type="button" class="btn-add-menu btn-add-submenu-atom">+ <?php esc_html_e('Add submenu', 'ffl_admin_theme'); ?></button>
						</div>
					</div>
					<?php } ?>
				</div>
			</div>
			<?php
				}
			} ?>
			<button type="button" class="btn-add-menu btn-add-menu-atom">+ <?php esc_html_e('Add menu', 'ffl_admin_theme'); ?></button>
			<button type="button" class="btn-add-menu btn-add-separator">+ <?php esc_html_e('Add separator', 'ffl_admin_theme'); ?></button>
			<div id="dropdown" class="dropdown box">
				<div class="box-body" id="tab-iconlist">
					<div class="clearfix">
						<ul class="subsubsub">
							<?php
								foreach ( $icons as $icon ) {
									echo sprintf('<li><a href="#tab-icon-%s">%s</a></li>&nbsp;&nbsp;', esc_attr($icon), esc_html($icon));
								}
							?>
						</ul>
					</div>
					<?php 
						foreach ( $icons as $icon ) {
				            echo sprintf('<div class="iconlist clearfix" id="tab-icon-%s" style="display: none;">', esc_attr($icon));
				            	$file = $this->setting->plugin_path.'theme/icons/'.$icon.'/icon.php';
				            	if(file_exists($file)){
				            		include($file);
				            	}
				            echo '</div>';
				        }
					?>
				</div>
			</div>
	</div>
