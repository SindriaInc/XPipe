<?php

namespace Sindria\Iam\ViewModel;

use Sindria\Iam\Form\ShowUserForm;
use Sindria\Iam\Helper;

class UserShowViewModel
{

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    public object $data;

    public ShowUserform $form;

    private function __construct()
    {

    }


    public function __invoke($data)
    {
        $this->data =  $data;

        $userForm = new ShowUserForm($data, 'show-user', 'show-user', Helper::getUsersShowFormAction(), Helper::getUsersShowFormMethod(), Helper::getUsersCancelForm());
        $this->form = $userForm;
    }

    /**
     * Get singleton instance
     *
     * @return \Sindria\Iam\ViewModel\UserShowViewModel
     */
    public static function getInstance() : UserShowViewModel
    {
        if ( ! isset(self::$instance) ) {
            $className = __CLASS__;
            self::$instance = new $className();
        }

        return self::$instance;
    }

}
