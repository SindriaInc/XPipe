<?php

namespace Sindria\Iam\ViewModel;

use Sindria\Iam\Form\AddPolicyForm;
use Sindria\Iam\Helper;

class PolicyAddViewModel
{

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    public object $data;

    public AddPolicyForm $form;

    private function __construct()
    {

    }


    public function __invoke($data)
    {
        $this->data =  $data;

        $policyForm = new AddPolicyForm($data, 'add-policy', 'add-policy', Helper::getPoliciesAddFormAction(), Helper::getPoliciesAddFormMethod(), Helper::getPoliciesCancelForm());
        $this->form = $policyForm;
    }

    /**
     * Get singleton instance
     *
     * @return \Sindria\Iam\ViewModel\PolicyAddViewModel
     */
    public static function getInstance() : PolicyAddViewModel
    {
        if ( ! isset(self::$instance) ) {
            $className = __CLASS__;
            self::$instance = new $className();
        }

        return self::$instance;
    }

}
