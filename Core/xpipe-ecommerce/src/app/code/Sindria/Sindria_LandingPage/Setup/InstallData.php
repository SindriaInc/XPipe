<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Sindria\LandingPage\Setup;

use Magento\Eav\Setup\EavSetupFactory;
use Magento\Catalog\Setup\CategorySetupFactory;
use Magento\Framework\Setup\InstallDataInterface;
use Magento\Framework\Setup\ModuleContextInterface;
use Magento\Framework\Setup\ModuleDataSetupInterface;
use Magento\Catalog\Model\Product;
use Magento\Framework\App\State;

/**
 * Landing Page InstallData
 *
 * @api
 * @author      Luca Pitzoi <luca.pitzoi@sindria.org>
 * @since 100.0.2
 */
class InstallData implements InstallDataInterface
{
    /**
     * Category setup factory
     *
     * @var CategorySetupFactory
     */
    private $categorySetupFactory;

    /**
     * EAV setup factory
     *
     * @var \Magento\Eav\Setup\EavSetupFactory
     */
    private $eavSetupFactory;

    /**
     * Catalog Product
     *
     * @var \Magento\Catalog\Model\Product
     */
    private $product;

    /**
     * App State for Area Code
     *
     * @var \Magento\Framework\App\State
     **/
    private $state;

    /**
     * Constructor
     *
     * @param CategorySetupFactory $categorySetupFactory
     * @param EavSetupFactory $eavSetupFactory
     * @param Product $product
     * @param State $state
     */
    public function __construct(
        CategorySetupFactory $categorySetupFactory,
        EavSetupFactory $eavSetupFactory,
        Product $product,
        State $state
    ) {
        $this->categorySetupFactory = $categorySetupFactory;
        $this->eavSetupFactory = $eavSetupFactory;
        $this->product = $product;
        $this->state = $state;
    }

    /**
     * {@inheritdoc}
     */
    public function install(ModuleDataSetupInterface $setup, ModuleContextInterface $context)
    {
        $eavSetup = $this->eavSetupFactory->create(['setup' => $setup]);
        $categorySetup = $this->categorySetupFactory->create(['setup' => $setup]);


        //$this->state->setAreaCode(\Magento\Framework\App\Area::AREA_ADMINHTML); // or \Magento\Framework\App\Area::AREA_FRONTEND, depending on your needs

        $attributeSetId = $categorySetup->getDefaultAttributeSetId(\Magento\Catalog\Model\Product::ENTITY);

        $this->storeVirtualProduct('individual', 'Individual',  'individual', $attributeSetId, 999);
        //$this->storeVirtualProduct('pro', 'Pro',  'pro', $attributeSetId, 9999);
        //$this->storeVirtualProduct('enterprise', 'Enterprise',  'enterprise', $attributeSetId, 99000);

//        $this->product->setSku('test-product'); // sku of the product
//        $this->product->setName('Test Product'); // name of the product
//        $this->product->setUrlKey('test-product'); // url key of the product
//        $this->product->setAttributeSetId($attributeSetId);
//        $this->product->setStatus(1); // enabled = 1, disabled = 0
//        $this->product->setVisibility(4); // visibilty of product, 1 = Not Visible Individually, 2 = Catalog, 3 = Search, 4 = Catalog, Search
//        $this->product->setTaxClassId(0); // Tax class id, 0 = None, 2 = Taxable Goods, etc.
//        $this->product->setTypeId('virtual'); // type of product (simple/virtual/downloadable/configurable)
//        //$this->product->setWeight(10); // weight of product
//        //$this->product->setProductHasWeight(0); // 1 = simple product, 0 = virtual product
//        $this->product->setPrice(10); // price of the product
//        $this->product->setWebsiteIds(array(1)); // Website ID
//
//        $this->product->setStockData(
//            array(
//                'use_config_manage_stock' => 0,
//                'manage_stock' => 1,
//                'is_in_stock' => 1,
//                'qty' => 999999
//            )
//        );
//
//        $this->product->save();
    }


    private function storeVirtualProduct(string $sku, string $name, string $urlKey, int $attributeSetId, int $price) : void
    {
        $this->product->setSku($sku); // sku of the product
        $this->product->setName($name); // name of the product
        $this->product->setUrlKey($urlKey); // url key of the product
        $this->product->setAttributeSetId($attributeSetId);
        $this->product->setStatus(1); // enabled = 1, disabled = 0
        $this->product->setVisibility(4); // visibilty of product, 1 = Not Visible Individually, 2 = Catalog, 3 = Search, 4 = Catalog, Search
        $this->product->setTaxClassId(0); // Tax class id, 0 = None, 2 = Taxable Goods, etc.
        $this->product->setTypeId('virtual'); // type of product (simple/virtual/downloadable/configurable)
        //$this->product->setWeight(10); // weight of product
        //$this->product->setProductHasWeight(0); // 1 = simple product, 0 = virtual product
        $this->product->setPrice($price); // price of the product
        $this->product->setWebsiteIds(array(1)); // Website ID

        $this->product->setStockData(
            array(
                'use_config_manage_stock' => 0,
                'manage_stock' => 1,
                'is_in_stock' => 1,
                'qty' => 999999
            )
        );

        $this->product->save();
    }
}
