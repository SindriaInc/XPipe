<?php

namespace Sindria\Iam\ViewModel;

use Sindria\Toolkit\BaseHelper;
use Sindria\Iam\DataTable\UsersDataTable;
use Sindria\Iam\Helper;

class UsersViewModel
{

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    public object $collection;

    public UsersDataTable $dataTable;

    public string $exportAction;

    public string $addNewAction;

    public string $managePoliciesAction;

    private function __construct()
    {

    }


    public function __invoke($collection)
    {
        $this->collection =  $collection;
        $this->exportAction = "users-export";

        $usersDataTable = new UsersDataTable($collection, $this->exportAction);
        $this->dataTable = $usersDataTable;
        $this->dataTable->prepare_items();

        $this->addNewAction = BaseHelper::buildAddNewAction(BaseHelper::hasCapability('write_users'), cms_dashboard_page_route('add-user'), trans('global.actions.add'));
        $this->managePoliciesAction = Helper::buildManagePoliciesAction();
    }

    /**
     * Get singleton instance
     *
     * @return \Sindria\Iam\ViewModel\UsersViewModel
     */
    public static function getInstance() : UsersViewModel
    {
        if ( ! isset(self::$instance) ) {
            $className = __CLASS__;
            self::$instance = new $className();
        }

        return self::$instance;
    }

}
