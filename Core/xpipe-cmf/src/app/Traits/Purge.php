<?php

namespace App\Traits;

use Illuminate\Support\Facades\DB;

trait Purge
{

    /**
     * Delete all records from $model->getTable()
     * and reset auto increment
     */
    public function purge() {
        DB::transaction(function() {
            $table = $this->model->getTable();

            DB::statement("DELETE FROM " . $table);
            DB::statement("ALTER TABLE " . $table . " AUTO_INCREMENT = 1");
        });
    }


}
