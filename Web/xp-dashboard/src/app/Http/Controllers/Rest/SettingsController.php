<?php

namespace App\Http\Controllers\Rest;

use Illuminate\Http\Request;

class SettingsController extends RestController
{

    public function index()
    {
        if (user_is_logged()) {

            $data = [];
            $data['user'] = current_user();

            return view('dashboard/settings/index')->with($data);
        }
        return abort(403);
    }


    /**
     * Get all settings
     *
     * @return \Illuminate\Contracts\Foundation\Application|\Illuminate\Contracts\View\Factory|\Illuminate\View\View|mixed|void
     */
    public function indexOld()
    {
        if (user_is_logged()) {
            try {
                $resource = $this->getWithToken(current_token(), '/api/v1/settings');
                $collection = json_decode($resource);

                $data = [];
                $data['user'] = current_user();
                $data['settings'] = [];

                if (!$collection->success) {
                    return $this->message('danger_message',$collection->message,'dashboard.settings');
                }

                if (!empty($collection->data)) {
                    $data['settings'] = $collection->data->settings;
                }

                return view('dashboard/settings/index')->with($data);
            } catch (\Exception $e) {
                return $this->message('danger_message','service unavailable','dashboard.settings');
            }
        }
        return abort(403);
    }


    /**
     * Send edited settings data to settings service
     *
     * @param Request $request
     * @return \Illuminate\Contracts\Foundation\Application|\Illuminate\Http\RedirectResponse|\Illuminate\Routing\Redirector|void
     */
    public function editOld(Request $request)
    {
        if (user_is_logged()) {
            try {
                $data =  [
                    'subscriptions' => $request->input('subscriptions'),
                ];

                $resource = $this->postWithToken(current_token(), '/api/v1/settings', $data);
                $collection = json_decode($resource);

                if (!$collection->success) {
                    return $this->message('danger_message', $collection->message, 'dashboard.settings');
                }

                return $this->message('success_message', $collection->message, 'dashboard.settings');
            } catch (\Exception $e) {
                return $this->message('danger_message','service unavailable','dashboard.settings');
            }
        }
        return abort(403);
    }
}
