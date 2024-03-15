<?php

namespace App\Http\Controllers\Rest;

class BlogController extends RestController
{
    public function index()
    {
        if (user_is_logged()) {

            $data = [];
            $data['user'] = current_user();

            return view('dashboard/blog/index')->with($data);
        }
        return abort(403);
    }
}
