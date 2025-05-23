<?php
namespace Pipelines\FormTemplate\Model;

use Magento\Ui\DataProvider\AbstractDataProvider;
use Core\Logger\Facade\LoggerFacade; // O il logger PSR di Magento, se preferisci

class FormDataProvider extends AbstractDataProvider
{
    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,
        array $meta = [],
        array $data = []
    ) {
        // Log i parametri costruttore, utile per debug
        LoggerFacade::debug('FormDataProvider::__construct', [
            'name' => $name,
            'primaryFieldName' => $primaryFieldName,
            'requestFieldName' => $requestFieldName
        ]);
        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData()
    {
        LoggerFacade::debug("FormDataProvider::getData chiamato");

        $items = [
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

        $response = [
            'items' => $items,
            'totalRecords' => count($items)
        ];

        LoggerFacade::debug('FormDataProvider::getData RESPONSE', $response);

        return $response;
    }
}
