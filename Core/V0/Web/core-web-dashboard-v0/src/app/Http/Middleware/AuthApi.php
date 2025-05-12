<?php

namespace App\Http\Middleware;

use Closure;
use App\Traits\Messages;
use App\Traits\Rest;

class AuthApi
{
    use Messages, Rest;

    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    public function handle($request, Closure $next)
    {

//        try {
//
//            $user_response = $this->getWithToken(current_token(), '/api/auth/user');
//            $user_resource = json_decode($user_response);
//
//            if (! $user_resource->success) {
//
//                $this->getWithToken(current_token(), '/api/auth/logout');
//
//                session()->remove('access_token');
//                session()->remove('refresh_token');
//                session()->remove('expires_at');
//                session()->remove('refresh_at');
//                session()->remove('refresh_count');
//                session()->remove('logged_user');
//
//                $request->session()->invalidate();
//                $request->session()->regenerateToken();
//
//            }
//
//        } catch (\Exception $e) {
//            $this->message('danger_message', 'Fatal Error','dashboard', array($e));
//        }

        // User not logged in
        if (! session()->has('access_token')) {
            //return $this->message('info_message', 'Per favore autenticati qua grazie','login');
            return redirect(route('login'));
        }


        // Session idle after range 10-15 minutes without user input
        if (token_is_expired()) {

            // User not logged in
            if (! session()->has('access_token')) {
                return redirect(route('login'));
            }

            // Force user logout
            session()->remove('access_token');
            session()->remove('refresh_token');
            session()->remove('expires_at');
            session()->remove('refresh_at');
            session()->remove('refresh_count');
            session()->remove('logged_user');

            $request->session()->invalidate();
            $request->session()->regenerateToken();
        }

        // Refresh current access token every 5 minutes
        if (token_need_refresh()) {

            // User not logged in
            if (! session()->has('access_token')) {
                return redirect(route('login'));
            }

            $form = [];
            $form['refresh_token'] = current_refresh_token();

            $response = $this->put('/api/auth/refresh', $form);
            $resource = json_decode($response);

            if ($resource->success) {
                session()->put('access_token', $resource->data->access_token);
                session()->put('refresh_token', $resource->data->refresh_token);
                session()->put('expires_at', now()->addSeconds($resource->data->expires_in));
                session()->put('refresh_at', now()->addSeconds($resource->data->expires_in / 3));
                session()->put('refresh_count', current_refresh_count() + 1);

            }

        }

        // Debug verbose
        //$this->message('warning_message', 'Access Token Expired ' . current_refresh_count(),'dashboard');


        return $next($request);
    }
}
