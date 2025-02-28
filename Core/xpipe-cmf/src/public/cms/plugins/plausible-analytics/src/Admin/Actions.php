<?php
/**
 * Plausible Analytics | Admin Actions.
 *
 * @since 1.0.0
 *
 * @package    WordPress
 * @subpackage Plausible Analytics
 */

namespace Plausible\Analytics\WP\Admin;

use Plausible\Analytics\WP\Includes\Helpers;

// Bailout, if accessed directly.
if ( ! defined( 'ABSPATH' ) ) {
	exit;
}

class Actions {

	/**
	 * Constructor.
	 *
	 * @since  1.0.0
	 * @access public
	 *
	 * @return void
	 */
	public function __construct() {
		add_action( 'admin_enqueue_scripts', [ $this, 'register_assets' ] );
		add_action( 'wp_ajax_plausible_analytics_save_admin_settings', [ $this, 'save_admin_settings' ] );
	}

	/**
	 * Register Assets.
	 *
	 * @since  1.0.0
	 * @access public
	 *
	 * @return void
	 */
	public function register_assets() {
		\wp_enqueue_style( 'plausible-admin', PLAUSIBLE_ANALYTICS_PLUGIN_URL . 'assets/dist/css/plausible-admin.css', '', filemtime( PLAUSIBLE_ANALYTICS_PLUGIN_DIR . 'assets/dist/css/plausible-admin.css' ), 'all' );
		\wp_enqueue_script( 'plausible-admin', PLAUSIBLE_ANALYTICS_PLUGIN_URL . 'assets/dist/js/plausible-admin.js', '', filemtime( PLAUSIBLE_ANALYTICS_PLUGIN_DIR . 'assets/dist/js/plausible-admin.js' ), true );
	}

	/**
	 * Save Admin Settings
	 *
	 * @since 1.0.0
	 *
	 * @return void
	 */
	public function save_admin_settings() {
		// Sanitize all the post data before using.
		$post_data        = Helpers::clean( $_POST );
		$current_settings = Helpers::get_settings();

		// Security: Roadblock to check for unauthorized access.
		if (
			'plausible_analytics_save_admin_settings' === $post_data['action'] &&
			current_user_can( 'administrator' ) &&
			(
				! empty( $post_data['roadblock'] ) &&
				wp_verify_nonce( $post_data['roadblock'], 'plausible-analytics-settings-roadblock' )
			)
		) {
			if (
				! empty( $post_data['plausible_analytics_settings']['domain_name'] )
				|| isset( $post_data['plausible_analytics_settings']['self_hosted_domain'] ) ) {
				$current_settings = array_replace( $current_settings, $post_data['plausible_analytics_settings'] );

				// Update all the options to plausible settings.
				update_option( 'plausible_analytics_settings', $current_settings );

				$status  = 'success';
				$message = esc_html__( 'Settings saved successfully.', 'plausible-analytics' );
			} else {
				$status  = 'error';
				$message = esc_html__( 'Something went wrong.', 'plausible-analytics' );
			}

			// Send response.
			wp_send_json_success(
				[
					'message' => $message,
					'status'  => $status,
				]
			);
		}
	}
}
