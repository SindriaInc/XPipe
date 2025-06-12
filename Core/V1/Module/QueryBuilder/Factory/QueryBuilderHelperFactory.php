<?php
namespace Core\QueryBuilder\Factory;

use Core\QueryBuilder\Helper\QueryBuilderHelper;

class QueryBuilderHelperFactory
{
    protected $objectManager;

    public function __construct(\Magento\Framework\ObjectManagerInterface $objectManager)
    {
        $this->objectManager = $objectManager;
    }

    public function create(array $data = []): QueryBuilderHelper
    {
        return $this->objectManager->create(QueryBuilderHelper::class, $data);
    }
}
