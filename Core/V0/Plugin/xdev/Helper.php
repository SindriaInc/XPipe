<?php

namespace Sindria\Xdev;

class Helper
{

    public static function getTerminalUrl() : string
    {
        return env('APP_URL') . '/content/terminal/';
    }

    public static function getSessionTerminalUrl() : string
    {
        //$hostname = '10.254.0.2';
        $hostname = env('XPIPE_CORE_XDEV_CLI_HOST');
        $port = 2222;
        $username = 'sindria';
        $password = 'c2luZHJpYQo=';

        return env('XPIPE_CORE_XDEV_CLI_URL') . '?hostname='. $hostname . '&port=' . $port . '&username=' . $username . '&password=' . $password;
    }

    public static function getXdevlUrl() : string
    {
        return env('XPIPE_CORE_XDEV_BASE_URL');
    }


    public static function getSessionXdevUrl() : string
    {
        $password = 'sindria';
        $autoconnect = 'true';
        $resize = 'remote';

        return env('XPIPE_CORE_XDEV_BASE_URL') . '/vnc.html?password='. $password . '&autoconnect=' . $autoconnect . '&resize=' . $resize;
    }

}
