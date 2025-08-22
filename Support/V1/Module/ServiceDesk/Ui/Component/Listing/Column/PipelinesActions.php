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

    private const SERVICEDESK_TICKET_CLOSE_URL = 'servicedesk/ticket/close';

    protected UrlInterface $urlBuilder;
    private string $closeTicketUrl;
    private Escaper $escaper;

    public function __construct(
        ContextInterface $context,
        UiComponentFactory $uiComponentFactory,
        UrlInterface $urlBuilder,
        Escaper $escaper,
        array $components = [],
        array $data = [],
        string $closeTicketUrl = self::SERVICEDESK_TICKET_CLOSE_URL
    ) {
        $this->urlBuilder = $urlBuilder;
        $this->closeTicketUrl = $closeTicketUrl;
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
                $item[$this->getData('name')]['close'] = [
                    'label' => __('Close Ticket'),
                    'class' => 'action-close-ticket',
                    'href' => $this->urlBuilder->getUrl($this->closeTicketUrl, ['ticket_id' => $item['ticket_id']]),
//                    'data_attribute' => [
//                        'run_id' => $item['run_id'],
//                    ],
                ];
            }
        }

        return $dataSource;
    }

}
