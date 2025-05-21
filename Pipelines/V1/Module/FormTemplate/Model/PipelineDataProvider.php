<?php
namespace Pipelines\FormTemplate\Model;

use Magento\Framework\Data\Collection;
use Magento\Framework\ObjectManagerInterface;
use Magento\Ui\DataProvider\AbstractDataProvider;

class PipelineDataProvider extends AbstractDataProvider
{
    protected $collection;

    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,
        ObjectManagerInterface $objectManager,
        array $meta = [],
        array $data = []
    ) {
        // Creiamo una collection fake
        $this->collection = $objectManager->create(Collection::class);
        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData()
    {
        return [
            'items' => [
                ['pipeline_id' => 1, 'name' => 'CI Pipeline', 'author' => 'Alice', 'created_at' => '2024-10-01'],
                ['pipeline_id' => 2, 'name' => 'CD Pipeline', 'author' => 'Bob', 'created_at' => '2024-10-15'],
                ['pipeline_id' => 3, 'name' => 'DevOps Flow', 'author' => 'Charlie', 'created_at' => '2025-01-20'],
            ],
            'totalRecords' => 3
        ];
    }
}
