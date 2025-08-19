<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */

namespace Support\ServiceDesk\Ui\Component\Listing\Column;

use Magento\Framework\Escaper;
use Magento\Framework\UrlInterface;
use Magento\Framework\View\Element\UiComponent\ContextInterface;
use Magento\Framework\View\Element\UiComponentFactory;
use Magento\Ui\Component\Listing\Columns\Column;

class PipelinesActions extends Column
{

    private const URL_PATH_SHOW = 'servicedesk/pipeline/index';

    protected UrlInterface $urlBuilder;
    private string $showUrl;
    private Escaper $escaper;

    public function __construct(
        ContextInterface $context,
        UiComponentFactory $uiComponentFactory,
        UrlInterface $urlBuilder,
        Escaper $escaper,
        array $components = [],
        array $data = [],
        string $showUrl = self::URL_PATH_SHOW
    ) {
        $this->urlBuilder = $urlBuilder;
        $this->showUrl = $showUrl;
        $this->escaper = $escaper;
        parent::__construct($context, $uiComponentFactory, $components, $data);
    }


    /**
     * @inheritDoc
     */
    public function prepareDataSource(array $dataSource) : array
    {
        if (isset($dataSource['data']['items'])) {
            foreach ($dataSource['data']['items'] as & $item) {
                $item[$this->getData('name')]['show'] = [
                    'label' => __('Show Pipeline'),
                    'class' => 'action-show-pipeline',
                    'href' => $this->urlBuilder->getUrl($this->showUrl, ['pipeline_id' => $item['name']]),
//                    'data_attribute' => [
//                        'run_id' => $item['run_id'],
//                    ],
                ];
            }
        }
        return $dataSource;
    }

}
