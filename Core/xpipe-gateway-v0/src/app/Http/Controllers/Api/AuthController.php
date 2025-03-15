<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use App\Http\Requests\Auth\LoginRequest;
use App\Http\Requests\Auth\RefreshTokenRequest;
use App\Http\Requests\Auth\SignupRequest;
use App\Models\User;
use Illuminate\Support\Facades\Http;
use Validator;
use App\Helpers\Auth;

class AuthController extends ApiController
{
    /**
     * Create user
     *
     * @param  [string] name
     * @param  [string] email
     * @param  [string] password
     * @param  [string] password_confirmation
     * @return [string] message
     */
    public function signup(SignupRequest $request)
    {
        $validated = $request->validated();

        $user = new User([
            'name' => $validated['name'],
            'email' => $validated['email'],
            'password' => bcrypt($validated['password'])
        ]);

        $user->save();

        return $this->sendResponse('Successfully created user!', 201);
    }


    /**
     * Signup activation account
     *
     * @param $token
     * @return \Illuminate\Http\JsonResponse
     */
    public function signupActivate($token)
    {
        $user = User::where('activation_token', $token)->first();

        if (!$user) {
            return $this->sendError('This activation token is invalid.', 404);
        }

        $user->active = true;
        $user->activation_token = '';
        $user->save();

        return $user;
    }


    /**
     * Login user and create token
     *
     * @param  [string] email
     * @param  [string] password
     * @param  [boolean] remember_me
     * @return [string] access_token
     * @return [string] expires_in
     * @return [string] refresh_expires_in
     * @return [string] refresh_token
     * @return [string] token_type
     * @return [string] session_state
     * @return [string] scope
     */
    public function login(LoginRequest $request)
    {
        $validated = $request->validated();
        $credentials = [];
        $credentials['email'] = $validated['email'];
        $credentials['password'] = $validated['password'];

        try {
            $form = [];
            $form['username'] = $credentials['email'];
            $form['password'] = $credentials['password'];
            $form['client_id'] = Auth::getAuthClientId();
            $form['client_secret'] = Auth::getAuthClientSecret();
            $form['grant_type'] = 'password';

            $url = Auth::getAuthBaseUrl().'/auth/realms/'.Auth::getAuthRealm().'/protocol/openid-connect/token';

            $response = Http::asForm()->post($url, $form);
            $result = $response->body();
            $resource = json_decode($result);

            if (isset($resource->error)) {
                return $this->sendError('Unauthorized', 401, $resource);
            }

            $data = [];
            $data['access_token'] = $resource->access_token;
            $data['expires_in'] = $resource->expires_in;
            $data['refresh_expires_in'] = $resource->refresh_expires_in;
            $data['refresh_token'] = $resource->refresh_token;
            $data['token_type'] = $resource->token_type;
            //$data['not-before-policy'] = $resource->not-before-policy;
            $data['session_state'] = $resource->session_state;
            $data['scope'] = $resource->scope;

            return $this->sendResponse('User login successfully.', 200, $data);
        } catch (\Exception $e) {
            return $this->sendError('Internal Server Error', 500, array($e));
        }
    }


    /**
     * Logout user (Revoke the token)
     *
     * @return [string] message
     */
    public function logout(Request $request)
    {
        try {
            $form = [];
            $form['client_id'] = Auth::getAuthClientId();
            $form['client_secret'] = Auth::getAuthClientSecret();

            $token = NULL;

            if ($request->headers->has('authorization')) {
                $token = $request->header('authorization');
            }

            // Extract only token value without Bearer
            if (strpos($token, 'Bearer ') !== false) {
                $value = substr($token, strlen('Bearer '));
            }

            $form['token'] = $value;

            $url = Auth::getAuthBaseUrl().'/auth/realms/'.Auth::getAuthRealm().'/protocol/openid-connect/revoke';

            $response = Http::asForm()->post($url, $form);
            $result = $response->body();
            $resource = json_decode($result);

            //if (!isset($resource->active)) {
            //    $errors = [];
            //    $errors['detail'] = "Error during revoke request, authorization token passed is null";
            //    $errors['data'] = $resource;
            //    return $this->sendError('Error during introspect request', 500, $errors);
            //}

            $data = [];
            $data['revoke'] = $resource;

            return $this->sendResponse('Successfully logged out', 200, $data);
        } catch (\Exception $e) {
            return $this->sendError('Internal Server Error', 500, array($e));
        }
    }


    /**
     * Get the authenticated User
     *
     * @return [json] user object
     */
    public function user(Request $request)
    {
        try {
            $form = [];
            $form['client_id'] = Auth::getAuthClientId();
            $form['client_secret'] = Auth::getAuthClientSecret();

            $token = NULL;

            if ($request->headers->has('authorization')) {
                $token = $request->header('authorization');
            }

            // Extract only token value without Bearer
            if (strpos($token, 'Bearer ') !== false) {
                $value = substr($token, strlen('Bearer '));
            }

            $form['token'] = $value;

            $url = Auth::getAuthBaseUrl().'/auth/realms/'.Auth::getAuthRealm().'/protocol/openid-connect/token/introspect';

            $response = Http::asForm()->post($url, $form);
            $result = $response->body();
            $resource = json_decode($result);

            if (!isset($resource->active)) {
                $errors = [];
                $errors['detail'] = "Error during introspect request, authorization token passed is null";
                $errors['data'] = $resource;
                return $this->sendError('Error during introspect request', 500, $errors);
            }

            $url_info = Auth::getAuthBaseUrl().'/auth/realms/'.Auth::getAuthRealm().'/protocol/openid-connect/userinfo';

            $response_info = Http::withHeaders([
                'Content-Type' => 'application/x-www-form-urlencoded',
                'Authorization' => $token,
            ])->get($url_info);

            $result_info = $response_info->body();
            $resource_info = json_decode($result_info);

            if (isset($resource_info->error)) {
                return $this->sendError('Unauthorized', 401, $resource_info);
            }

            $data = [];
            $data['introspect'] = $resource;
            $data['info'] = $resource_info;

            return $this->sendResponse('ok', 200, $data);
        } catch (\Exception $e) {
            return $this->sendError('Internal Server Error', 500, array($e));
        }
    }


    /**
     * Refresh user access token
     *
     * @param RefreshTokenRequest $request
     * @return \Illuminate\Http\JsonResponse|\Illuminate\Http\Response
     */
    public function refresh(RefreshTokenRequest $request)
    {

        $validated = $request->validated();
        $credentials = [];
        $credentials['refresh_token'] = $validated['refresh_token'];

        try {
            $form = [];
            $form['refresh_token'] = $credentials['refresh_token'];
            $form['client_id'] = Auth::getAuthClientId();
            $form['client_secret'] = Auth::getAuthClientSecret();
            $form['grant_type'] = 'refresh_token';

            $url = Auth::getAuthBaseUrl().'/auth/realms/'.Auth::getAuthRealm().'/protocol/openid-connect/token';

            $response = Http::asForm()->post($url, $form);
            $result = $response->body();
            $resource = json_decode($result);

            if (isset($resource->error)) {
                return $this->sendError('Unauthorized', 401, $resource);
            }

            $data = [];
            $data['access_token'] = $resource->access_token;
            $data['expires_in'] = $resource->expires_in;
            $data['refresh_expires_in'] = $resource->refresh_expires_in;
            $data['refresh_token'] = $resource->refresh_token;
            $data['token_type'] = $resource->token_type;
            //$data['not-before-policy'] = $resource->not-before-policy;
            $data['session_state'] = $resource->session_state;
            $data['scope'] = $resource->scope;

            return $this->sendResponse('User Access Token Refreshed', 202, $data);
        } catch (\Exception $e) {
            return $this->sendError('Internal Server Error', 500, array($e));
        }

    }
}