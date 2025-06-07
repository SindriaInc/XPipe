<?php

namespace Sindria\Iam\Form;

use Sindria\Toolkit\Form\Form;

class ShowUserForm extends Form
{
    /**
     * Define add user form inputs
     *
     * @override
     * @return void
     */
    protected function makeInputs()
    {
        $this->inputHidden('id', $this->entry->user->id);
        $this->inputHidden('user_login', $this->entry->user->username);

        $this->inputName("name", "name", trans('iam.users.field.name'), trans('iam.users.field.name.placeholder'), $this->entry->user->firstName, true);
        $this->inputName('surname', 'surname', trans('iam.users.field.surname'), trans('iam.users.field.surname.placeholder'), $this->entry->user->lastName, true);
        $this->inputEmail('email', 'email', trans('iam.users.field.email'), trans('iam.users.field.email.placeholder'), $this->entry->user->email, true);
        $this->inputText('user_login_disabled', 'user_login_disabled', trans('iam.users.field.username'), trans('iam.users.field.username.placeholder'), $this->entry->user->username, true, "disabled");


        // Status
        $currentStatus = $this->entry->user->enabled ? 1 : 0;
        $optionsStatus = [
            [
                'value' => 0,
                'label' => trans('iam.users.field.status.false'),
            ],
            [
                'value' => 1,
                'label' => trans('iam.users.field.status.true'),
            ]
        ];
        $this->inputSelect('enabled', 'enabled', trans('iam.users.field.status'), $optionsStatus, $currentStatus, false);

        // Email verified
        $currentEmailVerified = $this->entry->user->emailVerified ? 1 : 0;
        $optionsEmailVerified = [
            [
                'value' => 0,
                'label' => trans('iam.users.field.email_verified.false'),
            ],
            [
                'value' => 1,
                'label' => trans('iam.users.field.email_verified.true'),
            ]
        ];
        $this->inputSelect('email_verified', 'email_verified', trans('iam.users.field.email_verified'), $optionsEmailVerified, $currentEmailVerified, false);

        // User Meta
        $this->inputText('job_title', 'job_title', trans('iam.users.field.job_title'), trans('iam.users.field.job_title.placeholder'), $this->entry->meta->jobTitle, false);

    }
}
