<?php

namespace App\Services;

use App\Models\Voice;
use App\Traits\Purge;
use Illuminate\Support\Facades\DB;

class VoiceService extends BaseService {

    use Purge;

    /**
     * VoiceService constructor.
     */
    public function __construct(Voice $model) {
        parent::__construct($model);
    }

}
