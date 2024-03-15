<?php

namespace App\Http\Controllers\Rest;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Response;

class PoliciesController extends RestController
{

    /**
     * Default csv separator
     *
     * @var string
     */
    private string $separator = ';';

    /**
     * Show all policies table
     *
     * @return \Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function index(Request $request)
    {
        if (user_is_logged()) {

            try {

                $offset = $request->query('page') !== NUll ? $request->query('page') : 0;
                $size = $request->query('qty') !== NUll ? $request->query('qty') : session('quantity');

                $real_offset = $offset !== 0 ? $offset - 1 : $offset;

                $resource = $this->getWithToken(current_token(), '/api/v1/policies/paginate?off=' . $real_offset . '&sze=' . $size);
                $collection = json_decode($resource);

                if ($collection == NULL) {
                    return $this->message('warning_message', 'Errore durante l\'operazione','dashboard');
                }

                $data = [];
                $data['user'] = current_user();
                $data['current_query'] = '';
                $data['policies'] = [];

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                if (!empty($collection->data)) {
                    $data['policies'] = $collection->data->policies;
                }

                return view('dashboard/policies/index')->with($data);

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }
        abort(401);
    }


    /**
     * Add policy
     *
     * @return \Illuminate\Contracts\Foundation\Application|\Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function add()
    {
        if (user_is_logged()) {

            try {

                $resource = $this->getWithToken(current_token(), '/api/v1/policies/types');
                $collection = json_decode($resource);

                if ($collection == NULL) {
                    return $this->message('warning_message', 'Errore durante l\'operazione','dashboard');
                }

                $data = [];
                $data['user'] = current_user();
                $data['types'] = [];

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                if (!empty($collection->data)) {
                    $data['types'] = $collection->data->types;
                }

                return view('dashboard/policies/add')->with($data);

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }


    /**
     * Store a policy
     *
     * @param Request $request
     * @return \Illuminate\Contracts\Foundation\Application|\Illuminate\Contracts\View\Factory|\Illuminate\View\View|mixed|void
     */
    public function store(Request $request)
    {
        if (user_is_logged()) {

            try {

                $input =  [
                    'name' => $request->input('name'),
                    'content' => $request->input('content'),
                ];

                $resource = $this->postWithToken(current_token(), '/api/v1/policies', $input);
                $collection = json_decode($resource);

                if ($collection == NULL) {
                    return $this->message('warning_message', 'Errore durante l\'operazione','dashboard');
                }

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                return $this->message('success_message', $collection->message,'dashboard.policies');

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }


    /**
     * Show all policy details by id
     *
     * @param integer $id
     * @return \Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function details($id)
    {
        if (user_is_logged()) {

            try {

                $resource = $this->getWithToken(current_token(), '/api/v1/policies/' . $id);
                $collection = json_decode($resource);

                $data = [];
                $data['user'] = current_user();
                $data['policy'] = [];
                $data['users'] = [];

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message, 'dashboard');
                }

                if (!empty($collection->data)) {
                    $data['policy'] = $collection->data->policy;
                }

                $resource_users = $this->getWithToken(current_token(), '/api/v1/policies/users/' . $id);
                $collection_users = json_decode($resource_users);

                $users = [];

                if ($collection_users->success) {
                    foreach ($collection_users->data->users as $user_id) {
                        $resource_user = $this->getWithToken(current_token(), '/api/users/show/' . $user_id);

                        $collection_user = json_decode($resource_user);

                        if (!$collection_user->success) {
                            return $this->message('danger_message', 'Error during estracting related user data', 'dashboard');
                        }

                        if (!empty($collection_user->data)) {
                            $users[] = $collection_user->data->user;
                        }
                    }
                }

                if (!empty($collection_users->data)) {
                    $data['users'] = $users;
                }

                return view('dashboard/policies/detail')->with($data);

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }
        abort(401);
    }


    /**
     * Show policy data for edit
     *
     * @param integer $id
     * @return \Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function show($id)
    {
        if (user_is_logged()) {

            try {

                $resource = $this->getWithToken(current_token(), '/api/v1/policies/' . $id);
                $collection = json_decode($resource);

                $resource_types = $this->getWithToken(current_token(), '/api/v1/policies/types');
                $collection_types = json_decode($resource_types);

                if ($collection == NULL) {
                    return $this->message('warning_message', 'Errore durante l\'operazione','dashboard');
                }

                if ($collection_types == NULL) {
                    return $this->message('warning_message', 'Errore durante l\'operazione','dashboard');
                }

                $data = [];
                $data['user'] = current_user();
                $data['policy'] = [];
                $data['types'] = [];

                if (!$collection->success) {
                    $this->message('danger_message', $collection->message, 'dashboard');
                }

                if (!$collection_types->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                if (!empty($collection->data)) {
                    $data['policy'] = $collection->data->policy;
                }

                if (!empty($collection_types->data)) {
                    $data['types'] = $collection_types->data->types;
                }

                return view('dashboard/policies/show')->with($data);

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }


    /**
     * Edit a policy
     *
     * @param Request $request
     * @param integer $id
     * @return \Illuminate\Contracts\Foundation\Application|\Illuminate\Contracts\View\Factory|\Illuminate\View\View|mixed|void
     */
    public function edit(Request $request, $id)
    {
        if (user_is_logged()) {

            try {

                $input =  [
                    'name'      => $request->input('name'),
                    'content'   => $request->input('content'),
                ];

                $resource = $this->putWithToken(current_token(), '/api/v1/policies/' . $id, $input);
                $collection = json_decode($resource);

                if ($collection == NULL) {
                    return $this->message('danger_message', 'Errore durante l\'operazione','dashboard');
                }

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                return $this->message('success_message', $collection->message,'dashboard.policies');

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }


    /**
     * Destroy a policy
     *
     * @param integer $id
     * @return \Illuminate\Http\RedirectResponse|\Illuminate\Routing\Redirector|void
     */
    public function destroy($id)
    {
        if (user_is_logged()) {

            try {

                $resource = $this->deleteWithToken(current_token(), '/api/v1/policies/' . $id);
                $collection = json_decode($resource);

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                return $this->message('success_message', $collection->message,'dashboard.policies');

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }
        }
        abort(401);
    }


    /**
     * Search policies
     *
     * @param Request $request
     * @return \Illuminate\Contracts\Foundation\Application|\Illuminate\Contracts\View\Factory|\Illuminate\Http\RedirectResponse|\Illuminate\Routing\Redirector|\Illuminate\View\View|void
     */
    public function search(Request $request)
    {
        if (user_is_logged()) {

            try {

                $input =  [
                    'query' => $request->input('query')
                ];

                if ($request->input('query') == '') {
                    return redirect(route('dashboard.policies'));
                }

                $resource = $this->getWithToken(current_token(), '/api/v1/policies/search?q=' . $input['query']);
                $collection = json_decode($resource);

                $data = [];
                $data['user'] = current_user();
                $data['current_query'] = $input['query'];
                $data['policies'] = [];

                if (!$collection->success) {
                    $this->message('danger_message', $collection->message, 'dashboard');
                }

                if (!empty($collection->data)) {
                    $data['policies'] = $collection->data->policies;
                }

                return view('dashboard/policies/index')->with($data);

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }
        abort(401);
    }


    /**
     * Export policies
     *
     * @return \Symfony\Component\HttpFoundation\StreamedResponse|void
     */
    public function export()
    {
        if (user_is_logged()) {

            try {

                $resource = $this->getWithToken(current_token(), '/api/v1/policies');
                $collection = json_decode($resource);

                if ($collection == NULL) {
                    return $this->message('warning_message', 'Errore durante l\'operazione','dashboard');
                }

                if (!$collection->success) {
                    return $this->message('danger_message',$collection->message,'dashboard');
                }

                $policies = [];

                if (!empty($collection->data)) {
                    $policies = $collection->data->policies;
                }

                $filename = "policies_".now().".csv";

                $headers = [
                    'Content-Type' => 'text/csv',
                    'Content-Disposition' => 'attachment; filename="'.$filename.'"',
                ];

                $callback = function () use ($policies) {

                    $out = fopen('php://output', 'w');

                    $caption = [
                        "ID",
                        "Name",
                        "Content",
                    ];
                    fputcsv($out, $caption, $this->separator);

                    foreach ($policies as $policy) {
                        $formatted_policy = [
                            $policy->id,
                            $policy->name,
                            $policy->content,
                        ];

                        fputcsv($out, $formatted_policy, $this->separator);
                    }
                    fclose($out);
                };

                return Response::stream($callback, 200, $headers);

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }

    /**
     * Show attach policy form
     *
     * @return \Illuminate\Contracts\Foundation\Application|\Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function attach()
    {
        if (user_is_logged()) {

            try {

                $data = [];
                $data['user'] = current_user();
                $data['users'] = [];
                $data['policies'] = [];

                // Get all users
                $resource_users = $this->getWithToken(current_token(), '/api/users');
                $collection_users = json_decode($resource_users);

                if ($collection_users == NULL) {
                    return $this->message('warning_message', 'Errore durante l\'operazione','dashboard');
                }

                if (!$collection_users->success) {
                    return $this->message('danger_message',$collection_users->message,'dashboard');
                }

                // Get all policies
                $resource_policies = $this->getWithToken(current_token(), '/api/v1/policies');
                $collection_policies = json_decode($resource_policies);

                if ($collection_policies == NULL) {
                    return $this->message('warning_message', 'Errore durante l\'operazione','dashboard');
                }

                if (!$collection_policies->success) {
                    return $this->message('danger_message',$collection_policies->message,'dashboard');
                }

                // Get users data
                if (!empty($collection_users->data)) {
                    $data['users'] = $collection_users->data->users;
                }

                // Get policies data
                if (!empty($collection_policies->data)) {
                    $data['policies'] = $collection_policies->data->policies;
                }

                return view('dashboard/policies/attach')->with($data);

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }


    /**
     * Attach policy to a user
     *
     * @param Request $request
     * @return mixed|void
     */
    public function attachStore(Request $request)
    {
        if (user_is_logged()) {

            try {

                $input =  [
                    'userId' => $request->input('user_id'),
                    'policyId' => $request->input('policy_id'),
                ];

                $resource = $this->postWithToken(current_token(), '/api/v1/policies/attach', $input);
                $collection = json_decode($resource);

                if ($collection == NULL) {
                    return $this->message('warning_message', 'Errore durante l\'operazione','dashboard');
                }

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                return $this->message('success_message', $collection->message,'dashboard.policies');

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }


    /**
     * Detach policy from a user
     *
     * @param Request $request
     * @return mixed|void
     */
    public function detach(Request $request)
    {
        if (user_is_logged()) {

            try {

                $input =  [
                    'userId' => $request->input('user_id'),
                    'policyId' => $request->input('policy_id'),
                ];

                $resource = $this->postWithToken(current_token(), '/api/v1/policies/detach', $input);
                $collection = json_decode($resource);

                if ($collection == NULL) {
                    return $this->message('warning_message', 'Errore durante l\'operazione','dashboard');
                }

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                return $this->message('success_message', $collection->message,'dashboard.policies');

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }
}
