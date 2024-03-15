<?php

namespace App\Services;

use App\Models\Language;
use App\Traits\Purge;
use Illuminate\Support\Facades\DB;

class LanguageService extends BaseService {

    use Purge;

    /**
     * LanguageService constructor.
     */
    public function __construct(Language $model) {
        parent::__construct($model);
    }

}
