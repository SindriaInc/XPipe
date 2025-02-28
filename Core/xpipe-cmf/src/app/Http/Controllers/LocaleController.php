<?php

namespace App\Http\Controllers;

use App\Http\Requests\ChangeLocale as ChangeLocaleRequest;
use Illuminate\Http\RedirectResponse;

class LocaleController extends Controller
{

    /**
     * Change locale with redirect to current localized url
     *
     * @param ChangeLocaleRequest $request
     * @return \Illuminate\Contracts\Foundation\Application|\Illuminate\Http\RedirectResponse|\Illuminate\Routing\Redirector
     * @throws \Psr\Container\ContainerExceptionInterface
     * @throws \Psr\Container\NotFoundExceptionInterface
     */
    public function changeLocale(ChangeLocaleRequest $request) : RedirectResponse
    {
        $validated = $request->validated();
        $locale = $validated['lang'];
        session()->put('locale', $locale);
        return redirect()->back();
    }
}
