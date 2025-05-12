<?php

namespace App\Services;
use App\Models\FileManager;
use App\Traits\Purge;
use Illuminate\Support\Facades\DB;

class FileManagerService extends BaseService {

    use Purge;

    /**
     * FileManagerService constructor.
     */
    public function __construct(FileManager $model) {
        parent::__construct($model);
    }

}
