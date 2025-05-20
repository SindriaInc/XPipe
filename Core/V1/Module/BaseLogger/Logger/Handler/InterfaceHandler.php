<?php
namespace Core\BaseLogger\Logger\Handler;

use Monolog\Handler\StreamHandler;
use Monolog\Logger;

class InterfaceHandler extends StreamHandler
{
    public function __construct()
    {
        // Log everything from DEBUG and above
        parent::__construct('/var/log/app/interface.log', Logger::DEBUG);
    }
}
