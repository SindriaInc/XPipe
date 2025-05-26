<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */

namespace Pipelines\PipeManager\Ui\Component\Listing\Column;

use Magento\Framework\Escaper;
use Magento\Framework\UrlInterface;
use Magento\Framework\View\Element\UiComponent\ContextInterface;
use Magento\Framework\View\Element\UiComponentFactory;
use Magento\Ui\Component\Listing\Columns\Column;

class RunsActions extends Column
{
    private const URL_PATH_SHOW = 'pipemanager/pipeline_run/show';
    private const URL_PATH_STOP = 'pipemanager/pipeline_run/stop';

    protected UrlInterface $urlBuilder;
    private string $showUrl;

    private string $stopUrl;
    private Escaper $escaper;

    public function __construct(
        ContextInterface $context,
        UiComponentFactory $uiComponentFactory,
        UrlInterface $urlBuilder,
        Escaper $escaper,
        array $components = [],
        array $data = []
    ) {
        $this->urlBuilder = $urlBuilder;
        $this->showUrl = self::URL_PATH_SHOW;
        $this->stopUrl = self::URL_PATH_STOP;
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


                $name = $this->getData('name');
                $item[$name]['show_logs'] = [
                    'label' => __('Show Logs'),
                    'class' => 'action-show-logs',
                    'href' => $this->urlBuilder->getUrl($this->showUrl, ['run_id' => $item['run_id']]),
                ];

                if ($item['status'] === 'in_progress') {
                    $item[$name]['stop'] = [
                        'label' => __('Stop'),
                        'class' => 'action-stop',
                        'href' => $this->urlBuilder->getUrl($this->stopUrl, [
                            'pipeline_id' => $item['pipeline_id'],
                            'run_id' => $item['run_id']
                        ]),
                    ];
                }

            }

        }
        return $dataSource;
    }

}
