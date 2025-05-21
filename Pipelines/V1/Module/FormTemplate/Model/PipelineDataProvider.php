<?php
namespace Pipelines\FormTemplate\Model;

use Magento\Ui\DataProvider\AbstractDataProvider;

class PipelineDataProvider extends AbstractDataProvider
{
    protected $loadedData;

    public function getData()
    {
        if (isset($this->loadedData)) {
            return $this->loadedData;
        }

        $items = [
            ['pipeline_id' => 1, 'name' => 'CI Pipeline', 'author' => 'Alice', 'created_at' => '2024-10-01'],
            ['pipeline_id' => 2, 'name' => 'CD Pipeline', 'author' => 'Bob', 'created_at' => '2024-10-15'],
            ['pipeline_id' => 3, 'name' => 'DevOps Flow', 'author' => 'Charlie', 'created_at' => '2025-01-20'],
        ];

        $this->loadedData = [
            'totalRecords' => count($items),
            'items' => $items
        ];

        return $this->loadedData;
    }
}
