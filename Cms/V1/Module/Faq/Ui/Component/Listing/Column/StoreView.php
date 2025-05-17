<?php

namespace Cms\Faq\Ui\Component\Listing\Column;

use Magento\Store\Model\StoreManagerInterface;
use Magento\Ui\Component\Listing\Columns\Column;

class StoreView extends Column
{
    protected $storeManager;

    public function __construct(
        \Magento\Framework\View\Element\UiComponent\ContextInterface $context,
        \Magento\Framework\View\Element\UiComponentFactory $uiComponentFactory,
        StoreManagerInterface $storeManager,
        array $components = [],
        array $data = []
    ) {
        $this->storeManager = $storeManager;
        parent::__construct($context, $uiComponentFactory, $components, $data);
    }

    public function prepareDataSource(array $dataSource)
    {
        if (isset($dataSource['data']['items'])) {
            foreach ($dataSource['data']['items'] as &$item) {
                if (isset($item['store_ids'])) {
                    $storeNames = [];
                    $storeIds = is_array($item['store_ids']) ? $item['store_ids'] : explode(',', $item['store_ids']);

                    foreach ($storeIds as $storeId) {
                        try {
                            $store = $this->storeManager->getStore($storeId);
                            $storeNames[] = $store->getName();
                        } catch (\Magento\Framework\Exception\NoSuchEntityException $e) {
                            // Store not found
                        }
                    }

                    $item['store_ids'] = implode(', ', $storeNames);
                }
            }
        }

        return $dataSource;
    }
}
