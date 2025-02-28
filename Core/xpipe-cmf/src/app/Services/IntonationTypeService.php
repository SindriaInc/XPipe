<?php

namespace App\Services;

use App\Models\IntonationType;
use App\Traits\Purge;
use Illuminate\Support\Facades\DB;

class IntonationTypeService extends BaseService {

    use Purge;

    /**
     * IntonationTypeService constructor.
     */
    public function __construct(IntonationType $model) {
        parent::__construct($model);
    }

}
