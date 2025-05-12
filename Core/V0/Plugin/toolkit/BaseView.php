<?php

namespace Sindria\Toolkit;

abstract class BaseView
{

    /**
     * @var string
     */
    public string $viewsBasePath = __DIR__ . '/view/';

    /**
     * @var string
     */
    protected string $slug;


    public function __construct(string $slug = '')
    {
        $this->slug = $slug;
    }

    /**
     * Render a view template by slug
     *
     * @param string $slug template file name, without extension
     * @return mixed
     */
    public function render(string $slug)
    {
        return include $this->viewsBasePath . $slug . '.php';
    }
}
