<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateWpCommentsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('wp_comments', function (Blueprint $table) {
            $table->bigIncrements('comment_ID');
            $table->unsignedBigInteger('comment_post_ID')->default(0)->index('comment_post_ID');
            $table->string('comment_author');
            $table->string('comment_author_email', 100)->default('');
            $table->string('comment_author_url', 200)->default('');
            $table->string('comment_author_IP', 100)->default('');
            $table->dateTime('comment_date')->default('0000-00-00 00:00:00');
            $table->dateTime('comment_date_gmt')->default('0000-00-00 00:00:00')->index('comment_date_gmt');
            $table->text('comment_content');
            $table->integer('comment_karma')->default(0);
            $table->string('comment_approved', 20)->default('1');
            $table->string('comment_agent')->default('');
            $table->string('comment_type', 20)->default('comment');
            $table->unsignedBigInteger('comment_parent')->default(0)->index('comment_parent');
            $table->unsignedBigInteger('user_id')->default(0);
            
            $table->index(['comment_approved', 'comment_date_gmt'], 'comment_approved_date_gmt');
            $table->index(['comment_author_email`(10'], 'comment_author_email');
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('wp_comments');
    }
}
