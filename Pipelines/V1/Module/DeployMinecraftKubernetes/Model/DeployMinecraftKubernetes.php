<?php

namespace Pipelines\DeployMinecraftKubernetes\Model;

use Magento\Framework\DataObject;

class DeployMinecraftKubernetes extends DataObject
{

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    /**
     * Get singleton instance
     *
     * @return \Pipelines\DeployMinecraftKubernetes\Model\DeployMinecraftKubernetes
     */
    public static function getInstance() : \Pipelines\DeployMinecraftKubernetes\Model\DeployMinecraftKubernetes
    {
        if (!isset(self::$instance)) {
            $className = __CLASS__;
            self::$instance = new $className();
        }

        return self::$instance;
    }

    private string $serverName;

    private string $namespace;

    private function __construct(array $data = [])
    {
        parent::__construct($data);
    }

    public function __invoke(string $serverName, string $namespace)
    {
        $this->serverName = $serverName;
        $this->namespace = $namespace;

        $data = [];

        $data['template_id'] = 1;
        $data['server_name'] = $serverName;
        $data['namespace'] = $namespace;

        $this->setData($data);
    }

    public function getServerName(): string
    {
        return $this->serverName;
    }

    public function getNamespace(): string
    {
        return $this->namespace;
    }


}