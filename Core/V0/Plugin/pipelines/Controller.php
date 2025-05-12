<?php

namespace Sindria\Pipelines;

use Sindria\Pipelines\ViewModel\IndexViewModel;

use Illuminate\Http\Request;
use App\Traits\Rest;
use App\Traits\Messages;


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
     * Show pipelines datatable
     *
     * @param Request $request
     * @return mixed
     */
    public function index(Request $request)
    {
        $viewModel = IndexViewModel::getInstance();
        $viewModel();

        return $this->view->render('pipelines');
    }


}
