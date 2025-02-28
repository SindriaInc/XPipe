<?php

namespace Sindria\Xdev;

use Sindria\Xdev\ViewModel\CliViewModel;
use Sindria\Xdev\ViewModel\GuiViewModel;

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
     * Show XDev terminal cli
     *
     * @param Request $request
     * @return mixed
     */
    public function cli(Request $request)
    {
        $viewModel = CliViewModel::getInstance();
        $viewModel();

        return $this->view->render('xdev-cli');
    }

    /**
     * Show XDev gui
     *
     * @param Request $request
     * @return mixed
     */
    public function gui(Request $request)
    {
        $viewModel = GuiViewModel::getInstance();
        $viewModel();

        return $this->view->render('xdev-gui');
    }
}
