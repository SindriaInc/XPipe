<?php

namespace form;

use Sindria\Toolkit\Form\Form;

//use function Sindria\Iam\Form\old;
//use function Sindria\Iam\Form\trans;

class AddUserForm extends Form
{
    /**
     * Define add user form inputs
     *
     * @override
     * @return void
     */
    protected function makeInputs()
    {
        $this->inputName("name", "name", "Name", "Paolo", "", true);
        $this->inputName('surname', 'surname', "Surname", "Rossi", "", true);
        $this->inputEmail('email', 'email', "Email", "paolo.rossi@example.com", "", true);
        $this->inputText('user_login', 'user_login', "Username", "paolo.rossi", "", true);

        $options = [
            [
                'value' => 0,
                'label' => "Disabled",
            ],
            [
                'value' => 1,
                'label' => "Active",
            ]
        ];
        $this->inputSelect('enabled', 'enabled', "Status", $options, 0, false);

        $this->inputText('job_title', 'job_title', "Job Title", "DevOps Engineer", "", false);

    }
}
