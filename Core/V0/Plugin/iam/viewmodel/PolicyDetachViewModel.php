<?php

namespace Sindria\Iam\ViewModel;

use Sindria\Iam\Form\DetachPolicyForm;
use Sindria\Iam\Helper;

class PolicyDetachViewModel
{

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    public object $data;

    public DetachPolicyForm $form;

    private function __construct()
    {

    }


    public function __invoke($data)
    {
        $this->data =  $data;

        $detachPolicyForm = new DetachPolicyForm($data, 'detach-policy', 'detach-policy', Helper::getPoliciesDetachFormAction(), Helper::getPoliciesDetachFormMethod(), Helper::getPoliciesCancelForm());
        $this->form = $detachPolicyForm;
    }

    /**
     * Get singleton instance
     *
     * @return \Sindria\Iam\ViewModel\PolicyDetachViewModel
     */
    public static function getInstance() : PolicyDetachViewModel
    {
        if ( ! isset(self::$instance) ) {
            $className = __CLASS__;
            self::$instance = new $className();
        }

        return self::$instance;
    }

}
