<?php
namespace Sindria\BaseLogger\Logger\Handler;

use Monolog\Handler\StreamHandler;
use Monolog\Logger;

class Debug extends StreamHandler
{
    public function __construct()
    {
        parent::__construct('/var/log/app/interface.log', Logger::DEBUG);
    }
}
