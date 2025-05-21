<?php
namespace Pipelines\FormTemplate\Ui\Component\Listing;


use Magento\Framework\Data\Collection;
use Magento\Ui\DataProvider\AbstractDataProvider;

class DataProvider extends AbstractDataProvider
{
    protected $loadedData;


    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,
        Collection $collection,
        array $meta = [],
        array $data = []
    ) {
        $this->collection = $collection;
        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData()
    {
        if ($this->loadedData !== null) {
            return $this->loadedData;
        }


        $this->loadedData = [
            'totalRecords' => 0,
            'items' => []
        ];

        return $this->loadedData;
    }
}
