<?php

namespace Pipe\DebugSession\Observer;

use Magento\Framework\Event\ObserverInterface;
use Magento\Framework\Event\Observer;
use Core\Logger\Facade\LoggerFacade;

class LogSessionSize implements ObserverInterface
{
    private const MAX_SESSION_SIZE = 262144;

    private static array $lastSnapshot = [];

    public function execute(Observer $observer)
    {
        LoggerFacade::info('LogSessionSize::execute - observer triggered');

        $sessionSerialized = serialize($_SESSION);
        $sessionSize = strlen($sessionSerialized);
        LoggerFacade::info('LogSessionSize::execute - session size: ' . $sessionSize);

        if ($sessionSize > self::MAX_SESSION_SIZE) {
            LoggerFacade::warning('Session exceeds 256KB', [
                'total_size' => $sessionSize,
                'max' => self::MAX_SESSION_SIZE,
                'top_keys' => $this->getTopKeys($_SESSION)
            ]);

            if (isset($_SESSION['admin']) && is_array($_SESSION['admin'])) {
                $admin = $_SESSION['admin'];
                $adminSize = strlen(serialize($admin));

                LoggerFacade::debug('$_SESSION["admin"] analysis', [
                    'size' => $adminSize,
                    'keys' => array_keys($admin),
                    'summary' => $this->getAdminSessionSummary($admin),
                    'trace' => array_map(
                        fn($t) => isset($t['file']) ? ($t['file'] . ':' . $t['line']) : '',
                        array_slice(debug_backtrace(DEBUG_BACKTRACE_IGNORE_ARGS), 1, 6)
                    )
                ]);

                $this->compareWithPreviousSnapshot($admin);
                self::$lastSnapshot = $admin;
            }

            LoggerFacade::warning("Session size of {$sessionSize} exceeded allowed session max size of " . self::MAX_SESSION_SIZE . ".");
        }
    }

    private function getTopKeys(array $session): array
    {
        $result = [];
        foreach ($session as $key => $value) {
            $result[$key] = strlen(serialize($value));
        }

        arsort($result);
        return array_slice($result, 0, 10, true);
    }

    private function getAdminSessionSummary(array $admin): array
    {
        $summary = [];
        foreach ($admin as $key => $value) {
            $summary[$key] = is_object($value) ? get_class($value) : gettype($value);
        }
        return $summary;
    }

    private function compareWithPreviousSnapshot(array $current): void
    {
        if (empty(self::$lastSnapshot)) {
            LoggerFacade::debug('No previous admin session snapshot to compare.');
            return;
        }

        $prev = self::$lastSnapshot;

        $added = array_diff_key($current, $prev);
        $removed = array_diff_key($prev, $current);
        $changed = [];

        foreach (array_intersect_key($current, $prev) as $key => $val) {
            if (serialize($val) !== serialize($prev[$key])) {
                $changed[] = $key;
            }
        }

        LoggerFacade::debug('Session diff analysis', [
            'added_keys' => array_keys($added),
            'removed_keys' => array_keys($removed),
            'changed_keys' => $changed
        ]);
    }
}
