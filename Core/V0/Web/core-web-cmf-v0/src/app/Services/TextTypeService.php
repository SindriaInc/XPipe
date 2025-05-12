<?php

namespace App\Services;

use App\Models\TextType;
use App\Traits\Purge;
use Illuminate\Support\Facades\DB;

class TextTypeService extends BaseService {

    use Purge;

    /**
     * TextTypeService constructor.
     */
    public function __construct(TextType $model) {
        parent::__construct($model);
    }

}
