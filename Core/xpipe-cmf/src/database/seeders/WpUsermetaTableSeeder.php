<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;

class WpUsermetaTableSeeder extends Seeder
{

    /**
     * Auto generated seed file
     *
     * @return void
     */
    public function run()
    {


        \DB::table('wp_usermeta')->delete();

        \DB::table('wp_usermeta')->insert(array (
            0 =>
            array (
                'umeta_id' => 1,
                'user_id' => 1,
                'meta_key' => 'nickname',
                'meta_value' => 'admin',
            ),
            1 =>
            array (
                'umeta_id' => 2,
                'user_id' => 1,
                'meta_key' => 'first_name',
                'meta_value' => '',
            ),
            2 =>
            array (
                'umeta_id' => 3,
                'user_id' => 1,
                'meta_key' => 'last_name',
                'meta_value' => '',
            ),
            3 =>
            array (
                'umeta_id' => 4,
                'user_id' => 1,
                'meta_key' => 'description',
                'meta_value' => '',
            ),
            4 =>
            array (
                'umeta_id' => 5,
                'user_id' => 1,
                'meta_key' => 'rich_editing',
                'meta_value' => 'true',
            ),
            5 =>
            array (
                'umeta_id' => 6,
                'user_id' => 1,
                'meta_key' => 'syntax_highlighting',
                'meta_value' => 'true',
            ),
            6 =>
            array (
                'umeta_id' => 7,
                'user_id' => 1,
                'meta_key' => 'comment_shortcuts',
                'meta_value' => 'false',
            ),
            7 =>
            array (
                'umeta_id' => 8,
                'user_id' => 1,
                'meta_key' => 'admin_color',
                'meta_value' => 'fresh',
            ),
            8 =>
            array (
                'umeta_id' => 9,
                'user_id' => 1,
                'meta_key' => 'use_ssl',
                'meta_value' => '0',
            ),
            9 =>
            array (
                'umeta_id' => 10,
                'user_id' => 1,
                'meta_key' => 'show_admin_bar_front',
                'meta_value' => 'true',
            ),
            10 =>
            array (
                'umeta_id' => 11,
                'user_id' => 1,
                'meta_key' => 'locale',
                'meta_value' => '',
            ),
            11 =>
            array (
                'umeta_id' => 12,
                'user_id' => 1,
                'meta_key' => 'wp_capabilities',
                'meta_value' => 'a:1:{s:13:"administrator";b:1;}',
            ),
            12 =>
            array (
                'umeta_id' => 13,
                'user_id' => 1,
                'meta_key' => 'wp_user_level',
                'meta_value' => '10',
            ),
            13 =>
            array (
                'umeta_id' => 14,
                'user_id' => 1,
                'meta_key' => 'dismissed_wp_pointers',
                'meta_value' => '',
            ),
            14 =>
            array (
                'umeta_id' => 15,
                'user_id' => 1,
                'meta_key' => 'show_welcome_panel',
                'meta_value' => '0',
            ),
            15 =>
            array (
                'umeta_id' => 16,
                'user_id' => 1,
                'meta_key' => 'session_tokens',
            'meta_value' => 'a:1:{s:64:"fbffbdf8cdc70f860a3b393b4567d22c0333e14a449a265ed62d43a598fd6df7";a:4:{s:10:"expiration";i:1676661253;s:2:"ip";s:11:"10.10.200.1";s:2:"ua";s:70:"Mozilla/5.0 (X11; Linux x86_64; rv:108.0) Gecko/20100101 Firefox/108.0";s:5:"login";i:1676488453;}}',
            ),
            16 =>
            array (
                'umeta_id' => 17,
                'user_id' => 1,
                'meta_key' => 'wp_dashboard_quick_press_last_post_id',
                'meta_value' => '4',
            ),
            17 =>
            array (
                'umeta_id' => 18,
                'user_id' => 1,
                'meta_key' => 'community-events-location',
                'meta_value' => 'a:1:{s:2:"ip";s:11:"10.10.200.0";}',
            ),
            18 =>
            array (
                'umeta_id' => 19,
                'user_id' => 1,
                'meta_key' => 'meta-box-order_dashboard',
                'meta_value' => 'a:4:{s:6:"normal";s:41:"dashboard_site_health,dashboard_right_now";s:4:"side";s:21:"dashboard_quick_press";s:7:"column3";s:17:"dashboard_primary";s:7:"column4";s:18:"dashboard_activity";}',
            ),
            19 =>
            array (
                'umeta_id' => 20,
                'user_id' => 1,
                'meta_key' => '_new_email',
                'meta_value' => 'a:2:{s:4:"hash";s:32:"e96fe8d02fb425d2356e7efaa8f1a37c";s:8:"newemail";s:22:"devops@sindria.org";}',
            ),
            20 =>
            array (
                'umeta_id' => 21,
                'user_id' => 2,
                'meta_key' => 'nickname',
                'meta_value' => 'operator',
            ),
            21 =>
            array (
                'umeta_id' => 22,
                'user_id' => 2,
                'meta_key' => 'first_name',
                'meta_value' => '',
            ),
            22 =>
            array (
                'umeta_id' => 23,
                'user_id' => 2,
                'meta_key' => 'last_name',
                'meta_value' => '',
            ),
            23 =>
            array (
                'umeta_id' => 24,
                'user_id' => 2,
                'meta_key' => 'description',
                'meta_value' => '',
            ),
            24 =>
            array (
                'umeta_id' => 25,
                'user_id' => 2,
                'meta_key' => 'rich_editing',
                'meta_value' => 'true',
            ),
            25 =>
            array (
                'umeta_id' => 26,
                'user_id' => 2,
                'meta_key' => 'syntax_highlighting',
                'meta_value' => 'true',
            ),
            26 =>
            array (
                'umeta_id' => 27,
                'user_id' => 2,
                'meta_key' => 'comment_shortcuts',
                'meta_value' => 'false',
            ),
            27 =>
            array (
                'umeta_id' => 28,
                'user_id' => 2,
                'meta_key' => 'admin_color',
                'meta_value' => 'fresh',
            ),
            28 =>
            array (
                'umeta_id' => 29,
                'user_id' => 2,
                'meta_key' => 'use_ssl',
                'meta_value' => '0',
            ),
            29 =>
            array (
                'umeta_id' => 30,
                'user_id' => 2,
                'meta_key' => 'show_admin_bar_front',
                'meta_value' => 'true',
            ),
            30 =>
            array (
                'umeta_id' => 31,
                'user_id' => 2,
                'meta_key' => 'locale',
                'meta_value' => '',
            ),
            31 =>
            array (
                'umeta_id' => 32,
                'user_id' => 2,
                'meta_key' => 'wp_capabilities',
                'meta_value' => 'a:1:{s:8:"operator";b:1;}',
            ),
            32 =>
            array (
                'umeta_id' => 33,
                'user_id' => 2,
                'meta_key' => 'wp_user_level',
                'meta_value' => '0',
            ),
            33 =>
            array (
                'umeta_id' => 34,
                'user_id' => 2,
                'meta_key' => 'dismissed_wp_pointers',
                'meta_value' => '',
            ),
        ));


    }
}
