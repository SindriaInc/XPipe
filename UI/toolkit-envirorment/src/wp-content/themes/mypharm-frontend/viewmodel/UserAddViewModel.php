<?php

namespace viewmodel;

use form\AddUserForm;
use Helper;

class UserAddViewModel
{

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    public object $data;

    public AddUserform $form;

    private function __construct()
    {

    }


    public function __invoke($data)
    {
        $this->data =  $data;

        $userForm = new AddUserForm($data, 'add-user', 'add-user', Helper::getUsersAddFormAction(), Helper::getUsersAddFormMethod(), Helper::getUsersCancelForm());
        $this->form = $userForm;
    }

    /**
     * Get singleton instance
     *
     * @return \viewmodel\UserAddViewModel
     */
    public static function getInstance() : UserAddViewModel
    {
        if ( ! isset(self::$instance) ) {
            $className = __CLASS__;
            self::$instance = new $className();
        }

        return self::$instance;
    }

}
