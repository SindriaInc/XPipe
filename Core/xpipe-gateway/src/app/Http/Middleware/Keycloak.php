<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Support\Facades\Http;
use App\Traits\Api;
use App\Helpers\Auth;

class Keycloak
{
    use Api;

    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    public function handle($request, Closure $next)
    {

        try {
            $form = [];
            $form['client_id'] = Auth::getAuthClientId();
            $form['client_secret'] = Auth::getAuthClientSecret();

            $token = NULL;
            $value = NULL;

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

            if (isset($resource->error)) {
                return $this->sendError('Unauthorized', 401, $resource);
            }

            if (isset($resource->active)) {
                if (!$resource->active) {
                    return $this->sendError('Unauthorized', 401);
                }


                $userUid = $resource->sub;

                // call default attach ProfilePolicy endpoint
                $url_policies_default = env('POLICIES_SERVICE').'/api/v1/policies/default/attach/' . $userUid;
                $response_policies_default = Http::timeout(3)->get($url_policies_default);
                $result_policies_default = $response_policies_default->body();
                $resource_policies_default = json_decode($result_policies_default);

                // Exception
                if (! $resource_policies_default->success) {
                    return $this->sendError('Unespected error during default policy checking', 500, array());
                }


                // check policies for current authenticated user
                //return $this->checkPolicies($request, $resource->sub);

                // Check user policy for current uri and method
                $url_policies_verify = env('POLICIES_SERVICE').'/api/v1/policies/verify';
                $params = [];
                $params['uid'] = $userUid;
                $params['uri'] = $request->getRequestUri();
                $params['mtd'] = $request->getMethod();

                $response_policies_verify = Http::timeout(3)->get($url_policies_verify, $params);
                $result_policies_verify = $response_policies_verify->body();
                $resource_policies_verify = json_decode($result_policies_verify);

                // Exception
                if (! isset($resource_policies_verify->data->response)) {
                    return $this->sendError('Unespected error during policy validation', 500, array());
                }

                // Forbidden
                if (! $resource_policies_verify->data->response->hasAccess) {
                    return $this->sendError('Forbidden', 403);
                }


            }

        } catch (\Exception $e) {

            $data = [];
            $data['details'] = $e->getMessage();

            return $this->sendError('Internal Server Error', 500, $data);
        }

        return $next($request);
    }


    /**
     * Check policies for current authenticated user
     *
     * @param $request
     * @param $userUid
     * @return \Illuminate\Http\JsonResponse|\Illuminate\Http\Response|void
     */
    private function checkPolicies($request, $userUid)
    {
        // Check user policy for current uri and method
        $url = env('POLICIES_SERVICE').'/api/v1/policies/verify';
        $params = [];
        $params['uid'] = $userUid;
        $params['uri'] = $request->getRequestUri();
        $params['mtd'] = $request->getMethod();

        $response = Http::timeout(3)->get($url, $params);
        $result = $response->body();
        $resource = json_decode($result);

        if (! isset($resource->data->response)) {
            return $this->sendError('Unespected error during policy validation', 500, array());
        }

        if (! $resource->data->response->hasAccess) {
            return $this->sendError('Forbidden', 403);
        }
    }
}