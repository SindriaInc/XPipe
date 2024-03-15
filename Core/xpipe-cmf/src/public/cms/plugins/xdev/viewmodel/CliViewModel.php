<?php

namespace Sindria\Xdev\ViewModel;

use Sindria\Xdev\Helper;

class CliViewModel
{

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    public object $collection;

    public string $url;

    private function __construct()
    {

    }


    public function __invoke()
    {
        $this->url = Helper::getSessionTerminalUrl();
    }

    /**
     * Get singleton instance
     *
     * @return \Sindria\Xdev\ViewModel\CliViewModel
     */
    public static function getInstance() : CliViewModel
    {
        if ( ! isset(self::$instance) ) {
            $className = __CLASS__;
            self::$instance = new $className();
        }

        return self::$instance;
    }

}
