<?php

namespace App\Http\Controllers\Ajax;

use App\Models\User;
use App\Notifications\Alerts;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Response;

class NotificationsController extends AjaxController
{

    public function index(Request $request) : JsonResponse
    {
        $filter = $request->query('filter');

//        if ($filter != 'unread' || $filter != 'read' || $filter != NULL) {
//            return $this->sendError('Invalid Request',422);
//        }

        $user = Auth::user();

        $data = [];

        switch ($filter) {
            case 'unread':
                $notifications = $user->unreadNotifications;
                $data['counter'] = count($notifications);
                $data['notifications'] = $notifications;
                break;
            case 'read':
                $notifications = $user->readNotifications;
                $data['counter'] = count($notifications);
                $data['notifications'] = $notifications;
                break;
            default:
                $notifications = $user->notifications;
                $data['counter'] = count($notifications);
                $data['notifications'] = $notifications;
        }

        return $this->sendResponse('ok', 200, $data);
    }


    public function markAsRead(Request $request) : JsonResponse
    {
        $id = $request->query('id');
        $user = Auth::user();

        //$currentUser = current_user();
        //dd($currentUser['ID']);
        //$user = User::where('id', $currentUser['ID'])->first();

        //dd($user);



        if (! $id) {
            $user->notifications->markAsRead();
            return $this->sendResponse('ok', 201);
        } else {
            $user->notifications->where('id', $id)->markAsRead();
            return $this->sendResponse('ok', 201);
        }
    }

    public function markAsUnread(Request $request) : JsonResponse
    {
        $id = $request->query('id');
        $user = Auth::user();

        if (! $id) {
            $user->notifications->markAsUnread();
            return $this->sendResponse('ok', 201);
        } else {
            $user->notifications->where('id', $id)->markAsUnread();
            return $this->sendResponse('ok', 201);
        }
    }


    public function test(Request $request) : JsonResponse
    {

        $username = $request->query('u');
        $type = $request->query('type');
        $message = $request->query('msg');

        if (! $username) {
            return $this->sendError('Missing username param', 422);
        }

        if (! $type) {
            $type = 'info';
        }

        if (! $message) {
            $message = 'Test Message';
        }

        $user = User::where('user_login', $username)->first();

        $content = [];
        $content['message'] = $message;
        $content['type'] = $type;


        if ($type == 'danger' || $type == 'warning') {
            $content['mp3'] = 'arpeggio-467.mp3';
        } else {
            $content['mp3'] = 'task-completed-message-ringtone.mp3';
        }


        // Current auth user
        //$user = Auth::user();


        $user->notify(new Alerts($content));

        return $this->sendResponse('ok');
    }




}
