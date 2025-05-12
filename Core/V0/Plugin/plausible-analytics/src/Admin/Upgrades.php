<?php
/**
 * Plausible Analytics | Upgrades
 *
 * @since      1.3.0
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

/**
 * Class Upgrades
 *
 * @since 1.3.0
 */
class Upgrades {
	/**
	 * Constructor for Upgrades.
	 *
	 * @since  1.3.0
	 * @access public
	 *
	 * @return void
	 */
	public function __construct() {
		add_action( 'init', [ $this, 'register_routines' ] );
	}

	/**
	 * Register routines for upgrades.
	 *
	 * This is intended for automatic upgrade routines having less resource intensive tasks.
	 *
	 * @since  1.3.0
	 * @access public
	 *
	 * @return void
	 */
	public function register_routines() {
		$plausible_analytics_version = get_option( 'plausible_analytics_version' );

		// If version doesn't exist, then consider it `1.0.0`.
		if ( ! $plausible_analytics_version ) {
			$plausible_analytics_version = '1.0.0';
		}

		if ( version_compare( $plausible_analytics_version, '1.2.5', '<' ) ) {
			$this->upgrade_to_125();
		}

		if ( version_compare( $plausible_analytics_version, '1.2.6', '<' ) ) {
			$this->upgrade_to_126();
		}

		// Upgrade to version 1.3.0.
		// if ( version_compare( $plausible_analytics_version, '1.3.0', '<' ) ) {
		// 	$this->upgrade_to_130();
		// }

		// Add required upgrade routines for future versions here.
	}

	/**
	 * Get rid of the previous "example.com" default for self_hosted_domain.
	 *
	 * @since 1.2.6
	 *
	 * @return void
	 */
	public function upgrade_to_126() {
		$old_settings = Helpers::get_settings();
		$new_settings = $old_settings;

		if ( ! empty( $old_settings['self_hosted_domain'] )
			&& strpos( $old_settings['self_hosted_domain'], 'example.com' ) !== false ) {
				$new_settings['self_hosted_domain'] = '';
		}

		update_option( 'plausible_analytics_settings', $new_settings );

		update_option( 'plausible_analytics_version', '1.2.6' );
	}

	/**
	 * Upgrade routine for 1.2.5
	 *
	 * Cleans Custom Domain related options from database, as it was removed in this version.
	 *
	 * @since  1.2.5
	 * @access public
	 *
	 * @return void
	 */
	public function upgrade_to_125() {
		$old_settings = Helpers::get_settings();
		$new_settings = $old_settings;

		if ( isset( $old_settings['custom_domain_prefix'] ) ) {
			unset( $new_settings['custom_domain_prefix'] );
		}

		if ( isset( $old_settings['custom_domain'] ) ) {
			unset( $new_settings['custom_domain'] );
		}

		if ( isset( $old_settings['is_custom_domain'] ) ) {
			unset( $new_settings['is_custom_domain'] );
		}

		// Enable Outbound links by default.
		$new_settings['enhanced_measurements'] = [ 'outbound-links' ];

		if ( ! empty( $old_settings['track_administrator'] )
			&& $old_settings['track_administrator'] ) {
			$new_settings['tracked_user_roles'] = [ 'administrator' ];
		}

		update_option( 'plausible_analytics_settings', $new_settings );

		update_option( 'plausible_analytics_version', '1.2.5' );
	}

	/**
	 * Upgrade routine for 1.3.0
	 *
	 * @since  1.3.0
	 * @access public
	 *
	 * @return void
	 */
	public function upgrade_to_130() {
		$old_settings = Helpers::get_settings();
		$new_settings = $old_settings;

		$old_embed_analytics            = ! empty( $old_settings['embed_analytics'] ) ? $old_settings['embed_analytics'] : '';
		$new_settings['is_shared_link'] = $old_embed_analytics;

		// Update the new settings.
		update_option( 'plausible_analytics_settings', $new_settings );

		// Update the version in DB to the latest as upgrades completed.
		update_option( 'plausible_analytics_version', '1.3.0' );
	}
}
