<?php

namespace Sindria\Iam\Form;

use Sindria\Toolkit\Form\Form;

class AddPolicyForm extends Form
{
    /**
     * Define add policy form inputs
     *
     * @override
     * @return void
     */
    protected function makeInputs()
    {
        $this->inputName("name", "name", trans('iam.policies.field.name'), "", old('name'), true);
        $this->inputCodeEditor("content", "content", trans('iam.policies.field.content'), old('content'), true);
    }
}
