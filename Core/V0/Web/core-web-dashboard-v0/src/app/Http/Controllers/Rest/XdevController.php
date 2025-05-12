<?php

namespace App\Http\Controllers\Rest;

use Illuminate\Http\Request;

class XdevController extends RestController
{

    /**
     * @return \Illuminate\Contracts\Foundation\Application|\Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function cli()
    {
        if (user_is_logged()) {

            $data = [];
            $data['user'] = current_user();

            return view('dashboard/xdev/cli')->with($data);
        }

        abort(401);
    }

    /**
     * @return \Illuminate\Contracts\Foundation\Application|\Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function gui()
    {
        if (user_is_logged()) {

            $data = [];
            $data['user'] = current_user();
            $data['src'] = 'http://xdev-work.sindria.org:8080';

            return view('dashboard/xdev/gui')->with($data);
        }

        abort(401);
    }




}
