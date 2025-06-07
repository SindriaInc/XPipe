<?php

namespace Sindria\Iam\Form;

use Sindria\Toolkit\Form\Form;

class AttachPolicyForm extends Form
{
    /**
     * Define attach policy form inputs
     *
     * @override
     * @return void
     */
    protected function makeInputs()
    {

        // Select User

        $optionsUsers = [];

        foreach ($this->entry->users as $key => $user) {
            $optionsUsers[$key]['value'] = $user->id;
            $optionsUsers[$key]['label'] = $user->username . ' ' . '(' .$user->firstName . ' ' . $user->lastName . ')';
        }

        $this->inputSelect('user_id', 'user_id', trans('iam.policies.attach.field.user_id'), $optionsUsers, NULL, true);


        // Select Policy

        $optionsPolicies = [];

        foreach ($this->entry->policies as $key => $policy) {
            $optionsPolicies[$key]['value'] = $policy->id;
            $optionsPolicies[$key]['label'] = $policy->name;
        }

        $this->inputSelect('policy_id', 'policy_id', trans('iam.policies.attach.field.policy_id'), $optionsPolicies, NULL, true);


    }
}
