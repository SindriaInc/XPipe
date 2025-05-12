<?php

namespace App\Services;

use App\Models\ServiceCategory;
use App\Traits\Purge;
use Illuminate\Support\Facades\DB;

class ServiceCategoryService extends BaseService {

    use Purge;

    /**
     * ServiceCategoryService constructor.
     */
    public function __construct(ServiceCategory $model) {
        parent::__construct($model);
    }

}
