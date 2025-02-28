<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use App\Http\Requests\Users\ShowUserRequest;
use App\Http\Requests\Users\AddUserRequest;
use App\Http\Requests\Users\EditUserRequest;
use App\Http\Requests\Users\DeleteUserRequest;
use App\Http\Requests\Users\SearchUserRequest;
use Illuminate\Support\Facades\Http;
use App\Helpers\Auth;

class UsersController extends ApiController
{

    /**
     * Get all users
     *
     * @param Request $request
     * @return \Illuminate\Http\JsonResponse|\Illuminate\Http\Response
     */
    public function index(Request $request)
    {
        try {
            $token = NULL;

            if ($request->headers->has('authorization')) {
                $token = $request->header('authorization');
            }

            $url = Auth::getAuthBaseUrl().'/auth/admin/realms/'.Auth::getAuthRealm().'/users';

            $response = Http::withHeaders([
                'Content-Type' => 'application/x-www-form-urlencoded',
                'Authorization' => $token,
            ])->get($url);

            $result = $response->body();
            $resource = json_decode($result);

            if (isset($resource->error)) {
                return $this->sendError('Unauthorized', 401, $resource);
            }

            $data = [];
            $data['users'] = $resource;

            return $this->sendResponse('ok', 200, $data);
        } catch (\Exception $e) {
            return $this->sendError('Internal Server Error', 500, array($e));
        }
    }


    /**
     * Show a user by username
     *
     * @param ShowUserRequest $request
     * @return \Illuminate\Http\JsonResponse|\Illuminate\Http\Response
     */
    public function showUserByUsername(ShowUserRequest $request)
    {
        $validated = $request->validated();

        try {
            $token = NULL;

            if ($request->headers->has('authorization')) {
                $token = $request->header('authorization');
            }

            $url = Auth::getAuthBaseUrl().'/auth/admin/realms/'.Auth::getAuthRealm().'/users?username='. $validated['username'];

            $response = Http::withHeaders([
                'Content-Type' => 'application/x-www-form-urlencoded',
                'Authorization' => $token,
            ])->get($url);

            $result = $response->body();
            $resource = json_decode($result);

            if (isset($resource->error)) {
                return $this->sendError('Unauthorized', 401, $resource);
            }

            $data = [];
            $data['user'] = $resource['0'];

            return $this->sendResponse('ok', 200, $data);
        } catch (\Exception $e) {
            return $this->sendError('Internal Server Error', 500, array($e));
        }
    }


    /**
     * Show a user by id
     *
     * @param Request $request
     * @param $id
     * @return \Illuminate\Http\JsonResponse|\Illuminate\Http\Response
     */
    public function showUserById(Request $request, $id)
    {
        try {
            $token = NULL;

            if ($request->headers->has('authorization')) {
                $token = $request->header('authorization');
            }

            $url = Auth::getAuthBaseUrl().'/auth/admin/realms/'.Auth::getAuthRealm().'/users';

            $response = Http::withHeaders([
                'Content-Type' => 'application/x-www-form-urlencoded',
                'Authorization' => $token,
            ])->get($url);

            $result = $response->body();
            $resource = json_decode($result);

            if (isset($resource->error)) {
                return $this->sendError('Unauthorized', 401, $resource);
            }

            $data = [];
            $data['user'] = '';

            foreach ($resource as $user) {
                if ($user->id == $id) {
                    $data['user'] = $user;
                }
            }

            if ($data['user'] == '') {
                return $this->sendError('Not Found', 404);
            }

            return $this->sendResponse('ok', 200, $data);
        } catch (\Exception $e) {
            return $this->sendError('Internal Server Error', 500, array($e));
        }
    }


    /**
     * Store a user
     *
     * @param AddUserRequest $request
     * @return \Illuminate\Http\JsonResponse|\Illuminate\Http\Response
     */
    public function store(AddUserRequest $request)
    {
        $validated = $request->validated();

        try {
            $form = [];
            $form['firstName'] = $validated['first_name'];
            $form['lastName'] = $validated['last_name'];
            $form['email'] = $validated['email'];
            $form['username'] = $validated['username'];

            // Convert enabled boolean into string
            if ($validated['enabled']) {
                $form['enabled'] = "true";
            } else {
                $form['enabled'] = "false";
            }

            $token = NULL;

            if ($request->headers->has('authorization')) {
                $token = $request->header('authorization');
            }

            $url = Auth::getAuthBaseUrl().'/auth/admin/realms/'.Auth::getAuthRealm().'/users';
            $payload = json_encode($form);

            $response = Http::withHeaders([
                //'Content-Type' => 'application/json',
                'Authorization' => $token,
            ])->withBody($payload, 'application/json')->post($url);

            $result = $response->body();
            $resource = json_decode($result);

            if (isset($resource->error)) {
                return $this->sendError('Unauthorized', 401, $resource);
            }

            // User already exist
            if (isset($resource->errorMessage)) {
                return $this->sendError($resource->errorMessage, 409, $resource);
            }

            $data = [];
            $data['user'] = $resource;

            return $this->sendResponse('User added successfully', 201, $data);
        } catch (\Exception $e) {
            return $this->sendError('Internal Server Error', 500, array($e));
        }
    }


    /**
     * Edit a user
     *
     * @param EditUserRequest $request
     * @return \Illuminate\Http\JsonResponse|\Illuminate\Http\Response
     */
    public function edit(EditUserRequest $request)
    {
        $validated = $request->validated();

        try {
            $form = [];
            $form['id'] = $validated['id'];
            $form['firstName'] = $validated['first_name'];
            $form['lastName'] = $validated['last_name'];
            $form['email'] = $validated['email'];
            $form['username'] = $validated['username'];

            // Convert enabled boolean into string
            if ($validated['enabled']) {
                $form['enabled'] = "true";
            } else {
                $form['enabled'] = "false";
            }

            // Convert email_verified boolean into string
            if ($validated['email_verified']) {
                $form['emailVerified'] = "true";
            } else {
                $form['emailVerified'] = "false";
            }

            $token = NULL;

            if ($request->headers->has('authorization')) {
                $token = $request->header('authorization');
            }

            $url = Auth::getAuthBaseUrl().'/auth/admin/realms/'.Auth::getAuthRealm().'/users/' . $validated['id'];
            $payload = json_encode($form);

            $response = Http::withHeaders([
                //'Content-Type' => 'application/json',
                'Authorization' => $token,
            ])->withBody($payload, 'application/json')->put($url);

            $result = $response->body();
            $resource = json_decode($result);

            if (isset($resource->error)) {
                return $this->sendError('Unauthorized', 401, $resource);
            }

            // Catch conflicts
            if (isset($resource->errorMessage)) {
                return $this->sendError($resource->errorMessage, 409, $resource);
            }

            $data = [];
            $data['user'] = $resource;

            return $this->sendResponse('User edited successfully', 202, $data);
        } catch (\Exception $e) {
            return $this->sendError('Internal Server Error', 500, array($e));
        }
    }


    /**
     * Delete a user
     *
     * @param DeleteUserRequest $request
     * @return \Illuminate\Http\JsonResponse|\Illuminate\Http\Response
     */
    public function delete(DeleteUserRequest $request)
    {
        $validated = $request->validated();

        try {
            $token = NULL;

            if ($request->headers->has('authorization')) {
                $token = $request->header('authorization');
            }

            $url = Auth::getAuthBaseUrl().'/auth/admin/realms/'.Auth::getAuthRealm().'/users/' . $validated['id'];

            $response = Http::withHeaders([
                //'Content-Type' => 'application/json',
                'Authorization' => $token,
            ])->delete($url);

            $result = $response->body();
            $resource = json_decode($result);

            if (isset($resource->error)) {
                return $this->sendError('Unauthorized', 401, $resource);
            }

            // Catch conflicts
            if (isset($resource->errorMessage)) {
                return $this->sendError($resource->errorMessage, 409, $resource);
            }

            $data = [];
            $data['user'] = $resource;

            return $this->sendResponse('User deleted successfully', 202, $data);
        } catch (\Exception $e) {
            return $this->sendError('Internal Server Error', 500, array($e));
        }
    }


    /**
     * Search users
     *
     * @param SearchUserRequest $request
     * @return \Illuminate\Http\JsonResponse|\Illuminate\Http\Response
     */
    public function search(SearchUserRequest $request)
    {
        $validated = $request->validated();

        try {
            $token = NULL;

            if ($request->headers->has('authorization')) {
                $token = $request->header('authorization');
            }

            $url = Auth::getAuthBaseUrl().'/auth/admin/realms/'.Auth::getAuthRealm().'/users?search='. $validated['query'];

            $response = Http::withHeaders([
                'Content-Type' => 'application/x-www-form-urlencoded',
                'Authorization' => $token,
            ])->get($url);

            $result = $response->body();
            $resource = json_decode($result);

            if (isset($resource->error)) {
                return $this->sendError('Unauthorized', 401, $resource);
            }

            $data = [];
            $data['users'] = $resource;

            return $this->sendResponse('ok', 200, $data);
        } catch (\Exception $e) {
            return $this->sendError('Internal Server Error', 500, array($e));
        }
    }

}