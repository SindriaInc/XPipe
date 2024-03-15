<?php

use App\Cms\Kernel;
use Illuminate\Http\Request;

require __DIR__.'/../vendor/autoload.php';

$app = require_once __DIR__.'/../bootstrap/app.php';

$kernel = $app->make(\App\Cms\Kernel::class);

$response = $kernel->handle(Request::capture());
