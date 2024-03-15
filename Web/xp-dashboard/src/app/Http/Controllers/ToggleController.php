<?php

namespace App\Http\Controllers;

use App\Http\Requests\ToggleRequest;

class ToggleController extends Controller
{
    public function checkToggle(ToggleRequest $request) {

        $validated = $request->validated();

        $value = $validated['value'];

        if ($value == 'false') {
            $toggle = false;
        } else if ($value == 'true') {
            $toggle = true;
        } else {
            $toggle = NULL;
        }

        session()->put('toggled', $toggle);

        return redirect()->back();
    }
}
