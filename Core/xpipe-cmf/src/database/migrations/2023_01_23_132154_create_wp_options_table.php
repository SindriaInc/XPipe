<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateWpOptionsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('wp_options', function (Blueprint $table) {
            $table->bigIncrements('option_id');
            $table->string('option_name', 191)->default('')->unique('option_name');
            $table->longText('option_value');
            $table->string('autoload', 20)->default('yes')->index('autoload');
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('wp_options');
    }
}
