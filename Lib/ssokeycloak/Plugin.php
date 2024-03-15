<?php

namespace Sindria\SsoKeycloak;

use Sindria\SsoKeycloak\Controller;
use Sindria\SsoKeycloak\Service;
use Sindria\SsoKeycloak\Helper;

class Plugin
{
    /**
     * @var \Sindria\SsoKeycloak\Controller $controller
     */
    public Controller $controller;

    /**
     *  Plugin constructor
     *
     *  It associates some custom functions with the corresponding functions in the wordpress stream,
     *  through the WP function "add_action".
     *
     *  @see https://developer.wordpress.org/reference/functions/add_action/    Official Documentation
     *
     */
    public function __construct()
    {
        $this->controller = new \Sindria\SsoKeycloak\Controller(new \Sindria\SsoKeycloak\Service());

        add_action('login_form', array($this, 'handleLoginForm'));
        add_action('init', array($this, 'handleInit'), 10, 0);
        add_action('wp_logout', array($this, 'handleLogout'), 10, 0);
    }

    /**
     * Login Form handle plugin controller class
     *
     * @return void
     */
    public function handleLoginForm()
    {
        $this->controller->createSsoButton();
    }

    /**
     * Main handle plugin controller class
     *
     * @return void
     */
    public function handleInit()
    {
        return $this->controller->handle();
    }

    /**
     * Logout handle plugin controller class
     *
     * @return void
     */
    public function handleLogout()
    {
        $this->controller->performLogout();
    }

}
