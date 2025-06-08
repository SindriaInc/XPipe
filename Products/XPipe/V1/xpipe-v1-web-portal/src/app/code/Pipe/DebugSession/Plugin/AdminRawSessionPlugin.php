<?php
namespace Pipe\DebugSession\Plugin;

use Psr\Log\LoggerInterface;

class AdminRawSessionPlugin
{
    private LoggerInterface $logger;

    public function __construct(LoggerInterface $logger)
    {
        $this->logger = $logger;
    }

    public function beforeSetData($subject, $key, $value)
    {
        if ($key === 'admin') {
            $this->logger->debug('AdminRawSessionPlugin::beforeSetData - key=admin');
            $this->logValue($value);
        }
        return [$key, $value];
    }

    public function beforeOffsetSet($subject, $key, $value)
    {
        if ($key === 'admin') {
            $this->logger->debug('AdminRawSessionPlugin::beforeOffsetSet - key=admin');
            $this->logValue($value);
        }
        return [$key, $value];
    }

    private function logValue($value)
    {
        if (is_array($value)) {
            foreach ($value as $k => $v) {
                $info = is_object($v) ? get_class($v) : gettype($v);
                $this->logger->debug("AdminRawSessionPlugin:: key [$k] => $info");
            }
        } else {
            $this->logger->debug('AdminRawSessionPlugin:: admin is not array: ' . gettype($value));
        }
    }
}
