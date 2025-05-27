<?php
namespace Academy\SampleApi\Ui\Component\Form;

use Magento\Framework\Data\Collection;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Academy\SampleApi\Service\Api\Client;

class DataProvider extends AbstractDataProvider
{
    protected $loadedData;

    private \Magento\Framework\App\RequestInterface $request;

    /**
     * @var Client
     */
    protected Client $client;

    protected $urlBuilder;


    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,
        Collection $collection,
        array $meta = [],
        array $data = [],
        \Magento\Framework\App\RequestInterface $request = null,
        Client $client,
        \Magento\Framework\UrlInterface $urlBuilder
    ) {
        $this->collection = $collection; // Fake Collection
        $this->request = $request;
        $this->client = $client;
        $this->urlBuilder = $urlBuilder;
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

        dd($this->loadedData);

        return $this->loadedData;
    }

    public function getConfigData()
    {
        $configData = parent::getConfigData();

        $id = (int) $this->request->getParam('id');
        $submitUrl = $id
            ? $this->urlBuilder->getUrl('sampleapi/index/edit', ['id' => $id])
            : $this->urlBuilder->getUrl('sampleapi/index/save');


        $configData['submit_url'] = $submitUrl;

        return $configData;
    }

}
