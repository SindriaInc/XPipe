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

    private array $players;

    private array $visibility;

    private string $serverMotd;

    private array $gameMode;

    private array $difficulty;

    private function __construct(array $data = [])
    {
        parent::__construct($data);
    }

    public function __invoke(
        string $serverName,
        string $namespace,
        array $players = [],
        array $visibility = [],
        string $serverMotd = '',
        array $gameMode = [],
        array $difficulty = []
    )
    {
        $this->serverName = $serverName;
        $this->namespace = $namespace;
        $this->players = $players;
        $this->visibility = $visibility;
        $this->serverMotd = $serverMotd;
        $this->gameMode = $gameMode;
        $this->difficulty = $difficulty;

        $data = [];

        $data['template_id'] = 1;
        $data['server_name'] = $serverName;
        $data['namespace'] = $namespace;
        $data['players'] = $players;
        $data['visibility'] = $visibility;
        $data['server_motd'] = $serverMotd;
        $data['game_mode'] = $gameMode;
        $data['difficulty'] = $difficulty;

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

    public function getPlayers(): array
    {
        return $this->players;
    }

    public function getVisibility(): array
    {
        return $this->visibility;
    }

    public function getServerMotd(): string
    {
        return $this->serverMotd;
    }

    public function getGameMode(): array
    {
        return $this->gameMode;
    }

    public function getDifficulty(): array
    {
        return $this->difficulty;
    }


}