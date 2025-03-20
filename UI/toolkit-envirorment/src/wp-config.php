<?php
/**
 * The base configuration for WordPress
 *
 * The wp-config.php creation script uses this file during the installation.
 * You don't have to use the website, you can copy this file to "wp-config.php"
 * and fill in the values.
 *
 * This file contains the following configurations:
 *
 * * Database settings
 * * Secret keys
 * * Database table prefix
 * * ABSPATH
 *
 * @link https://developer.wordpress.org/advanced-administration/wordpress/wp-config/
 *
 * @package WordPress
 */

// ** Database settings - You can get this info from your web host ** //
/** The name of the database for WordPress */
define( 'DB_NAME', 'app' );

/** Database username */
define( 'DB_USER', 'user' );

/** Database password */
define( 'DB_PASSWORD', 'secret' );

/** Database hostname */
define( 'DB_HOST', '172.16.10.200' );

/** Database charset to use in creating database tables. */
define( 'DB_CHARSET', 'utf8mb4' );

/** The database collate type. Don't change this if in doubt. */
define( 'DB_COLLATE', '' );

/**#@+
 * Authentication unique keys and salts.
 *
 * Change these to different unique phrases! You can generate these using
 * the {@link https://api.wordpress.org/secret-key/1.1/salt/ WordPress.org secret-key service}.
 *
 * You can change these at any point in time to invalidate all existing cookies.
 * This will force all users to have to log in again.
 *
 * @since 2.6.0
 */
define( 'AUTH_KEY',         '|^1!;LtmJ-U;5+tEy|7J!fAgpkSSoju#5B^D$mB n9PpSg=@WUvCsVAMomf*5Hn;' );
define( 'SECURE_AUTH_KEY',  'F4?iWgq_zv|kck,`wB]9{!dkr_}QoY`7bA?i,{fJ<E:8MCLkTrmdC}m^~ciD ,9i' );
define( 'LOGGED_IN_KEY',    'v3PKhX$vI<mB=-i&tXx4Oujf< 4* wdrNF9u@UgbOtY:v/sbyeycz>P_8%Bj }Ev' );
define( 'NONCE_KEY',        'exl -8YcW?xgPM*i7=6ZbNNG6SIy0W^CX=6)]0%h1`&xj!Zj%3i@#i[@jN)xvT`&' );
define( 'AUTH_SALT',        '*w<j/qm04^S:g#Xk$T)eR_a_}U,RWUz%6/P&U*ar8}kwu9cXzA@H_->LN%$WA$lp' );
define( 'SECURE_AUTH_SALT', '+#R@I_Mi.DoJS6?s?)R}p|swGb0e]{y;/!`KUo:9c9w!t4$l=)D`wAq-A1W*ZW5|' );
define( 'LOGGED_IN_SALT',   'z%(@N!AI~d{3N~5fJ]1K,{?=3xaJA;rvc!BtXfs|V<-;>hWFyuE_Ezd,)Td^93#<' );
define( 'NONCE_SALT',       ']5w$l/63V!@oAEu#n<~>Gn2NSo-@[:pQ)GwD>O+aDkto)PI{3CVCe=jegn3[0[~K' );

/**#@-*/

/**
 * WordPress database table prefix.
 *
 * You can have multiple installations in one database if you give each
 * a unique prefix. Only numbers, letters, and underscores please!
 *
 * At the installation time, database tables are created with the specified prefix.
 * Changing this value after WordPress is installed will make your site think
 * it has not been installed.
 *
 * @link https://developer.wordpress.org/advanced-administration/wordpress/wp-config/#table-prefix
 */
$table_prefix = 'wp_';

/**
 * For developers: WordPress debugging mode.
 *
 * Change this to true to enable the display of notices during development.
 * It is strongly recommended that plugin and theme developers use WP_DEBUG
 * in their development environments.
 *
 * For information on other constants that can be used for debugging,
 * visit the documentation.
 *
 * @link https://developer.wordpress.org/advanced-administration/debug/debug-wordpress/
 */
define( 'WP_DEBUG', true );

/* Add any custom values between this line and the "stop editing" line. */

//define('WPLANG', cms_current_locale_code());

/** Custom wp-content directory */
//define('WP_CONTENT_DIR', dirname(__FILE__) . '/../cms');
//define('WP_CONTENT_URL', 'http://' . $_SERVER['HTTP_HOST'] . '/cms');

if ( defined( 'WP_CLI' ) ) {
    $_SERVER['HTTP_HOST'] = '127.0.0.1';
}

if (isset($_SERVER['HTTP_X_FORWARDED_PROTO']) && $_SERVER['HTTP_X_FORWARDED_PROTO'] === 'https') {
    $_SERVER['HTTPS'] = 'on';
}

define('WP_SITEURL', 'http://' . $_SERVER['HTTP_HOST']);
define('WP_HOME', 'http://' . $_SERVER['HTTP_HOST'] );
define('FS_METHOD', 'direct');

/** App URL */
//define('WP_HOME', 'https://pis-contentmanagementpis-cms-mediacontent.ilsasvi.corp');
//define('WP_SITEURL', 'https://pis-contentmanagementpis-cms-mediacontent.ilsasvi.corp');

/** Disallow file edit */
define('DISALLOW_FILE_EDIT', true);

//define('WP_ALLOW_REPAIR', true);
define('WP_MAX_MEMORY_LIMIT', '128M');

define('DISABLE_WP_CRON', true);
// define('ALTERNATE_WP_CRON', true);

define('FORCE_SSL_ADMIN', false);
//define('FORCE_SSL_LOGIN', true);
//define('FORCE_SSL_CONTENT', true);

/** Disable post revisions */
define('WP_POST_REVISIONS', false);

/** Enable Core auto-updates */
define( 'AUTOMATIC_UPDATER_DISABLED', true );
define( 'WP_AUTO_UPDATE_CORE', false );
//define( 'WP_AUTO_UPDATE_CORE', true);

//define( 'WP_CACHE', false );

//define( 'CONCATENATE_SCRIPTS', false );
//define( 'SCRIPT_DEBUG', true );

//define('ADMIN_COOKIE_PATH', '/');
//define('COOKIE_DOMAIN', '');
//define('COOKIEPATH', '');
//define('SITECOOKIEPATH', '');




/* That's all, stop editing! Happy publishing. */

/** Absolute path to the WordPress directory. */
if ( ! defined( 'ABSPATH' ) ) {
	define( 'ABSPATH', __DIR__ . '/' );
}

/** Sets up WordPress vars and included files. */
require_once ABSPATH . 'wp-settings.php';
