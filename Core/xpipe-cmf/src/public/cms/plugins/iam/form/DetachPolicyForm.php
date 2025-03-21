<?php

namespace Sindria\Iam\Form;

use Sindria\Toolkit\Form\Form;

class DetachPolicyForm extends Form
{
    /**
     * Define attach policy form inputs
     *
     * @override
     * @return void
     */
    protected function makeInputs()
    {
        $this->inputName("name", "name", trans('iam.policies.field.name'), "", old('name'), true);
        $this->inputEditor("content", "content", trans('iam.policies.field.content'), old('content'), true);



//        $this->inputName('surname', 'surname', trans('iam.users.field.surname'), trans('iam.users.field.surname.placeholder'), old('surname'), true);
//        $this->inputEmail('email', 'email', trans('iam.users.field.email'), trans('iam.users.field.email.placeholder'), old('email'), true);
//        $this->inputText('user_login', 'user_login', trans('iam.users.field.username'), trans('iam.users.field.username.placeholder'), old('user_login'), true);
//
//        $options = [
//            [
//                'value' => 0,
//                'label' => trans('iam.users.field.status.false'),
//            ],
//            [
//                'value' => 1,
//                'label' => trans('iam.users.field.status.true'),
//            ]
//        ];
//        $this->inputSelect('enabled', 'enabled', trans('iam.users.field.status'), $options, 0, false);
//
//        $this->inputText('job_title', 'job_title', trans('iam.users.field.job_title'), trans('iam.users.field.job_title.placeholder'), old('job_title'), false);

    }
}
