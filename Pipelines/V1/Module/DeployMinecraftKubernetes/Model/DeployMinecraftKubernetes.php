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

    private int $templateId;

    private string $owner;

    private array $configMap;
    private string $serverName;
    private string $serverMotd;

    private string $namespace;

    private array $players;

    private array $visibility;

    private array $gameMode;

    private array $difficulty;

    private function __construct(array $data = [])
    {
        parent::__construct($data);
    }

    public function __invoke(
        int $templateId,
        string $owner,
        array $configMap,
        string $serverName,
        string $serverMotd,
        string $namespace,
        array $players = [],
        array $visibility = [],
        array $gameMode = [],
        array $difficulty = []
    )
    {
        $this->templateId = $templateId;
        $this->owner = $owner;
        $this->configMap = $configMap;
        $this->serverName = $serverName;
        $this->serverMotd = $serverMotd;
        $this->namespace = $namespace;
        $this->players = $players;
        $this->visibility = $visibility;
        $this->gameMode = $gameMode;
        $this->difficulty = $difficulty;

        $data = [];

        $data['template_id'] = $templateId;
        $data['owner'] = $owner;
        $data['config_map'] = $configMap;
        $data['server_name'] = $serverName;
        $data['server_motd'] = $serverMotd;
        $data['namespace'] = $namespace;
        $data['players'] = $players[2]['value'];
        $data['visibility'] = $visibility[1]['value'];
        $data['game_mode'] = $gameMode[0]['value'];
        $data['difficulty'] = $difficulty[1]['value'];

        $this->setData($data);
    }

    public function getTemplateId() : int
    {
        return $this->templateId;
    }

    public function getOwner() : string
    {
        return $this->owner;
    }

    public function getConfigMap() : array
    {
        return $this->configMap;
    }

    public function getServerName() : string
    {
        return $this->serverName;
    }

    public function getServerMotd() : string
    {
        return $this->serverMotd;
    }

    public function getNamespace() : string
    {
        return $this->namespace;
    }

    public function getPlayers() : array
    {
        return $this->players;
    }

    public function getVisibility() : array
    {
        return $this->visibility;
    }



    public function getGameMode() : array
    {
        return $this->gameMode;
    }

    public function getDifficulty() : array
    {
        return $this->difficulty;
    }


}