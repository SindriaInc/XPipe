<?php

namespace App\Http\Controllers\Auth;

use App\Http\Controllers\Controller;
use App\Providers\RouteServiceProvider;
use Illuminate\Foundation\Auth\AuthenticatesUsers;
use Illuminate\Http\Request;
use App\Models\User;
use Illuminate\Support\Facades\Auth;

class LoginController extends Controller
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
     * Validate the user login request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return void
     *
     * @throws \Illuminate\Validation\ValidationException
     */
    protected function validateLogin(Request $request)
    {

    }

    /**
     * Attempt to log the user into the application.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return bool|void
     */
    protected function attemptLogin(Request $request)
    {
        if (! empty(current_user())) {

            if (! $request->query('u')) {
                abort(401);
            }

            $username = $request->query('u');
            $user = User::where('user_login','=', $username)->first();

            // Login into Laravel
            Auth::loginUsingId($user->ID, TRUE);

            $loggedUser = [];
            $loggedUser['ID'] = $user->ID;
            $loggedUser['user_login'] = $user->user_login;
            $loggedUser['user_pass'] = $user->user_pass;
            $loggedUser['user_nicename'] = $user->user_nicename;
            $loggedUser['user_email'] = $user->user_email;
            $loggedUser['user_url'] = $user->user_url;
            $loggedUser['user_registered'] = $user->user_registered;
            $loggedUser['user_activation_key'] = $user->user_activation_key;
            $loggedUser['user_status'] = $user->user_status;
            $loggedUser['display_name'] = $user->display_name;

            session()->put('logged', true);
            session()->put('logged_user', $loggedUser);

            session()->save();

            return true;
        }

        abort(401);
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
        //dd($request);

        //$request->session()->regenerate();
        //$this->clearLoginAttempts($request);

        //return redirect($this->redirectTo);
        return redirect(env('APP_CMS_BASE_URL'));
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
        if (Auth::check()) {

            $this->guard()->logout();

            session()->remove('logged');
            session()->remove('logged_user');

            $request->session()->invalidate();
            $request->session()->regenerateToken();

            return redirect(env('APP_CMS_BASE_URL') . '/wp-login.php');
        }

        abort(401);
    }
}
