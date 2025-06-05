<?php
namespace Pipe\LanguageSelector\Block;

use Magento\Framework\View\Element\Template;
use Magento\Store\Model\StoreManagerInterface;

class Selector extends Template
{
    protected $storeManager;

    public function __construct(
        Template\Context $context,
        StoreManagerInterface $storeManager,
        array $data = []
    ) {
        $this->storeManager = $storeManager;
        parent::__construct($context, $data);
    }

    public function getCurrentStoreCode()
    {
        return $this->storeManager->getStore()->getCode();
    }
}
