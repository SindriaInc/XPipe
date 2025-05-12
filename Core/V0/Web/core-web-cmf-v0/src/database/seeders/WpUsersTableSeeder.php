<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;

class WpUsersTableSeeder extends Seeder
{

    /**
     * Auto generated seed file
     *
     * @return void
     */
    public function run()
    {


        \DB::table('wp_users')->delete();

        \DB::table('wp_users')->insert(array (
            0 =>
            array (
                'ID' => 1,
                'user_login' => 'admin',
                'user_pass' => '$P$Bp.YunLELylpz7zYKe61oywelv/yOz/',
                'user_nicename' => 'admin',
                'user_email' => 'devops@sindria.org',
                'user_url' => 'https://local-demo-xpipe.sindria.org',
                'user_registered' => '2023-01-21 21:37:27',
                'user_activation_key' => '',
                'user_status' => 0,
                'display_name' => 'admin',
            ),
            1 =>
            array (
                'ID' => 2,
                'user_login' => 'operator',
                'user_pass' => '$P$BA8ACpzRDU2DeGH0RRFjvFOh4Rnrd6.',
                'user_nicename' => 'operator',
                'user_email' => 'operator@sindria.org',
                'user_url' => 'https://local-demo-xpipe.sindria.org',
                'user_registered' => '2023-02-15 19:15:10',
                'user_activation_key' => '1676488510:$P$BejlxHPsNzJ2hrmhb5xEsVMCG2Eorc/',
                'user_status' => 0,
                'display_name' => 'operator',
            ),
        ));


    }
}
