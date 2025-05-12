<?php

namespace App\Http\Controllers\Rest;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Response;

class PipelinesController extends RestController
{
    /**
     * Default csv separator
     *
     * @var string
     */
    private string $separator = ';';

    /**
     * Show all pipelines table
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

                $resource = $this->getWithToken(current_token(), '/api/v1/pipelines/paginate?off=' . $real_offset . '&sze=' . $size);
                $collection = json_decode($resource);

                if ($collection == NULL) {
                    return $this->message('warning_message', 'Errore durante l\'operazione','dashboard');
                }

                $data = [];
                $data['user'] = current_user();
                $data['current_query'] = '';
                $data['pipelines'] = [];

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                if (!empty($collection->data)) {
                    $data['pipelines'] = $collection->data->pipelines;
                }

                return view('dashboard/pipelines/index')->with($data);
            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }
        }
        abort(401);
    }

    /**
     * Add pipeline
     *
     * @return \Illuminate\Contracts\Foundation\Application|\Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function add()
    {
        if (user_is_logged()) {

            $data = [];
            $data['user'] = current_user();

            return view('dashboard/pipelines/add')->with($data);
        }

        abort(401);
    }

    /**
     * Store a pipeline
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

                $resource = $this->postWithToken(current_token(), '/api/v1/pipelines', $input);
                $collection = json_decode($resource);

                if ($collection == NULL) {
                    return $this->message('warning_message', 'Errore durante l\'operazione','dashboard');
                }

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                return $this->message('success_message', $collection->message,'dashboard.pipelines');

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }


    /**
     * Show all pipeline details by uuid
     *
     * @param integer $uuid
     * @return \Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function details($uuid)
    {
        if (user_is_logged()) {

            try {

                $resource = $this->getWithToken(current_token(), '/api/v1/pipelines/' . $uuid);
                $collection = json_decode($resource);

                $data = [];
                $data['user'] = current_user();
                $data['pipeline'] = [];
                $data['users'] = [];

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message, 'dashboard');
                }

                if (!empty($collection->data)) {
                    $data['pipeline'] = $collection->data->pipeline;
                }

                $resource_users = $this->getWithToken(current_token(), '/api/v1/pipelines/users/' . $collection->data->pipeline->id);
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

                return view('dashboard/pipelines/detail')->with($data);

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }
        abort(401);
    }

    /**
     * Show pipeline data for edit
     *
     * @param integer $uuid
     * @return \Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function show($uuid)
    {
        if (user_is_logged()) {

            try {

                $resource = $this->getWithToken(current_token(), '/api/v1/pipelines/' . $uuid);
                $collection = json_decode($resource);

                $data = [];
                $data['user'] = current_user();
                $data['pipeline'] = [];

                if (!$collection->success) {
                    $this->message('danger_message', $collection->message, 'dashboard');
                }

                if (!empty($collection->data)) {
                    $data['pipeline'] = $collection->data->pipeline;
                }

                return view('dashboard/pipelines/show')->with($data);

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }

    /**
     * Edit a pipeline
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

                $resource = $this->putWithToken(current_token(), '/api/v1/pipelines/' . $id, $input);
                $collection = json_decode($resource);

                if ($collection == NULL) {
                    return $this->message('danger_message', 'Errore durante l\'operazione','dashboard');
                }

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                return $this->message('success_message', $collection->message,'dashboard.pipelines');

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }

        }

        abort(401);
    }

    /**
     * Destroy a pipeline
     *
     * @param integer $uuid
     * @return \Illuminate\Http\RedirectResponse|\Illuminate\Routing\Redirector|void
     */
    public function destroy($uuid)
    {
        if (user_is_logged()) {

            try {

                $resource = $this->deleteWithToken(current_token(), '/api/v1/pipelines/' . $uuid);
                $collection = json_decode($resource);

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message,'dashboard');
                }

                return $this->message('success_message', $collection->message,'dashboard.pipelines');

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }
        }
        abort(401);
    }

    /**
     * Search pipelines
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
                    return redirect(route('dashboard.pipelines'));
                }

                $resource = $this->getWithToken(current_token(), '/api/v1/pipelines/search?q=' . $input['query']);
                $collection = json_decode($resource);

                $data = [];
                $data['user'] = current_user();
                $data['current_query'] = $input['query'];
                $data['pipelines'] = [];

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message, 'dashboard');
                }

                if (!empty($collection->data)) {
                    $data['pipelines'] = $collection->data->pipelines;
                }

                return view('dashboard/pipelines/index')->with($data);

            } catch (\Exception $e) {
                return $this->message('danger_message', 'Service Unavailable','dashboard');
            }
        }
        abort(401);
    }


    /**
     * Export pipelines
     *
     * @return \Symfony\Component\HttpFoundation\StreamedResponse|void
     */
    public function export()
    {
        if (user_is_logged()) {

            try {

                $resource = $this->getWithToken(current_token(), '/api/v1/pipelines');
                $collection = json_decode($resource);

                if ($collection == NULL) {
                    return $this->message('warning_message', 'Errore durante l\'operazione','dashboard');
                }

                if (!$collection->success) {
                    return $this->message('danger_message',$collection->message,'dashboard');
                }

                $pipelines = [];

                if (!empty($collection->data)) {
                    $pipelines = $collection->data->pipelines;
                }

                $filename = "pipelines_".now().".csv";

                $headers = [
                    'Content-Type' => 'text/csv',
                    'Content-Disposition' => 'attachment; filename="'.$filename.'"',
                ];

                $callback = function () use ($pipelines) {

                    $out = fopen('php://output', 'w');

                    $caption = [
                        "ID",
                        "Name",
                        "Content",
                    ];
                    fputcsv($out, $caption, $this->separator);

                    foreach ($pipelines as $pipeline) {
                        $formatted_pipeline = [
                            $pipeline->id,
                            $pipeline->name,
                            $pipeline->content,
                        ];

                        fputcsv($out, $formatted_pipeline, $this->separator);
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
