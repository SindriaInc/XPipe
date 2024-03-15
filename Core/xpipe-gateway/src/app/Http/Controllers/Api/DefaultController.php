<?php

namespace App\Http\Controllers\Api;

class DefaultController extends ApiController
{
    /**
     * Default index handler
     *
     * @return \Illuminate\Http\JsonResponse|\Illuminate\Http\Response
     */
    public function index()
    {
        return $this->sendResponse('API Gateway is running', '200');
    }
}