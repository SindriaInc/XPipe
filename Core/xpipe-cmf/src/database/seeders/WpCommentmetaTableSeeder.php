<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;

class WpCommentmetaTableSeeder extends Seeder
{

    /**
     * Auto generated seed file
     *
     * @return void
     */
    public function run()
    {


        \DB::table('wp_commentmeta')->delete();



    }
}
