<?php

namespace App\Services;

use App\Models\Point;
use App\Traits\Purge;
use Illuminate\Support\Facades\DB;

class PointService extends BaseService {

    use Purge;

    /**
     * PointService constructor.
     */
    public function __construct(Point $model) {
        parent::__construct($model);
    }

}
