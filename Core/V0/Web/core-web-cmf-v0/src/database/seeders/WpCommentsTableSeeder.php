<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;

class WpCommentsTableSeeder extends Seeder
{

    /**
     * Auto generated seed file
     *
     * @return void
     */
    public function run()
    {


        \DB::table('wp_comments')->delete();

        \DB::table('wp_comments')->insert(array (
            0 =>
            array (
                'comment_ID' => 1,
                'comment_post_ID' => 1,
                'comment_author' => 'A WordPress Commenter',
                'comment_author_email' => 'wapuu@wordpress.example',
                'comment_author_url' => 'https://wordpress.org/',
                'comment_author_IP' => '',
                'comment_date' => '2023-01-21 21:37:27',
                'comment_date_gmt' => '2023-01-21 21:37:27',
                'comment_content' => 'Hi, this is a comment.
To get started with moderating, editing, and deleting comments, please visit the Comments screen in the dashboard.
Commenter avatars come from <a href="https://en.gravatar.com/">Gravatar</a>.',
                'comment_karma' => 0,
                'comment_approved' => '1',
                'comment_agent' => '',
                'comment_type' => 'comment',
                'comment_parent' => 0,
                'user_id' => 0,
            ),
        ));


    }
}
