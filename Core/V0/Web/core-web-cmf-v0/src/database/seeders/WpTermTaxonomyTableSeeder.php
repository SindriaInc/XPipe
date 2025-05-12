<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;

class WpTermTaxonomyTableSeeder extends Seeder
{

    /**
     * Auto generated seed file
     *
     * @return void
     */
    public function run()
    {


        \DB::table('wp_term_taxonomy')->delete();

        \DB::table('wp_term_taxonomy')->insert(array (
            0 =>
            array (
                'term_taxonomy_id' => 1,
                'term_id' => 1,
                'taxonomy' => 'category',
                'description' => '',
                'parent' => 0,
                'count' => 1,
            ),
        ));


    }
}
