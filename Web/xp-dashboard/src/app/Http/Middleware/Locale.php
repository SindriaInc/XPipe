<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Support\Facades\App;

class Locale
{


    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    public function handle($request, Closure $next) {

        if (session()->has('locale')) {
            $locale = session()->get('locale', config('app.locale'));
        } else {
            $locale = substr($request->server('HTTP_ACCEPT_LANGUAGE'), 0, 2);
            session()->put('locale', $locale);

            if (!in_array($locale, config('app.locales'))) {
                $locale = config('app.locale');
            }
        }

        App::setLocale($locale);

        return $next($request);
    }
}
