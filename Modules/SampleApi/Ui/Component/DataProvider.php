<?php
namespace Sindria\SampleApi\Ui\Component;

use Magento\Ui\DataProvider\AbstractDataProvider;
use Magento\Framework\Data\Collection;
use Magento\Framework\DataObject;

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
        $this->collection = $collection; // Fake Collection
        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData()
    {
        if ($this->loadedData !== null) {
            return $this->loadedData;
        }


        $items = [];

        try {

            $client = new \Zend\Http\Client('https://api.restful-api.dev/objects', ['timeout' => 10]);
            $client->setMethod('GET');
            $response = $client->send();

            if ($response->isSuccess()) {
                $data = json_decode($response->getBody(), true);

                foreach ($data as $row) {
                    $item = new \Magento\Framework\DataObject($row);
                    $this->collection->addItem($item); // populate collection

                }

            }
        } catch (\Exception $e) {
            // Log error
        }

//        dd(array_map(function ($item) {
//            return $item->getData();
//        }, $this->collection->getItems()));


//        $this->loadedData = [
//            'totalRecords' => $this->collection->getSize(),
//            'items' => array_values(array_map(function ($item) {
//                return $item->getData();
//            }, $this->collection->getItems())),
//        ];

        $this->loadedData = [
            'totalRecords' => $this->collection->getSize(),
            'items' => array_values(array_map(function ($item) {
                $data = $item->getData();

                return [
                    'id' => $data['id'] ?? null,
                    'name' => $data['name'] ?? '',
                    'color' => $data['data']['color'] ?? '',
                    'capacity' => $data['data']['capacity'] ?? ''
                ];
            }, $this->collection->getItems())),
        ];

        return $this->loadedData;
    }

}
