<?php
namespace Pipe\DebugSession\Plugin;

use Magento\Backend\Model\Auth\Session as AdminSession;
use Core\Logger\Facade\LoggerFacade;

class AdminSessionPlugin
{
    public function beforeSetData(AdminSession $subject, $key, $value)
    {
        LoggerFacade::warning('AdminSessionPlugin::beforeSetData', [
            'key' => $key,
            'value_type' => is_object($value) ? get_class($value) : gettype($value),
            'trace' => $this->getTrace()
        ]);
    }

    public function beforeOffsetSet(AdminSession $subject, $offset, $value)
    {
        LoggerFacade::warning('AdminSessionPlugin::beforeOffsetSet', [
            'offset' => $offset,
            'value_type' => is_object($value) ? get_class($value) : gettype($value),
            'trace' => $this->getTrace()
        ]);
    }

    private function getTrace(): array
    {
        return array_map(function ($trace) {
            return isset($trace['file']) && isset($trace['line'])
                ? $trace['file'] . ':' . $trace['line']
                : '';
        }, array_slice(debug_backtrace(DEBUG_BACKTRACE_IGNORE_ARGS), 1, 5));
    }
}
