<?php
namespace Sindria\SampleApi\Ui\SampleApi;

use Magento\Ui\DataProvider\AbstractDataProvider;
use Magento\Framework\Data\Collection;
use Magento\Framework\DataObject;

class DataProvider extends AbstractDataProvider
{
    protected $loadedData;

    private \Magento\Framework\App\RequestInterface $request;

    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,
        Collection $collection,
        array $meta = [],
        array $data = [],
        \Magento\Framework\App\RequestInterface $request = null
    ) {
        $this->collection = $collection; // Fake Collection
        $this->request = $request;
        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData()
    {


        $itemId = $this->request->getParam('id');

        if ($this->loadedData !== null) {
            return $this->loadedData;
        }


        try {

            $client = new \Zend\Http\Client('https://api.restful-api.dev/objects/' . $itemId, ['timeout' => 10]);
            $client->setMethod('GET');
            $response = $client->send();

            if ($response->isSuccess()) {
                $data = json_decode($response->getBody(), true);

                $item = new \Magento\Framework\DataObject($data);

                $this->collection->addItem($item); // populate collection



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
