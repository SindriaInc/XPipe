<?php

namespace App\Http\Controllers\Rest;

class GalleryController extends RestController
{
    public function index()
    {
        if (user_is_logged()) {

            $data = [];
            $data['user'] = current_user();

            return view('dashboard/gallery/index')->with($data);
        }
        return abort(403);
    }
}
