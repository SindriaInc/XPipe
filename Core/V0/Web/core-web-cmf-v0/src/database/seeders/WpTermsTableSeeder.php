<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;

class WpTermsTableSeeder extends Seeder
{

    /**
     * Auto generated seed file
     *
     * @return void
     */
    public function run()
    {


        \DB::table('wp_terms')->delete();

        \DB::table('wp_terms')->insert(array (
            0 =>
            array (
                'term_id' => 1,
                'name' => 'Uncategorized',
                'slug' => 'uncategorized',
                'term_group' => 0,
            ),
        ));


    }
}
