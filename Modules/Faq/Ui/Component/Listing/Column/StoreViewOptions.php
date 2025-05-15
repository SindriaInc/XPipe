<?php

namespace Sindria\Faq\Ui\Component\Listing\Column;

use Magento\Framework\Data\OptionSourceInterface;
use Magento\Store\Model\StoreManagerInterface;

class StoreViewOptions implements OptionSourceInterface
{
    /**
     * @var StoreManagerInterface
     */
    protected $storeManager;

    public function __construct(StoreManagerInterface $storeManager)
    {
        $this->storeManager = $storeManager;
    }

    public function toOptionArray()
    {
        $options = [];

        foreach ($this->storeManager->getStores() as $store) {
            $options[] = [
                'label' => $store->getName(),
                'value' => $store->getId()
            ];
        }

        return $options;
    }
}
