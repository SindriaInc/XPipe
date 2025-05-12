<?php

namespace App\Http\Controllers\Rest;

class PagesController extends RestController
{
    public function index()
    {
        if (user_is_logged()) {

            $data = [];
            $data['user'] = current_user();

            return view('dashboard/pages/index')->with($data);
        }
        return abort(403);
    }

    public function structure()
    {
        if (user_is_logged()) {

            $data = [];
            $data['user'] = current_user();

            return view('dashboard/pages/structure')->with($data);
        }
        return abort(403);
    }
}
