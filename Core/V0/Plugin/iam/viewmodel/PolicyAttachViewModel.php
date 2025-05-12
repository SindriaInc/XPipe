<?php

namespace Sindria\Iam\ViewModel;

use Sindria\Iam\Form\AttachPolicyForm;
use Sindria\Iam\Helper;

class PolicyAttachViewModel
{

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    public object $data;

    public AttachPolicyForm $form;

    private function __construct()
    {

    }


    public function __invoke($data)
    {
        $this->data =  $data;

        $attachPolicyForm = new AttachPolicyForm($data, 'attach-policy', 'attach-policy', Helper::getPoliciesAttachFormAction(), Helper::getPoliciesAttachFormMethod(), Helper::getPoliciesCancelForm());
        $this->form = $attachPolicyForm;
    }

    /**
     * Get singleton instance
     *
     * @return \Sindria\Iam\ViewModel\PolicyAttachViewModel
     */
    public static function getInstance() : PolicyAttachViewModel
    {
        if ( ! isset(self::$instance) ) {
            $className = __CLASS__;
            self::$instance = new $className();
        }

        return self::$instance;
    }

}
