<?php
namespace Core\SystemEnv\Helper;

class SystemEnvHelper
{
    private const ALLOWED_KEYS = [
        'APP_MODE',
        'MAGENTO_ENV',
        'REDIS_HOST',
        'LOG_LEVEL'
    ];

    private array $env = [];

    public function __construct()
    {
        foreach (self::ALLOWED_KEYS as $key) {
            $this->env[$key] = getenv($key) !== false ? getenv($key) : null;
        }
    }

    public function get(string $key, $default = null)
    {
        return $this->env[$key] ?? $default;
    }

    public function all(): array
    {
        return $this->env;
    }
}
