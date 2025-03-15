<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use Illuminate\Contracts\Routing\ResponseFactory;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Facades\Http;
use App\Helpers\Auth;

class AdminController extends ApiController
{

   public function index(Request $request)
   {
       try {
           $login = $this->adminLogin();
           $token = $login['access_token'];

           $url = Auth::getAuthBaseUrl().'/auth/admin/realms';

           $response = Http::withHeaders([
               'Content-Type' => 'application/x-www-form-urlencoded',
           ])->withToken($token)->get($url);

           $result = $response->body();
           $resource = json_decode($result);

           if (isset($resource->error)) {
               return $this->sendError('Unauthorized', 401, $resource);
           }

           $data = [];
           $data['realms'] = $resource;

           $logout = $this->adminLogout($token);

           return $this->sendResponse('ok', 200, $data);
       } catch (\Exception $e) {
           return $this->sendError('Internal Server Error', 500, array($e));
       }
   }

    public function realms(Request $request)
    {
        try {
            $login = $this->adminLogin();
            $token = $login['access_token'];

            $url = Auth::getAuthBaseUrl().'/auth/admin/realms';

            $response = Http::withHeaders([
                'Content-Type' => 'application/x-www-form-urlencoded',
            ])->withToken($token)->get($url);

            $result = $response->body();
            $resource = json_decode($result);

            if (isset($resource->error)) {
                return $this->sendError('Unauthorized', 401, $resource);
            }

            $realms = [];

            foreach ($resource as $key => $value) {
                $realms[$key]['íd'] = $value->id;
                $realms[$key]['realm'] = $value->realm;
            }

            $data = [];
            $data['realms'] = $realms;

            $logout = $this->adminLogout($token);

            return $this->sendResponse('ok', 200, $data);
        } catch (\Exception $e) {
            return $this->sendError('Internal Server Error', 500, array($e));
        }
    }

   private function adminLogin() : array
   {
       $form = [];
       $form['username'] = Auth::getAuthAdminUsername();
       $form['password'] = Auth::getAuthAdminPassword();
       $form['client_id'] = Auth::getAuthAdminClientId();
       $form['client_secret'] = Auth::getAuthAdminClientSecret();
       $form['grant_type'] = 'password';

       $url = Auth::getAuthBaseUrl().'/auth/realms/'.Auth::getAuthAdminRealm().'/protocol/openid-connect/token';

       $response = Http::asForm()->post($url, $form);
       $result = $response->body();
       $resource = json_decode($result);

       //if (isset($resource->error)) {
       //    return $this->sendError('Unauthorized', 401, $resource);
       //}

       $data = [];
       $data['access_token'] = $resource->access_token;
       $data['expires_in'] = $resource->expires_in;
       $data['refresh_expires_in'] = $resource->refresh_expires_in;
       $data['refresh_token'] = $resource->refresh_token;
       $data['token_type'] = $resource->token_type;
       //$data['not-before-policy'] = $resource->not-before-policy;
       $data['session_state'] = $resource->session_state;
       $data['scope'] = $resource->scope;

       return $data;
   }

   private function adminLogout($token) : array
   {
       $form = [];
       $form['client_id'] = Auth::getAuthAdminClientId();
       $form['client_secret'] = Auth::getAuthAdminClientSecret();
       $form['token'] = $token;

       $url = Auth::getAuthBaseUrl().'/auth/realms/'.Auth::getAuthAdminRealm().'/protocol/openid-connect/revoke';

       $response = Http::asForm()->post($url, $form);
       $result = $response->body();
       $resource = json_decode($result);

       $data = [];
       $data['revoke'] = $resource;

       return $data;
   }

}