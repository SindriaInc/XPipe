<?php

namespace Pipe\DebugSession\Plugin;

use Core\Logger\Facade\LoggerFacade;
use Magento\User\Model\User;
use Magento\Backend\Model\Auth\Session as AdminSession;

class AdminRawSessionPlugin
{
    public function beforeSetData($subject, $key, $value)
    {
        if ($key === 'admin') {
            LoggerFacade::debug('AdminRawSessionPlugin::beforeSetData - key=admin');
            $this->logValue($value);
        }
        return [$key, $value];
    }

    public function beforeOffsetSet($subject, $key, $value)
    {
        if ($key === 'admin') {
            LoggerFacade::debug('AdminRawSessionPlugin::beforeOffsetSet - key=admin', [
                'trace' => $this->getTrace()
            ]);
            $this->logValue($value);
        }
        return [$key, $value];
    }

    public function beforeSetUser(AdminSession $subject, User $user)
    {
        LoggerFacade::debug('AdminRawSessionPlugin::beforeSetUser - invoked');

        if (isset($_SESSION['admin']) && is_array($_SESSION['admin'])) {
            $summary = [];
            foreach ($_SESSION['admin'] as $k => $v) {
                $summary[$k] = is_object($v) ? get_class($v) : gettype($v);
            }

            LoggerFacade::debug('AdminRawSessionPlugin:: $_SESSION["admin"] details', [
                'keys' => array_keys($_SESSION['admin']),
                'types' => $summary,
                'serialized_size' => strlen(@serialize($_SESSION['admin']))
            ]);
        } else {
            LoggerFacade::debug('AdminRawSessionPlugin:: $_SESSION["admin"] is not set or not an array');
        }

        return null;
    }

    private function logValue($value)
    {
        if (is_array($value)) {
            $summary = [];
            foreach ($value as $k => $v) {
                $summary[$k] = is_object($v) ? get_class($v) : gettype($v);
            }

            LoggerFacade::debug('AdminRawSessionPlugin:: array value summary', $summary);

            if (isset($value['user']) && $value['user'] instanceof User) {
                LoggerFacade::debug('AdminRawSessionPlugin:: user object size', [
                    'serialized_size' => strlen(@serialize($value['user']))
                ]);
            }
        } else {
            LoggerFacade::debug('AdminRawSessionPlugin:: admin is not array: ' . gettype($value));
        }
    }

    private function getTrace(): array
    {
        return array_map(function ($t) {
            return isset($t['file'], $t['line']) ? "{$t['file']}:{$t['line']}" : '';
        }, array_slice(debug_backtrace(DEBUG_BACKTRACE_IGNORE_ARGS), 1, 7));
    }
}
