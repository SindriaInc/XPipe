<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;

class WpPostmetaTableSeeder extends Seeder
{

    /**
     * Auto generated seed file
     *
     * @return void
     */
    public function run()
    {


        \DB::table('wp_postmeta')->delete();

        \DB::table('wp_postmeta')->insert(array (
            0 =>
            array (
                'meta_id' => 1,
                'post_id' => 2,
                'meta_key' => '_wp_page_template',
                'meta_value' => 'default',
            ),
            1 =>
            array (
                'meta_id' => 2,
                'post_id' => 3,
                'meta_key' => '_wp_page_template',
                'meta_value' => 'default',
            ),
        ));


    }
}
