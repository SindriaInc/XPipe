<?php

namespace Sindria\Iam\ViewModel;

use Sindria\Iam\Helper;
use Sindria\Iam\InfoTable\UserInfoTable;
use Sindria\Iam\DataTable\UserPoliciesDataTable;

class UserInfoViewModel
{

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    public object $data;

    public UserInfoTable $infoTable;

    public string $backAction;

    public UserPoliciesDataTable $userPoliciesTable;

    private function __construct()
    {

    }


    public function __invoke($data)
    {
        $this->data =  $data;
        $this->backAction = Helper::getUserInfoBack();

        $userInfoTable = new UserInfoTable($data, $this->backAction);
        $this->infoTable = $userInfoTable;

        $userPoliciesTable = new UserPoliciesDataTable($data->policies, '');
        $this->userPoliciesTable = $userPoliciesTable;
        $this->userPoliciesTable->prepare_items();
    }

    /**
     * Get singleton instance
     *
     * @return \Sindria\Iam\ViewModel\UserInfoViewModel
     */
    public static function getInstance() : UserInfoViewModel
    {
        if ( ! isset(self::$instance) ) {
            $className = __CLASS__;
            self::$instance = new $className();
        }

        return self::$instance;
    }

}
