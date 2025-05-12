<?php

namespace App\Services;

use App\Models\Gender;
use App\Traits\Purge;
use Illuminate\Support\Facades\DB;

class GenderService extends BaseService {

    use Purge;

    /**
     * GenderService constructor.
     */
    public function __construct(Gender $model) {
        parent::__construct($model);
    }

}
