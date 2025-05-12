<?php

namespace App\Http\Controllers;

use App\Http\Requests\ChangeLocale as ChangeLocaleRequest;

class LocaleController extends Controller
{

    public function changeLocale(ChangeLocaleRequest $request) {

        $validated = $request->validated();

        $locale = $validated['lang'];

        session()->put('locale', $locale);

        return redirect()->back();
    }
}
