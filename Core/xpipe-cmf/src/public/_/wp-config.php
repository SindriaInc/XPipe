<?php
/**
 * The base configuration for WordPress
 *
 * The wp-config.php creation script uses this file during the installation.
 * You don't have to use the web site, you can copy this file to "wp-config.php"
 * and fill in the values.
 *
 * This file contains the following configurations:
 *
 * * Database settings
 * * Secret keys
 * * Database table prefix
 * * ABSPATH
 *
 * @link https://wordpress.org/support/article/editing-wp-config-php/
 *
 * @package WordPress
 */

// ** Database settings - You can get this info from your web host ** //
/** The name of the database for WordPress */
define( 'DB_NAME', getenv('XPIPE_CORE_CMF_DB_DATABASE') );

/** Database username */
define( 'DB_USER', getenv('XPIPE_CORE_CMF_DB_USERNAME') );

/** Database password */
define( 'DB_PASSWORD', getenv('XPIPE_CORE_CMF_DB_PASSWORD') );

/** Database hostname */
define( 'DB_HOST', getenv('XPIPE_CORE_CMF_DB_HOST') );

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
define( 'AUTH_KEY',         'ePd9/r{tO)z;(25a`UeKtA(h|_/1e]s:5fYH-qI[TdtRG;@& jDz$S!!l+RvShI8' );
define( 'SECURE_AUTH_KEY',  ')M36vK)%>ywi_>I 16;8J>eh(#HD-f1:glq:6$XxPL>b[g@|B&a>.(dY:tNJn}1E' );
define( 'LOGGED_IN_KEY',    'E-1)kRE(7[ejbk2?dmVZ^vIJ+`~J~S8sfIkyFVx&npg[xQqKgFj8aO-@aohT9Bi*' );
define( 'NONCE_KEY',        '(%B&vC(= l-2Ir-d{s5Axgb/a2UFszK~P:}esT:p%(>X&Q(JVKQ.%GZAd<pv120D' );
define( 'AUTH_SALT',        'Dw9N::10c1tW279u%~]S[[n~RRaC!fQ-<=;H<re67]8ue]_`HHqidAW8Y9b!wX&w' );
define( 'SECURE_AUTH_SALT', 'j^[Aok+I%B6+AQ9%G3=m;^oHKL!ITQanm^<dx0Y<4Vt.d=3U-=Qe8n[!KNDlc`)&' );
define( 'LOGGED_IN_SALT',   ';NeHTNiZmb[^f-g||ikR>$:=d(l*,rq%WQ^@PKi,geR?-Vdv++aj-POp[5TGL$m4' );
define( 'NONCE_SALT',       '{2$g2+$ibl,pe5i Pk10g]$x;#g3m_9Nt@W`tQTdwy{dBXkzH3pEpB}899D|hHTn' );

/**#@-*/

/**
 * WordPress database table prefix.
 *
 * You can have multiple installations in one database if you give each
 * a unique prefix. Only numbers, letters, and underscores please!
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
 * @link https://wordpress.org/support/article/debugging-in-wordpress/
 */
define( 'WP_DEBUG', false );

/* Add any custom values between this line and the "stop editing" line. */

//define('WPLANG', cms_current_locale_code());

/** Custom wp-content directory */
define('WP_CONTENT_DIR', dirname(__FILE__) . '/../cms');
define('WP_CONTENT_URL', 'http://' . $_SERVER['HTTP_HOST'] . '/cms');

if ( defined( 'WP_CLI' ) ) {
    $_SERVER['HTTP_HOST'] = '127.0.0.1';
}

if (isset($_SERVER['HTTP_X_FORWARDED_PROTO']) && $_SERVER['HTTP_X_FORWARDED_PROTO'] === 'https') {
    $_SERVER['HTTPS'] = 'on';
}

define('WP_SITEURL', 'http://' . $_SERVER['HTTP_HOST'] . '/_');
define('WP_HOME', 'http://' . $_SERVER['HTTP_HOST'] . '/_');
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
