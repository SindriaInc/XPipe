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
        $this->inputNumber('number', 'number', "Number", "2", "", true);
//        $this->inputCheckbox('checkbox', 'checkbox', "Checkbox", false, "", true);
//
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
//
        $this->inputTextArea('text-area', 'text-area', "Text Area", "A generic text area", "", false);

    }
}
