<?php

namespace App\Http\Controllers\Auth;

use App\Http\Controllers\Rest\RestController;
use App\Providers\RouteServiceProvider;
use Illuminate\Foundation\Auth\AuthenticatesUsers;
use Illuminate\Http\Request;

class LoginController extends RestController
{
    /*
    |--------------------------------------------------------------------------
    | Login Controller
    |--------------------------------------------------------------------------
    |
    | This controller handles authenticating users for the application and
    | redirecting them to your home screen. The controller uses a trait
    | to conveniently provide its functionality to your applications.
    |
    */

    use AuthenticatesUsers;

    /**
     * Where to redirect users after login.
     *
     * @var string
     */
    protected $redirectTo = RouteServiceProvider::HOME;


    /**
     * Create a new controller instance.
     *
     * @return void
     */
    public function __construct()
    {
        $this->middleware('guest')->except('logout');
    }


    /**
     * Show the application's login form.
     *
     * @override
     * @return \Illuminate\Contracts\View\Factory|\Illuminate\View\View
     */
    public function showLoginForm()
    {
        return view('auth.dashboard.login');
    }


    /**
     * Attempt to log the user into the application.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return bool
     */
    protected function attemptLogin(Request $request)
    {
        $data = [];
        $data['email'] = $request->input('email');
        $data['password'] = $request->input('password');
        $data['remember_me'] = $request->filled('remember');

        $response = $this->auth('/api/auth/login', $data);
	    $resource = json_decode($response);

        if ($resource->success) {
            session()->put('access_token', $resource->data->access_token);
            session()->put('refresh_token', $resource->data->refresh_token);
            session()->put('expires_at', now()->addSeconds($resource->data->expires_in));
            session()->put('refresh_at', now()->addSeconds($resource->data->expires_in / 3));
            session()->put('refresh_count', 0);

            $resource = $this->getWithToken(session('access_token'), '/api/auth/user');
            $user = json_decode($resource);
            session()->put('logged_user', $user->data);

            return true;
        }
        return false;
    }


    /**
     * Send the response after the user was authenticated.
     *
     * @override
     * @param  \Illuminate\Http\Request  $request
     * @return mixed
     */
    protected function sendLoginResponse(Request $request)
    {
        $request->session()->regenerate();
        $this->clearLoginAttempts($request);
        return redirect(route('dashboard'));
    }


    /**
     * Log the user out of the application.
     *
     * @override
     * @param  \Illuminate\Http\Request  $request
     * @return mixed|void
     */
    public function logout(Request $request)
    {
        if (user_is_logged()) {

            $this->getWithToken(current_token(), '/api/auth/logout');

            session()->remove('access_token');
            session()->remove('refresh_token');
            session()->remove('expires_at');
            session()->remove('refresh_at');
            session()->remove('refresh_count');
            session()->remove('logged_user');

            $request->session()->invalidate();
            $request->session()->regenerateToken();

            return redirect('/');
        }

        abort(401);
    }
}
