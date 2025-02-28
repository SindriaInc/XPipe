<?php

namespace Sindria\DashboardNotifications;

use Illuminate\Support\Facades\Auth;
use Sindria\DashboardNotifications\ViewModel\IndexViewModel;

use Illuminate\Http\Request;
use App\Traits\Rest;
use App\Traits\Messages;
use Illuminate\Support\Facades\Response;


class Controller
{
    use Rest, Messages;

    /**
     * @var View $view
     */
    protected View $view;

    /**
     * Controller constructor
     *
     * @param View $view
     */
    public function __construct(View $view)
    {
        $this->view = $view;
    }

    /**
     * Show notifications tables with pagination and search
     *
     * @param Request $request
     * @return mixed
     */
    public function index(Request $request)
    {
        //try {
            $user = Auth::user();

            $viewModel = IndexViewModel::getInstance();
            $viewModel($user->unreadNotifications);

            return $this->view->render('notifications');

        //} catch (\Exception $e) {
        //    report($e);
        //    //return $this->message('danger_message', 'Service Unavailable','notifications');
        //}
    }
}
