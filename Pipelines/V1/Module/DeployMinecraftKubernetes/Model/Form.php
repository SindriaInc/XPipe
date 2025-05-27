<?php

namespace Pipelines\DeployMinecraftKubernetes\Model;

use Magento\Framework\DataObject;

class Form extends DataObject
{
    private string $serverName;

    public function getServerName(): string
    {
        return $this->serverName;
    }

    public function setServerName(string $serverName): void
    {
        $this->serverName = $serverName;
    }


}