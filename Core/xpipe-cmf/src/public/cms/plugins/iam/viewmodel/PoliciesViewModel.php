<?php

namespace Sindria\Iam\ViewModel;

use Sindria\Toolkit\BaseHelper;
use Sindria\Iam\DataTable\PoliciesDataTable;
use Sindria\Iam\Helper;

class PoliciesViewModel
{

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    public object $collection;

    public PoliciesDataTable $dataTable;

    public string $exportAction;

    public string $addNewAction;

    public string $attachPolicyAction;

    public string $detachPolicyAction;

    private function __construct()
    {

    }


    public function __invoke($collection)
    {
        $this->collection =  $collection;
        $this->exportAction = "policies-export";

        $policiesDataTable = new PoliciesDataTable($collection, $this->exportAction);
        $this->dataTable = $policiesDataTable;
        $this->dataTable->prepare_items();

        $this->addNewAction = BaseHelper::buildAddNewAction(BaseHelper::hasCapability('write_policies'), cms_dashboard_page_route('add-policy'), trans('global.actions.add'));
        $this->attachPolicyAction = Helper::buildAttachPolicyAction();
        $this->detachPolicyAction = Helper::buildDetachPolicyAction();
    }

    /**
     * Get singleton instance
     *
     * @return \Sindria\Iam\ViewModel\PoliciesViewModel
     */
    public static function getInstance() : PoliciesViewModel
    {
        if ( ! isset(self::$instance) ) {
            $className = __CLASS__;
            self::$instance = new $className();
        }

        return self::$instance;
    }

}
