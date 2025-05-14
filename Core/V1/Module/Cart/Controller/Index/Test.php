<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Core\LandingPage\Controller\Index;

use Magento\Framework\View\Element\Template;
use Magento\Framework\View\Element\Template\Context;
use Magento\Framework\View\Page\Config;
use Magento\Catalog\Model\ResourceModel\Product\CollectionFactory;
use Magento\Catalog\Model\CategoryFactory;
use Magento\Framework\Registry;
use Magento\Catalog\Model\Product\Visibility;
use Magento\Catalog\Model\Product\Attribute\Source\Status;

class Test extends \Magento\Framework\App\Action\Action
{

    protected $collectionFactory;
    protected $_pageFactory;

    public function __construct(
        \Magento\Catalog\Model\ResourceModel\Product\CollectionFactory $collectionFactory,
        \Magento\Framework\App\Action\Context $context,
        \Magento\Framework\View\Result\PageFactory $pageFactory
    )
    {
        $this->collectionFactory = $collectionFactory;
        $this->_pageFactory = $pageFactory;
        return parent::__construct($context);
    }

    public function execute()
    {
        echo "Hello World";

        $productCollection = $this->collectionFactory->create();

        $productCollection->addAttributeToSelect('*');

        dd($productCollection);

        foreach ($productCollection as $product){
            echo 'Name  =  '.$product->getName();
        }

        exit;
    }



    public function yourMethod()
    {

        $productCollection = $this->collectionFactory->create();

        $productCollection->addAttributeToSelect('*');

        foreach ($productCollection as $product){
            echo 'Name  =  '.$product->getName();
        }
    }
}
