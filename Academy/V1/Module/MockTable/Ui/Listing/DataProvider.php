<?php
namespace Academy\MockTable\Ui\Listing;

use Magento\Ui\DataProvider\AbstractDataProvider;
use Magento\Framework\Data\Collection\EntityFactoryInterface;

use Core\Logger\Facade\LoggerFacade;
use Academy\MockTable\Model\MockCollection;

class DataProvider extends AbstractDataProvider
{
    protected $collection;

    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,
        EntityFactoryInterface $entityFactory,
        array $meta = [],
        array $data = []
    ) {
        LoggerFacade::debug('DataProvider::__construct', [
            'name' => $name,
            'primaryFieldName' => $primaryFieldName,
            'requestFieldName' => $requestFieldName
        ]);

        // Dati mock
        $mockItems = [
            [
                'pipeline_id' => 101,
                'name' => 'External CI',
                'author' => 'Remote A',
                'created_at' => '2025-01-01'
            ],
            [
                'pipeline_id' => 102,
                'name' => 'External CD',
                'author' => 'Remote B',
                'created_at' => '2025-01-02'
            ]
        ];

        // Inizializza la collection mock
        $this->collection = new MockCollection($entityFactory, $mockItems);

        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData()
    {
        LoggerFacade::debug('DataProvider::getData chiamato');

        $items = [];
        foreach ($this->collection->getItems() as $item) {
            $items[] = $item->getData();
        }

        $response = [
            'items' => $items,
            'totalRecords' => count($items)
        ];

        LoggerFacade::debug('DataProvider::getData RESPONSE', $response);

        return $response;
    }
}
