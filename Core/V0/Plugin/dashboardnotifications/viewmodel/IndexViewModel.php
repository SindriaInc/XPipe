<?php

namespace Sindria\DashboardNotifications\ViewModel;

use Sindria\DashboardNotifications\Helper;

class IndexViewModel
{

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    public object $collection;

    public int $counter;

    public string $url;

    private function __construct()
    {

    }


    public function __invoke($collection)
    {
        $this->collection = $collection;
        $this->counter = count($collection);
        $this->url = Helper::getNotificationsUrl();
    }

    /**
     * Get singleton instance
     *
     * @return \Sindria\DashboardNotifications\ViewModel\IndexViewModel
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
