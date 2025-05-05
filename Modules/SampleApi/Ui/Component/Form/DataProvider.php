<?php
namespace Sindria\SampleApi\Ui\Component\Form;

use Magento\Framework\Data\Collection;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Sindria\SampleApi\Service\Api\Client;

class DataProvider extends AbstractDataProvider
{
    protected $loadedData;

    private \Magento\Framework\App\RequestInterface $request;

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
        \Magento\Framework\App\RequestInterface $request = null,
        Client $client
    ) {
        $this->collection = $collection; // Fake Collection
        $this->request = $request;
        $this->client = $client;
        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData()
    {
        $itemId = $this->request->getParam('id');

        if ($itemId == null) {
            return $this->loadedData = [];
        }

        try {
            $response = $this->client->get($itemId);

            if ($response['success']) {
                $item = new \Magento\Framework\DataObject($response['data']);
                $this->collection->addItem($item); // populate collection
            } else {
                // Log error if needed: $response['error']
            }
        } catch (\Exception $e) {
            // Log error
        }

        $entry = $this->collection->getFirstItem()->getData();

        $formattedEntry = [
            'id' => $entry['id'],
            'name' => $entry['name'] ?? '',
            'color' => $entry['data']['color'] ?? '',
            'capacity' => $entry['data']['capacity'] ?? '',
        ];

        $this->loadedData[$entry['id']]['data'] = $formattedEntry;

        return $this->loadedData;
    }

}
