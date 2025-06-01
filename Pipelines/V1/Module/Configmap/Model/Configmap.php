<?php

namespace Pipelines\Configmap\Model;

use Magento\Framework\DataObject;

class Configmap extends DataObject
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
     * @return \Pipelines\Configmap\Model\Configmap
     */
    public static function getInstance() : \Pipelines\Configmap\Model\Configmap
    {
        if (!isset(self::$instance)) {
            $className = __CLASS__;
            self::$instance = new $className();
        }

        return self::$instance;
    }

    private int $configmapId;

   private string $name;

    private function __construct(array $data = [])
    {
        parent::__construct($data);
    }

    public function __invoke(
        int $configmapId,
        string $name
    )
    {
        $this->configmapId = $configmapId;
        $this->name = $name;


        $data = [];

        $data['configmap_id'] = $configmapId;
        $data['name'] = $name;


        $this->setData($data);
    }

    public function getConfigmapId() : int
    {
        return $this->configmapId;
    }

    public function getName() : string
    {
        return $this->name;
    }



}