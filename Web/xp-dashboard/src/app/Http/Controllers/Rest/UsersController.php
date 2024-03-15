<?php

namespace App\Http\Controllers\Rest;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Response;

class UsersController extends RestController
{

    /**
     * Default csv separator
     *
     * @var string
     */
    private string $separator = ';';

    /**
     * Show all users table
     *
     * @return \Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function index()
    {
        if (user_is_logged()) {

            try {

                $resource = $this->getWithToken(current_token(), '/api/users');
                $collection = json_decode($resource);

                if ($collection == NULL) {
                    return $this->message('warning_message', 'Errore durante l\'operazione','dashboard');
                }

                $data = [];
                $data['user'] = current_user();
                $data['current_query'] = '';
                $data['users'] = [];

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                if (!empty($collection->data)) {
                    $data['users'] = $collection->data->users;
                }

                return view('dashboard/users/index')->with($data);

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }
        }

        abort(401);
    }


    /**
     * Add user
     *
     * @return \Illuminate\Contracts\Foundation\Application|\Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function add()
    {
        if (user_is_logged()) {

            $data = [];
            $data['user'] = current_user();

            return view('dashboard/users/add')->with($data);
        }

        abort(401);
    }


    /**
     * Store a user
     *
     * @param Request $request
     * @return \Illuminate\Contracts\Foundation\Application|\Illuminate\Contracts\View\Factory|\Illuminate\View\View|mixed|void
     */
    public function store(Request $request)
    {
        if (user_is_logged()) {

            try {

                $input =  [
                    'first_name' => $request->input('name'),
                    'last_name' => $request->input('surname'),
                    'email' => $request->input('email'),
                    'username' => $request->input('username'),
                    'enabled' => $request->input('enabled'),
                ];

                $resource = $this->postWithToken(current_token(), '/api/users/store', $input);
                $collection = json_decode($resource);

                if ($collection == NULL) {
                    return $this->message('warning_message', 'Errore durante l\'operazione','dashboard');
                }

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                return $this->message('success_message', $collection->message,'dashboard.users');

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }


    /**
     * Show all user details by id
     *
     * @param string $username
     * @return \Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function details($username)
    {
        if (user_is_logged()) {

            try {

                $input =  [
                    'username' => $username
                ];

                $resource = $this->postWithToken(current_token(), '/api/users/show/', $input);
                $collection = json_decode($resource);

                //dd($resource);

                $data = [];
                $data['user'] = current_user();
                $data['u'] = [];
                $data['policies'] = [];

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message, 'dashboard');
                }

                if (!empty($collection->data)) {
                    $data['u'] = $collection->data->users[0];
                }

                $resource_policies = $this->getWithToken(current_token(), '/api/v1/policies/user/' . $data['u']->id);
                $collection_policies = json_decode($resource_policies);

                if ($collection_policies->success) {
                    if (!empty($collection_policies->data)) {
                        $data['policies'] = $collection_policies->data->policies;
                    }
                }

                return view('dashboard/users/detail')->with($data);

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }


    /**
     * Show user data for edit
     *
     * @param string $username
     * @return \Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function show($username)
    {
        if (user_is_logged()) {

            try {

                $input =  [
                    'username' => $username
                ];

                $resource = $this->postWithToken(current_token(), '/api/users/show/', $input);
                $collection = json_decode($resource);

                $data = [];
                $data['user'] = current_user();
                $data['u'] = [];

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message, 'dashboard');
                }

                if (!empty($collection->data)) {
                    $data['u'] = $collection->data->users[0];
                }

                return view('dashboard/users/show')->with($data);

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }


    /**
     * Edit a user
     *
     * @param Request $request
     * @return \Illuminate\Contracts\Foundation\Application|\Illuminate\Contracts\View\Factory|\Illuminate\View\View|mixed|void
     */
    public function edit(Request $request, $id)
    {
        if (user_is_logged()) {

            try {

                $input =  [
                    'id'                => $id,
                    'first_name'        => $request->input('name'),
                    'last_name'         => $request->input('surname'),
                    'email'             => $request->input('email'),
                    'username'          => $request->input('username'),
                    'enabled'           => $request->input('enabled'),
                    'email_verified'    => $request->input('email_verified'),
                ];

                $resource = $this->putWithToken(current_token(), '/api/users/edit/', $input);
                $collection = json_decode($resource);


                if ($collection == NULL) {
                    return $this->message('danger_message', 'Errore durante l\'operazione','dashboard');
                }

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                return $this->message('success_message', $collection->message,'dashboard.users');

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }


    /**
     * Destroy a user
     *
     * @param $id
     * @return \Illuminate\Http\RedirectResponse|\Illuminate\Routing\Redirector|void
     */
    public function destroy($id)
    {
        if (user_is_logged()) {

            try {

                $input =  [
                    'id' => $id
                ];

                $resource = $this->deleteWithToken(current_token(), '/api/users/delete', $input);
                $collection = json_decode($resource);

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                return $this->message('success_message', $collection->message,'dashboard.users');

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }


    /**
     * Search users
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
                    return redirect(route('dashboard.users'));
                }

                $resource = $this->postWithToken(current_token(), '/api/users/search', $input);
                $collection = json_decode($resource);

                $data = [];
                $data['user'] = current_user();
                $data['current_query'] = $request->input('query');
                $data['users'] = [];

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message, 'dashboard');
                }

                if (!empty($collection->data)) {
                    $data['users'] = $collection->data->users;
                }

                return view('dashboard/users/index')->with($data);

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }


    /**
     * Export users
     *
     * @return \Symfony\Component\HttpFoundation\StreamedResponse|void
     */
    public function export()
    {
        if (user_is_logged()) {

            try {

                $resource = $this->getWithToken(current_token(), '/api/users');
                $collection = json_decode($resource);

                if ($collection == NULL) {
                    return $this->message('warning_message', 'Errore durante l\'operazione','dashboard');
                }

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                $users = [];

                if (!empty($collection->data)) {
                    $users = $collection->data->users;
                }

                $filename = "users_".now().".csv";

                $headers = [
                    'Content-Type' => 'text/csv',
                    'Content-Disposition' => 'attachment; filename="'.$filename.'"',
                ];

                $callback = function () use ($users) {

                    $out = fopen('php://output', 'w');

                    $caption = [
                        "ID",
                        "Name",
                        "Surname",
                        "Username",
                        "Email",
                    ];
                    fputcsv($out, $caption, $this->separator);

                    foreach ($users as $user) {
                        $formatted_user = [
                            $user->id,
                            $user->firstName,
                            $user->lastName,
                            $user->username,
                            $user->email,
                        ];

                        fputcsv($out, $formatted_user, $this->separator);
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
}
