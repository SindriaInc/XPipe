<?php

namespace App\Traits;

use Illuminate\Support\Facades\Session;
use Illuminate\Support\Str;
use Psr\Container\ContainerExceptionInterface;
use Psr\Container\NotFoundExceptionInterface;

trait Messages
{

    /**
     * Simple custom method for backoffice user display message
     *
     * @param string $type message type [ "success_message" | "warning_message" | "danger_message" | "info_message" ]
     * @param string $display text to display
     * @param string $slug admin page slug
     * @param array $query key/value pairs
     * @return bool
     */
    public function message(string $type, string $display, string $slug = 'dashboard', array $query = []) : bool
    {
        // Server side redirect
        //$message = Session::flash($type, $display);
        //echo redirect(route($slug))->withMessage($message);
        //return redirect(cms_dashboard_page_route($slug))->withMessage($message);
        //echo redirect(cms_dashboard_page_route($slug))->withMessage($message);

        // Client side redirect
        $href = empty($slug) || $slug === 'dashboard' ? cms_dashboard_base_url() : cms_dashboard_page_route($slug);

        if (!empty($query)) {
            $href = add_query_arg($query, $href);
        }

        echo("<script>document.cookie = '".$type."=".$display."; expires=' + new Date(Date.now() + 300000).toUTCString() + '; path=/'</script>");
        echo("<script>location.href = '".$href."';</script>");
        return true;
    }


    /**
     * Simple custom method for backoffice user display message with translation
     *
     * @param string $type                    message type [ "success_message" | "warning_message" | "danger_message" | "info_message" ]
     * @param string $file                    the translation file
     * @param string $key                     the translation key
     * @param string $slug                    admin page slug to redirect
     * @param array  $query key/value pairs
     * @return bool
     * @throws ContainerExceptionInterface
     * @throws NotFoundExceptionInterface
     */
    public function transMessage(string $type, string $file, string $key, string $slug = 'dashboard', array $query = []) : bool
    {
        // Server side redirect
        //$display = trans($file.".".$key);
        //$message = Session::flash($type, $display);
        //return redirect(cms_dashboard_page_route($slug))->withMessage($message);

        // Client side redirect
        $transMessage = trans($file.".".$key, [], session()->get('locale', config('app.locale')));
        $display = Str::startsWith($transMessage, $file.'.') ? $key : $transMessage;
        return $this->message($type, $display, $slug, $query);
    }


}
