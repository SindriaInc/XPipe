<?php
namespace Core\SampleApi\Ui\Component\Listing;


use Magento\Framework\Data\Collection;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Sindria\SampleApi\Service\Api\Client;

class DataProvider extends AbstractDataProvider
{
    protected $loadedData;

    /**
     * @var Client
     */
    protected Client $client;

    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,
        Collection $collection,
        array $meta = [],
        array $data = [],
        Client $client
    ) {
        $this->collection = $collection;
        $this->client = $client;
        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData()
    {
        if ($this->loadedData !== null) {
            return $this->loadedData;
        }

        try {
            $response = $this->client->getAll();

            if ($response['success']) {
                foreach ($response['data'] as $row) {
                    $item = new \Magento\Framework\DataObject($row);
                    $this->collection->addItem($item); // populate collection
                }
            } else {
                // Log error if needed: $response['error']
            }

        } catch (\Exception $e) {
            // Log exception if needed
        }

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

