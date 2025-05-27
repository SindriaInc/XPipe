<?php

namespace Pipelines\DeployMinecraftKubernetes\Model;

use Magento\Framework\DataObject;

class DeployMinecraftKubernetes extends DataObject
{
    private string $serverName;

    private string $namespace;

    public function __construct(string $serverName, string $namespace)
    {
        $this->serverName = $serverName;
        $this->namespace = $namespace;


        $data['template_id'] = 1;
        $data['server_name'] = $serverName;
        $data['namespace'] = $namespace;

        parent::__construct($data);
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