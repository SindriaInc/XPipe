<?php
namespace Core\SystemCommand\Helper;

use Psr\Log\LoggerInterface;

class SystemCommandHelper
{
    private const ALLOWED_COMMANDS = [
        'ls',
        'df',
        'uptime',
        'date',
        'whoami'
    ];

    private const ARGUMENT_PATTERN = '/^[a-zA-Z0-9_\-\/\.]*$/';

    private LoggerInterface $logger;

    public function __construct(LoggerInterface $logger)
    {
        $this->logger = $logger;
    }

    public function run(string $cmd, array $args = []): string
    {
        $base = escapeshellcmd($cmd);
        if (!in_array($base, self::ALLOWED_COMMANDS)) {
            $this->logger->warning("Blocked attempt to run disallowed command: {$cmd}");
            throw new \RuntimeException("Command '{$cmd}' is not allowed.");
        }

        foreach ($args as $arg) {
            if (!preg_match(self::ARGUMENT_PATTERN, $arg)) {
                $this->logger->warning("Blocked attempt with invalid argument '{$arg}' for command '{$cmd}'");
                throw new \RuntimeException("Argument '{$arg}' is not allowed.");
            }
        }

        $full = $base . ' ' . implode(' ', array_map('escapeshellarg', $args));
        $this->logger->info("Executing command: {$full}");

        $output = trim(shell_exec($full) ?: '');
        $this->logger->debug("Command output: " . $output);

        return $output;
    }
}
