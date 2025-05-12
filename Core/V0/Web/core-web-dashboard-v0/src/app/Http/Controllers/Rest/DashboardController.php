<?php

namespace App\Http\Controllers\Rest;

class DashboardController extends RestController
{
    public function index()
    {
        if (user_is_logged()) {

            $data = [];
            $data['user'] = current_user();

            return view('dashboard/index')->with($data);
        }
        return abort(403);
    }
}
