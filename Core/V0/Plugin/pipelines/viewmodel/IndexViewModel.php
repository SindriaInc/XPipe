<?php

namespace Sindria\Pipelines\ViewModel;

use Sindria\Pipelines\Helper;

class IndexViewModel
{

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    public object $collection;

    private function __construct()
    {

    }


    public function __invoke()
    {

    }

    /**
     * Get singleton instance
     *
     * @return \Sindria\Pipelines\ViewModel\IndexViewModel
     */
    public static function getInstance() : IndexViewModel
    {
        if ( ! isset(self::$instance) ) {
            $className = __CLASS__;
            self::$instance = new $className();
        }

        return self::$instance;
    }

}
