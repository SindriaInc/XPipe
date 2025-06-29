<?php
namespace Core\SystemEnv\Helper;

class SystemEnvHelper
{
    private const ALLOWED_KEYS = [
        // Core envs
        'CORE_PRODUCT_NAME',
        'CORE_PRODUCT_VERSION',
        'CORE_STOREVIEW_ITALIAN_TOGGLE',
        'CORE_NOTIFICATIONS_ACCESS_TOKEN',
        'CORE_GITHUB_ACCESS_TOKEN',
        // Fnd envs
        'FND_COLLECTOR_BASE_URL',
        'FND_COLLECTOR_ADMIN_USERNAME',
        'FND_COLLECTOR_ADMIN_PASSWORD',
        'FND_NOTIFICATIONS_ACCESS_TOKEN',
        'FND_IDENTITY_SERVER_ACCESS_TOKEN',
        'FND_GATEWAY_ACCESS_TOKEN',
        // Iam envs
        'IAM_COLLECTOR_BASE_URL',
        'IAM_COLLECTOR_ADMIN_USERNAME',
        'IAM_COLLECTOR_ADMIN_PASSWORD',
        'IAM_USERS_ACCESS_TOKEN',
        'IAM_USERS_META_ACCESS_TOKEN',
        'IAM_GROUPS_ACCESS_TOKEN',
        'IAM_POLICIES_ACCESS_TOKEN',
        // Pipelines envs
        'PIPELINES_COLLECTOR_BASE_URL',
        'PIPELINES_COLLECTOR_ADMIN_USERNAME',
        'PIPELINES_COLLECTOR_ADMIN_PASSWORD',
        'PIPELINES_CONFIGMAP_VAULT_BASE_URL',
        'PIPELINES_CONFIGMAP_VAULT_ACCESS_TOKEN',
        'PIPELINES_TEMPLATE_CATALOG_ACCESS_TOKEN',
        'PIPELINES_ORCHESTRATOR_ACCESS_TOKEN',
        // Lab envs
        'LAB_COLLECTOR_BASE_URL',
        'LAB_COLLECTOR_ADMIN_USERNAME',
        'LAB_COLLECTOR_ADMIN_PASSWORD',
        'LAB_CONFIGMAP_VAULT_ACCESS_TOKEN',
        'LAB_SHELL_CATALOG_ACCESS_TOKEN',
        'LAB_ORCHESTRATOR_ACCESS_TOKEN',
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
