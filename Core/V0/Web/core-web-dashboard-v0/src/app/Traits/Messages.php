<?php

namespace App\Traits;

use Illuminate\Support\Facades\Session;

trait Messages {

    /**
     * Simple custom method for backoffice user display message
     *
     * @param string $type
     * @param string $display
     * @param string $path
     * @return mixed
     */
    public function message($type, $display, $path, $params = []) {
        $message = Session::flash($type, $display);
        return redirect(route($path, $params))->withMessage($message);
    }


    /**
     * Simple custom method for backoffice user display message with translation
     *
     * @param $type
     * @param $target
     * @param $key
     * @param $path
     * @return mixed
     */
    public function transmessage($type, $target, $key, $path) {

        if (strpos($path, 'admin/') !== false) {
            $path = substr($path, strlen('admin/'));
        } else {
            $path = route($path);
        }


        $message = Session::flash($type, trans($target.".".$key));
        return redirect($path)->withMessage($message);
    }


}
