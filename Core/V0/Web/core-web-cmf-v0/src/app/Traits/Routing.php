<?php

namespace App\Traits;

use Illuminate\Support\Facades\Session;
use Illuminate\Support\Str;
use Psr\Container\ContainerExceptionInterface;
use Psr\Container\NotFoundExceptionInterface;

trait Routing
{

    /**
     * Simple client side redirect method
     *
     * @param string $slug admin page slug
     * @param array $query key/value pairs
     * @return bool
     */
    public function redirect(string $slug = 'dashboard', array $query = []) : bool
    {
        // Client side redirect
        $href = empty($slug) || $slug === 'dashboard' ? cms_dashboard_base_url() : cms_dashboard_page_route($slug);

        if (!empty($query)) {
            $href = add_query_arg($query, $href);
        }

        //echo("<script>document.cookie = '".$type."=".$display."; expires=' + new Date(Date.now() + 300000).toUTCString() + '; path=/'</script>");
        echo("<script>location.href = '".$href."';</script>");
        return true;
    }
}
