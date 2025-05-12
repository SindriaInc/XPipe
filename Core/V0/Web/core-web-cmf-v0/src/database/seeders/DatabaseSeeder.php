<?php

namespace Database\Seeders;

use App\Services\FileMetaService;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     *
     * @return void
     */
    public function run()
    {
        // \App\Models\User::factory(10)->create();

        // WP
        $this->call(WpOptionsTableSeeder::class);
        $this->call(WpCommentmetaTableSeeder::class);
        $this->call(WpCommentsTableSeeder::class);
        $this->call(WpLinksTableSeeder::class);
        $this->call(WpPostmetaTableSeeder::class);
        $this->call(WpPostsTableSeeder::class);
        $this->call(WpTermmetaTableSeeder::class);
        $this->call(WpTermRelationshipsTableSeeder::class);
        $this->call(WpTermsTableSeeder::class);
        $this->call(WpTermTaxonomyTableSeeder::class);
        $this->call(WpUsermetaTableSeeder::class);
        $this->call(WpUsersTableSeeder::class);
        // Custom

    }
}
