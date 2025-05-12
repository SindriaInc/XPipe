<?php

namespace App\Services;

use App\Models\Category;
use App\Traits\Purge;
use Illuminate\Support\Facades\DB;

class CategoryService extends BaseService {

    use Purge;

    /**
     * CategoryService constructor.
     */
    public function __construct(Category $model) {
        parent::__construct($model);
    }

}
