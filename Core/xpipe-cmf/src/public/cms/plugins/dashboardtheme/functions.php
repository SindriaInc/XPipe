<?php

/**
 * Init Plugin
 *
 * @return void
 */
function dashboardtheme() : void
{
    if (class_exists('\Sindria\DashboardTheme\Plugin')) {
        $plugin = new \Sindria\DashboardTheme\Plugin();
    }
}
