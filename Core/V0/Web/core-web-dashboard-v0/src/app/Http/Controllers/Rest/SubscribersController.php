<?php

namespace App\Http\Controllers\Rest;

use Illuminate\Http\Request;

class SubscribersController extends RestController
{

    /**
     * Show all subscribers table
     *
     * @return \Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function index()
    {
        if (user_is_logged()) {

            $resource = $this->getWithToken(current_token(), '/api/v1/tournaments/subscribers');
            $collection = json_decode($resource);

            $data = [];
            $data['user'] = current_user();
            $data['subscribers'] = [];

           if ($collection->success) {

               if (!empty($collection->data)) {
                   $data['subscribers'] = $collection->data->subscribers;
               }

           } elseif (!$collection->success) {
               return $this->message('danger_message',$collection->message,'dashboard.tournaments.subscribers');
           } else {
               return;
           }

           return view('dashboard/tournaments/subscribers/index')->with($data);
       }

        return abort(403);
    }


    /**
     * Show all subscriber details by id
     *
     * @param $id
     * @return \Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function details($id)
    {
        if (user_is_logged()) {

            $resource = $this->getWithToken(current_token(), '/api/v1/tournaments/subscribers/show/' . $id);
            $collection = json_decode($resource);

            $data = [];
            $data['user'] = current_user();
            $data['subscriber'] = [];
            $data['score'] = [];
            $data['category'] = [];
            $data['type'] = [];

            if ($collection->success) {

                if (!empty($collection->data)) {
                    $data['subscriber'] = $collection->data->subscriber;
                    $data['score'] = $collection->data->score;
                    $data['category'] = $collection->data->category;
                    $data['type'] = $collection->data->type;
                }


            } elseif (!$collection->success) {
                $this->message('danger_message',$collection->message,'dashboard.tournaments');
            } else {
                return;
            }

            return view('dashboard/tournaments/subscribers/detail')->with($data);
        }
        return abort(403);
    }


    /**
     * Store a subscriber
     *
     * @param Request $request
     * @return mixed
     */
    public function store(Request $request)
    {
        $data =  [
            'tournament_id' => $request->input('tournament_id'),
            'name' => $request->input('name'),
            'surname' => $request->input('surname'),
            'birthday' => $request->input('birthday'),
            'email' => $request->input('email'),
            'phone' => $request->input('phone'),
            'fit' => $request->input('fit'),
            'club' => $request->input('club'),
            'score_id' => $request->input('score_id'),
            'category_id' => $request->input('category_id'),
            'type_id' => $request->input('type_id'),
            'note' => $request->input('note'),
        ];

        $response = $this->postWithToken(current_token(), '/api/v1/tournaments/subscribers/store', $data);
        $collection = json_decode($response);

        if ($collection == NULL) {
            return $this->message('danger_message', 'Errore durante l\'operazione', 'dashboard.tournaments');
        }

        if (!$collection->success) {

            // Validation errors
            if ($collection->message == 'validation error') {

                $validation = [];
                $validation['response'] = false;
                $validation['errors'] = $collection->data->messages;

                return redirect()->back()->withInput()->withErrors($validation['errors']);
            }

            return $this->message('danger_message', $collection->message, 'dashboard.tournaments');
        }

        return $this->message('success_message', $collection->message, 'dashboard.tournaments');
    }


    /**
     * Show subscriber data for edit
     *
     * @param $id
     * @return \Illuminate\Contracts\View\Factory|\Illuminate\View\View|void
     */
    public function show($id)
    {
        if (user_is_logged()) {

            $resource = $this->getWithToken(current_token(), '/api/v1/tournaments/subscribers/show/' . $id);
            $collection = json_decode($resource);

            $data = [];
            $data['user'] = current_user();
            $data['subscriber'] = [];
            $data['score'] = [];
            $data['category'] = [];
            $data['type'] = [];


            // All options for select
            $resource_ = $this->getWithToken(current_token(), '/api/v1/tournaments/subscriber');
            $collection_ = json_decode($resource_);

            $data['scores'] = [];
            $data['categories'] = [];
            $data['types'] = [];


            if (!$collection_->success) {
                $this->message('danger_message',$collection_->message,'dashboard.tournaments');
            }

            if (!$collection->success) {
                $this->message('danger_message',$collection->message,'dashboard.tournaments');
            }


            if (!empty($collection_->data)) {
                $data['scores'] = $collection_->data->scores;
                $data['categories'] = $collection_->data->categories;
                $data['types'] = $collection_->data->types;
            }


            if (!empty($collection->data)) {
                $data['subscriber'] = $collection->data->subscriber;
                $data['score'] = $collection->data->score;
                $data['category'] = $collection->data->category;
                $data['type'] = $collection->data->type;
            }


            return view('dashboard/tournaments/subscribers/show')->with($data);
        }
        return abort(403);
    }


    /**
     * Edit a subscriber
     *
     * @param Request $request
     * @return mixed
     */
    public function edit(Request $request, $id)
    {

        $data =  [
            'tournament_id' => $request->input('tournament_id'),
            'name' => $request->input('name'),
            'surname' => $request->input('surname'),
            'birthday' => $request->input('birthday'),
            'email' => $request->input('email'),
            'phone' => $request->input('phone'),
            'fit' => $request->input('fit'),
            'club' => $request->input('club'),
            'score_id' => $request->input('score_id'),
            'category_id' => $request->input('category_id'),
            'type_id' => $request->input('type_id'),
            'note' => $request->input('note'),
        ];

        $response = $this->postWithToken(current_token(), "/api/v1/tournaments/subscribers/edit/"."$id", $data);
        $collection = json_decode($response);

        if ($collection == NULL) {
            return $this->message('danger_message', 'Errore durante l\'operazione', 'dashboard.tournaments');
        }

        if (!$collection->success) {

            // Validation errors
            if ($collection->message == 'validation error') {

                $validation = [];
                $validation['response'] = false;
                $validation['errors'] = $collection->data->messages;

                return redirect()->back()->withInput()->withErrors($validation['errors']);
            }

            return $this->message('danger_message', $collection->message, 'dashboard.tournaments.details', $data['tournament_id']);
        }

        return $this->message('success_message', $collection->message, 'dashboard.tournaments.details', $data['tournament_id']);
    }


    /**
     * Delete a Subscriber
     *
     * @param Request $request
     * @param $id
     * @return \Illuminate\Http\RedirectResponse|\Illuminate\Routing\Redirector|void
     */
    public function delete(Request $request, $id)
    {
        if (user_is_logged()) {

            $tournament_id = $request->input('tournament_id');

            $resource = $this->postWithToken(current_token(), '/api/v1/tournaments/subscribers/delete/' . $id);
            $collection = json_decode($resource);

            if (!$collection->success) {
                return  $this->message('danger_message', $collection->message,'dashboard.tournaments.details', $tournament_id);
            }

            return  $this->message('success_message', $collection->message,'dashboard.tournaments.details', $tournament_id);
        }
        return abort(403);
    }


    public function export()
    {
        if (user_is_logged()) {
            return $this->getWithToken(current_token(), '/api/v1/tournaments/subscribers/export');
        }
        return abort(403);
    }
}
