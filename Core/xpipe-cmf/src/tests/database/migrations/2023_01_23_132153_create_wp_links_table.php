<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateWpLinksTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('wp_links', function (Blueprint $table) {
            $table->bigIncrements('link_id');
            $table->string('link_url')->default('');
            $table->string('link_name')->default('');
            $table->string('link_image')->default('');
            $table->string('link_target', 25)->default('');
            $table->string('link_description')->default('');
            $table->string('link_visible', 20)->default('Y')->index('link_visible');
            $table->unsignedBigInteger('link_owner')->default(1);
            $table->integer('link_rating')->default(0);
            $table->dateTime('link_updated')->default('0000-00-00 00:00:00');
            $table->string('link_rel')->default('');
            $table->mediumText('link_notes');
            $table->string('link_rss')->default('');
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('wp_links');
    }
}
