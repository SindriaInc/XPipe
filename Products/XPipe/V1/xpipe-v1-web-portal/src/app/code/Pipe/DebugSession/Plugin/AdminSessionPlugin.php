<?php

namespace Pipe\DebugSession\Plugin;

use Magento\Backend\Model\Auth\Session as AdminSession;
use Core\Logger\Facade\LoggerFacade;

class AdminSessionPlugin
{
    /**
     * Intercetta i settaggi diretti via ->setData()
     */
    public function beforeSetData(AdminSession $subject, $key, $value)
    {
        LoggerFacade::warning('AdminSessionPlugin::beforeSetData', [
            'key' => $key,
            'value_type' => $this->getType($value),
            'value_summary' => $this->summarize($value),
            'trace' => $this->getTrace()
        ]);

        return [$key, $value];
    }

    /**
     * Intercetta i settaggi via accesso array-style
     */
    public function beforeOffsetSet(AdminSession $subject, $offset, $value)
    {
        LoggerFacade::warning('AdminSessionPlugin::beforeOffsetSet', [
            'offset' => $offset,
            'value_type' => $this->getType($value),
            'value_summary' => $this->summarize($value),
            'trace' => $this->getTrace()
        ]);

        return [$offset, $value];
    }

    /**
     * Ottiene tipo semplificato
     */
    private function getType($value): string
    {
        return is_object($value) ? get_class($value) : gettype($value);
    }

    /**
     * Fornisce riepilogo chiavi/tipi se è un array
     */
    private function summarize($value)
    {
        if (is_array($value)) {
            $summary = [];
            foreach ($value as $k => $v) {
                $summary[$k] = is_object($v) ? get_class($v) : gettype($v);
            }
            return $summary;
        }

        return 'not array';
    }

    /**
     * Ridotta stack trace utile per il log
     */
    private function getTrace(): array
    {
        return array_map(function ($trace) {
            return isset($trace['file'], $trace['line'])
                ? $trace['file'] . ':' . $trace['line']
                : '';
        }, array_slice(debug_backtrace(DEBUG_BACKTRACE_IGNORE_ARGS), 1, 6));
    }
}
