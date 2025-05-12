<?php

namespace Sindria\Iam\ViewModel;

use Sindria\Iam\Helper;
use Sindria\Iam\InfoTable\PolicyInfoTable;

class PolicyInfoViewModel
{

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    public object $data;

    public PolicyInfoTable $infoTable;

    public string $backAction;

    private function __construct()
    {

    }


    public function __invoke($data)
    {
        $this->data =  $data;
        $this->backAction = Helper::getPolicyInfoBack();

        $userInfoTable = new PolicyInfoTable($data, $this->backAction);
        $this->infoTable = $userInfoTable;
    }

    /**
     * Get singleton instance
     *
     * @return \Sindria\Iam\ViewModel\PolicyInfoViewModel
     */
    public static function getInstance() : PolicyInfoViewModel
    {
        if ( ! isset(self::$instance) ) {
            $className = __CLASS__;
            self::$instance = new $className();
        }

        return self::$instance;
    }

}
