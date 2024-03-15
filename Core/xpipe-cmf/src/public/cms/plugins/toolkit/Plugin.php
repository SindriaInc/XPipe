<?php

namespace Sindria\Toolkit;

class Plugin
{
    /**
     *  Plugin constructor
     *
     *  It associates some custom functions with the corresponding functions in the wordpress stream,
     *  through the WP function "add_action".
     *
     *  @see https://developer.wordpress.org/reference/functions/add_action/    Official Documentation
     *
     */
    function __construct()
    {
        //add_action('admin_menu', array($this, 'createMenu'), 10, 0);
    }


}
