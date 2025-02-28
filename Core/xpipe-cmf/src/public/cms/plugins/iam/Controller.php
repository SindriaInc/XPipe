<?php

namespace Sindria\Iam;

use Illuminate\Http\Request;
use App\Traits\Rest;
use App\Traits\Messages;
use App\Traits\Routing;

use Sindria\Toolkit\BaseHelper;

use Illuminate\Support\Facades\Response;
use Psr\Container\ContainerExceptionInterface;
use Psr\Container\NotFoundExceptionInterface;

use Sindria\Iam\ViewModel\UsersViewModel;
use Sindria\Iam\ViewModel\UserInfoViewModel;
use Sindria\Iam\ViewModel\UserAddViewModel;
use Sindria\Iam\ViewModel\UserShowViewModel;

use Sindria\Iam\ViewModel\PoliciesViewModel;
use Sindria\Iam\ViewModel\PolicyInfoViewModel;
use Sindria\Iam\ViewModel\PolicyAddViewModel;
//use Sindria\Iam\ViewModel\PolicyShowViewModel;
use Sindria\Iam\ViewModel\PolicyAttachViewModel;
use Sindria\Iam\ViewModel\PolicyDetachViewModel;

class Controller
{
    use Rest, Messages, Routing;

    /**
     * @var View $view
     */
    protected View $view;

    /**
     * @var Service $service
     */
    protected Service $service;

    /**
     * Default csv separator
     *
     * @var string $separator
     */
    private string $separator = ';';

    /**
     * Controller constructor
     *
     * @param View $view
     * @param Service $service
     */
    public function __construct(View $view, Service $service)
    {
        $this->view = $view;
        $this->service = $service;
    }

    /**
     * Show users datatable
     *
     * @param Request $request
     * @return mixed
     */
    public function users(Request $request)
    {
        try {
            $token = access_token();
            $response = $this->getWithToken($token, '/api/users');
            $resource = json_decode($response, false);

            $viewModel = UsersViewModel::getInstance();
            $viewModel(collect($resource->data->users));

            return $this->view->render('users');

        } catch (\Exception $e) {
            return $this->transMessage('danger_message', 'api', 'Service not available');
        }

    }

    /**
     * Show users datatable filter by search query
     *
     * @param Request $request
     * @return bool|mixed
     * @throws ContainerExceptionInterface
     * @throws NotFoundExceptionInterface
     */
    public function usersSearch(Request $request)
    {
        try {
            $query = $request->post('s');
            $queryString = trim(filter_var($query, FILTER_DEFAULT, [FILTER_FLAG_STRIP_HIGH, FILTER_FLAG_STRIP_LOW]));

            if (!$queryString) {
                //return $this->transMessage('danger_message', 'global', 'search.error.parameter', 'iam');
                return $this->redirect('iam');
            }

            $token = access_token();
            $response = $this->postWithToken($token, '/api/users/search', ['query' => $queryString]);
            $resource = json_decode($response, false);

            if ($resource == NULL) {
                return $this->transMessage('warning_message', 'api', 'Problem during operation', 'iam');
            }

            if (!$resource->success) {
                return $this->transMessage('danger_message', 'api', $resource->message, 'iam');
            }

            $viewModel = UsersViewModel::getInstance();
            $viewModel(collect($resource->data->users));

            return $this->view->render('users');

        } catch (\Throwable $exception) {
            return $this->transMessage('danger_message', 'api', 'Service not available');
        }
    }

    /**
     * Export users into csv format
     *
     * @param Request $request
     * @return mixed
     */
    public function usersExport(Request $request)
    {
        try {
            $token = access_token();
            $response = $this->getWithToken($token, '/api/users');
            $resource = json_decode($response, false);

            //dd($resource);


//            if ($resource == NULL) {
//                return $this->transMessage('warning_message', 'dashboard','filemanager.export.warning','file-manager');
//            }
//
//            if (!$resource->success) {
//                return $this->message('danger_message', trans($resource->message),'dashboard');
//            }

            $users = [];

            if (!empty($resource->data)) {
                $users = $resource->data->users;
            }

            $filename = "users_".now().".csv";

            $headers = [
                'Content-Type' => 'text/csv',
                'Content-Disposition' => 'attachment; filename="'.$filename.'"',
            ];

            $callback = function () use ($users) {

                $out = fopen('php://output', 'w');

                $caption = [
                    trans('iam.users.field.id'),
                    trans('iam.users.field.username'),
                    trans('iam.users.field.email'),
                    trans('iam.users.field.name'),
                    trans('iam.users.field.surname'),
                ];
                fputcsv($out, $caption, $this->separator);

                foreach ($users as $user) {
                    $formatted_file = [
                        $user->id,
                        $user->username,
                        $user->email,
                        $user->firstName,
                        $user->lastName,
                    ];

                    fputcsv($out, $formatted_file, $this->separator);
                }
                fclose($out);
            };


            // TODO: Debug response stream on MVVM router


            $result = $callback();
            //return $result;

            //header('Content-Type:text/csv');
            //header('Content-Disposition:attachment; filename="'.$filename.'"');


            //$test = Response::stream($callback, 200, $headers);
            //dd($test);

            //echo $test;

            return $this->redirect('iam');

            //return $test;


        } catch (\Exception $e) {
            return $this->transMessage('danger_message', 'api', 'Problem during operation', 'iam');
        }

    }


    /**
     * Show details about a user
     *
     * @param Request $request
     * @return mixed
     */
    public function detailsUser(Request $request)
    {
        try {
            $id = $request->query('id');

            $token = access_token();

            // Get user data
            $responseUser = $this->getWithToken($token, '/api/users/show/' . $id);
            $resourceUser = json_decode($responseUser, false);

            if ($resourceUser == NULL) {
                return $this->transMessage('warning_message', 'api', 'Problem during operation', 'iam');
            }

            if (!$resourceUser->success) {
                return $this->transMessage('danger_message', 'api', $resourceUser->message, 'iam');
            }

            // Get user policies
            $responsePolicies = $this->getWithToken($token, '/api/v1/policies/user/' . $id);
            $resourcePolicies = json_decode($responsePolicies, false);

            if ($resourcePolicies == NULL) {
                return $this->transMessage('warning_message', 'api', 'Problem during operation', 'iam');
            }

            if (!$resourcePolicies->success) {
                return $this->transMessage('danger_message', 'api', $resourcePolicies->message, 'iam');
            }

            $data = new \stdClass();
            $data->user = $resourceUser->data->user;
            $data->policies = collect(new \stdClass());

            if (isset($resourcePolicies->data->policies)) {
                $data->policies = collect($resourcePolicies->data->policies);
            }

            $user = $this->service->findProfileByUsername($data->user->username);

            if (! $user) {
                $meta = new \stdClass();
                $meta->jobTitle = '';
            } else {
                $meta = new \stdClass();
                $meta->jobTitle = $this->service->findUserMeta($user->ID, 'job_title');
            }

            $data->meta = $meta;

            $viewModel = UserInfoViewModel::getInstance();
            $viewModel($data);

            return $this->view->render('details-user');

        } catch (\Throwable $e) {
            return $this->transMessage('danger_message', 'api', 'Service not available', 'iam');
        }

    }


    /**
     * Show add new user form
     *
     * @param Request $request
     * @return mixed
     */
    public function addUser(Request $request)
    {
        try {
            $data = new \stdClass();

            $viewModel = UserAddViewModel::getInstance();
            $viewModel($data);

            return $this->view->render('add-user');

        } catch (\Exception $e) {
            return $this->transMessage('danger_message', 'api', 'Service not available', 'iam');
        }

    }

    /**
     * Store a new user
     *
     * @param Request $request
     * @return mixed
     */
    public function storeUser(Request $request)
    {
        try {

            $input =  [
                'first_name' => $request->input('name'),
                'last_name' => $request->input('surname'),
                'email' => $request->input('email'),
                'username' => $request->input('user_login'),
                'enabled' => $request->input('enabled'),
            ];

            $token = access_token();
            $response = $this->postWithToken($token, '/api/users/store', $input);
            $resource = json_decode($response, false);

            if ($resource == NULL) {
                return $this->transMessage('warning_message', 'api', 'Problem during operation', 'iam');
            }

            if (!$resource->success) {

                // Validation errors
                if ($resource->message == 'validation error') {
                    return $this->transMessage('validation', 'api', json_encode($resource->data->messages), 'add-user');
                }

                return $this->transMessage('danger_message', 'api', $resource->message, 'add-user');
            }

            $query = [];
            $query['username'] = $request->input('user_login');

            $responseUser = $this->postWithToken($token, '/api/users/show', $query);
            $resourceUser = json_decode($responseUser, false);

            if ($resourceUser == NULL) {
                return $this->transMessage('warning_message', 'api', 'Problem during operation', 'iam');
            }

            if (!$resourceUser->success) {
                return $this->transMessage('danger_message', 'api', $resourceUser->message, 'iam');
            }

            $user = $resourceUser->data->user;

            // Checking profile on dashboard DB
            $profile = $this->service->findProfileByUsername($user->username);

            if (! $profile) {
                $profileId = $this->service->createUserProfile($user->username, $user->email, $user->firstName, $user->lastName);
            } else {
                $profileId = $profile->ID;
            }

            $meta = [
                'job_title' => $request->input('job_title'),
            ];

            $this->service->saveOrUpdateUserMeta($profileId, 'job_title', $meta['job_title']);

            return $this->transMessage('success_message', 'api', $resource->message, 'iam');

        } catch (\Exception $e) {
            return $this->transMessage('danger_message', 'api', 'Problem during operation', 'iam');
        }
    }

    /**
     * Show existing user data on edit form
     *
     * @param Request $request
     * @return mixed
     */
    public function showUser(Request $request)
    {
        try {
            $id = $request->query('id');

            $token = access_token();
            $response = $this->getWithToken($token, '/api/users/show/' . $id);
            $resource = json_decode($response, false);

            $data = new \stdClass();
            $data->user = $resource->data->user;

            $user = $this->service->findProfileByUsername($data->user->username);

            if (! $user) {
                $meta = new \stdClass();
                $meta->jobTitle = '';
            } else {
                $meta = new \stdClass();
                $meta->jobTitle = $this->service->findUserMeta($user->ID, 'job_title');
            }

            $data->meta = $meta;

            $viewModel = UserShowViewModel::getInstance();
            $viewModel($data);

            return $this->view->render('show-user');
        } catch (\Exception $e) {
            return $this->transMessage('danger_message', 'api', 'Service not available', 'iam');
        }

    }

    /**
     * Edit an existing user
     *
     * @param Request $request
     * @return mixed
     */
    public function editUser(Request $request)
    {
        try {
            $uuid = $request->input('id');

            $input =  [
                'id' => $uuid,
                'first_name' => $request->input('name'),
                'last_name' => $request->input('surname'),
                'email' => $request->input('email'),
                'username' => $request->input('user_login'),
                'enabled' => $request->input('enabled'),
                'email_verified' => $request->input('email_verified'),
            ];

            $token = access_token();
            $response = $this->putWithToken($token, '/api/users/edit', $input);
            $resource = json_decode($response, false);

            if ($resource == NULL) {
                return $this->transMessage('warning_message', 'api', 'Problem during operation', 'iam');
            }

            if (!$resource->success) {

                // Validation errors
                if ($resource->message == 'validation error') {
                    return $this->transMessage('validation', 'api', json_encode($resource->data->messages), 'show-user', ['id' => $uuid]);
                }

                return $this->transMessage('danger_message', 'api', $resource->message, 'show-user', ['id' => $uuid]);
            }

            // Checking profile on dashboard DB
            $profile = $this->service->findProfileByUsername($input['username']);

            if (! $profile) {
                $profileId = $this->service->createUserProfile($input['username'], $input['email'], $input['first_name'], $input['last_name']);
            } else {
                $id = $profile->ID;
                $profileId = $this->service->updateUserProfile($id, $input['username'], $input['email'], $input['first_name'], $input['last_name']);
            }

            $meta = [
                'job_title' => $request->input('job_title'),
            ];

            $this->service->saveOrUpdateUserMeta($profileId, 'job_title', $meta['job_title']);

            return $this->transMessage('success_message', 'api', $resource->message, 'iam');

        } catch (\Exception $e) {
            return $this->transMessage('danger_message', 'api', 'Service not available', 'iam');
        }

    }

    /**
     * Delete a user
     *
     * @param Request $request
     * @return mixed
     */
    public function deleteUser(Request $request)
    {
        try {
            $uuid = $request->input('id');

            $token = access_token();
            $response = $this->getWithToken($token, '/api/users/show/' . $uuid);
            $resource = json_decode($response, false);

            if ($resource == NULL) {
                return $this->transMessage('warning_message', 'api', 'Problem during operation', 'iam');
            }

            if (!$resource->success) {
                return $this->transMessage('danger_message', 'api', $resource->message, 'iam');
            }

            $user = $resource->data->user;

            $responseDelete = $this->deleteWithToken($token, '/api/users/delete', ['id' => $uuid]);
            $resourceDelete = json_decode($responseDelete, false);

            if ($resourceDelete == NULL) {
                return $this->transMessage('warning_message', 'api', 'Problem during operation', 'iam');
            }

            if (!$resourceDelete->success) {
                return $this->transMessage('danger_message', 'api', $resourceDelete->message, 'iam');
            }

            // Checking profile on dashboard DB
            $profile = $this->service->findProfileByUsername($user->username);

            if (! $profile) {
                return $this->transMessage('warning_message', 'api', $resourceDelete->message . ' but profile doesn\'t exists', 'iam');
            } else {
                $check = $this->service->deleteUserProfile($profile->ID);

                if (! $check) {
                    return $this->transMessage('warning_message', 'api', 'Problem during delete profile operation', 'iam');
                }
            }

            return $this->transMessage('success_message', 'api', $resourceDelete->message, 'iam');

        } catch (\Exception $e) {
            return $this->transMessage('danger_message', 'api', 'Service not available', 'iam');
        }

    }


    // ################### POLICIES ###################


    /**
     * Show policies datatable
     *
     * @param Request $request
     * @return mixed
     */
    public function policies(Request $request)
    {
        try {
            $token = access_token();
            $response = $this->getWithToken($token, '/api/v1/policies');
            $resource = json_decode($response, false);

            $viewModel = PoliciesViewModel::getInstance();
            $viewModel(collect($resource->data->policies));

            return $this->view->render('policies');

        } catch (\Exception $e) {
            return $this->transMessage('danger_message', 'api', 'Service not available');
        }
    }


    /**
     * Show policies datatable filter by search query
     *
     * @param Request $request
     * @return bool|mixed
     * @throws ContainerExceptionInterface
     * @throws NotFoundExceptionInterface
     */
    public function policiesSearch(Request $request)
    {
        try {
            $query = $request->post('s');
            $queryString = trim(filter_var($query, FILTER_DEFAULT, [FILTER_FLAG_STRIP_HIGH, FILTER_FLAG_STRIP_LOW]));

            if (!$queryString) {
                //return $this->transMessage('danger_message', 'global', 'search.error.parameter', 'policies');
                return $this->redirect('policies');
            }

            $token = access_token();
            $response = $this->getWithToken($token, '/api/v1/policies/search?q=' . $queryString);
            $resource = json_decode($response, false);

            if ($resource == NULL) {
                return $this->transMessage('warning_message', 'api', 'Problem during operation', 'policies');
            }

            if (!$resource->success) {
                return $this->transMessage('danger_message', 'api', $resource->message, 'policies');
            }

            $viewModel = PoliciesViewModel::getInstance();
            $viewModel(collect($resource->data->policies->content));

            return $this->view->render('policies');

        } catch (\Throwable $exception) {
            return $this->transMessage('danger_message', 'api', 'Service not available');
        }
    }

    /**
     * Export policies into csv format
     *
     * @param Request $request
     * @return mixed
     */
    public function policiesExport(Request $request)
    {
        // TODO: implement
    }


    /**
     * Show details about a policy
     *
     * @param Request $request
     * @return mixed
     */
    public function detailsPolicy(Request $request)
    {
        try {
            $idEncoded = $request->query('id');
            $id = BaseHelper::decodeSequence($idEncoded);

            $token = access_token();
            $response = $this->getWithToken($token, '/api/v1/policies/' . $id);
            $resource = json_decode($response, false);

            if ($resource == NULL) {
                return $this->transMessage('warning_message', 'api', 'Problem during operation', 'policies');
            }

            if (!$resource->success) {
                return $this->transMessage('danger_message', 'api', $resource->message, 'policies');
            }

            $viewModel = PolicyInfoViewModel::getInstance();
            $viewModel($resource->data->policy);

            return $this->view->render('details-policy');

        } catch (\Throwable $e) {
            return $this->transMessage('danger_message', 'api', 'Service not available', 'iam');
        }

    }


    /**
     * Show add new policy form
     *
     * @param Request $request
     * @return mixed
     */
    public function addPolicy(Request $request)
    {
        try {
            $data = new \stdClass();

            $viewModel = PolicyAddViewModel::getInstance();
            $viewModel($data);

            return $this->view->render('add-policy');

        } catch (\Exception $e) {
            return $this->transMessage('danger_message', 'api', 'Service not available', 'policies');
        }

    }


    /**
     * Store a new policy
     *
     * @param Request $request
     * @return mixed
     */
    public function storePolicy(Request $request)
    {
        // TODO: implement

        dd($request->input());
    }


    /**
     * Show existing policy data on edit form
     *
     * @param Request $request
     * @return mixed
     */
    public function showPolicy(Request $request)
    {
        // TODO: implement
    }


    /**
     * Edit an existing policy
     *
     * @param Request $request
     * @return mixed
     */
    public function editPolicy(Request $request)
    {
        // TODO: implement
    }


    /**
     * Delete a policy
     *
     * @param Request $request
     * @return mixed
     */
    public function deletePolicy(Request $request)
    {
        // TODO: implement
    }


    /**
     * Show attach policy form
     *
     * @param Request $request
     * @return mixed
     */
    public function attachPolicy(Request $request)
    {
        try {

            $token = access_token();

            // Get all users
            $responseUsers = $this->getWithToken($token, '/api/users');
            $resourceUsers = json_decode($responseUsers, false);

            if ($resourceUsers == NULL) {
                return $this->transMessage('warning_message', 'api', 'Problem during operation', 'policies');
            }

            if (!$resourceUsers->success) {
                return $this->transMessage('danger_message', 'api', $resourceUsers->message, 'policies');
            }

            // Get all policies
            $responsePolicies = $this->getWithToken($token, '/api/v1/policies');
            $resourcePolicies = json_decode($responsePolicies, false);

            if ($resourcePolicies == NULL) {
                return $this->transMessage('warning_message', 'api', 'Problem during operation', 'policies');
            }

            if (!$resourcePolicies->success) {
                return $this->transMessage('danger_message', 'api', $resourcePolicies->message, 'policies');
            }


            $data = new \stdClass();
            $data->users = $resourceUsers->data->users;
            $data->policies = $resourcePolicies->data->policies;

            $viewModel = PolicyAttachViewModel::getInstance();
            $viewModel($data);

            return $this->view->render('attach-policy');

        } catch (\Exception $e) {
            return $this->transMessage('danger_message', 'api', 'Service not available', 'policies');
        }
    }


    /**
     * Attach policy to a user
     *
     * @param Request $request
     * @return mixed
     */
    public function attachStorePolicy(Request $request)
    {
        try {

            $input =  [
                'userId' => $request->input('user_id'),
                'policyId' => $request->input('policy_id'),
            ];

            // TODO: implement multi-select for policy_id

            $token = access_token();
            $response = $this->postWithToken($token, '/api/v1/policies/attach', $input);
            $resource = json_decode($response, false);

            if ($resource == NULL) {
                return $this->transMessage('warning_message', 'api', 'Problem during operation', 'policies');
            }

            if (!$resource->success) {
                return $this->transMessage('danger_message', 'api', $resource->message, 'attach-policy');
            }

            return $this->transMessage('success_message', 'api', $resource->message, 'details-user', ['id' => $input['userId']]);

        } catch (\Exception $e) {
            return $this->transMessage('danger_message', 'api', 'Service not available', 'policies');
        }
    }


    /**
     * Show detach policy form
     *
     * @param Request $request
     * @return mixed
     */
    public function detachPolicy(Request $request)
    {
        try {
            $data = new \stdClass();

            $viewModel = PolicyDetachViewModel::getInstance();
            $viewModel($data);

            return $this->view->render('detach-policy');

        } catch (\Exception $e) {
            return $this->transMessage('danger_message', 'api', 'Service not available', 'policies');
        }
    }


    /**
     * Detach policy from a user
     *
     * @param Request $request
     * @return mixed
     */
    public function detachStorePolicy(Request $request)
    {
        try {

            $input =  [
                'userId' => $request->input('user_id'),
                'policyId' => $request->input('policy_id'),
            ];

            $token = access_token();
            $response = $this->postWithToken($token, '/api/v1/policies/detach', $input);
            $resource = json_decode($response, false);

            if ($resource == NULL) {
                return $this->transMessage('warning_message', 'api', 'Problem during operation', 'policies');
            }

            if (!$resource->success) {
                return $this->transMessage('danger_message', 'api', $resource->message, 'detach-policy');
            }

            return $this->transMessage('success_message', 'api', $resource->message, 'policies');

        } catch (\Exception $e) {
            return $this->transMessage('danger_message', 'api', 'Service not available', 'policies');
        }
    }




}
