<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Pipe\LandingPage\Setup\Patch\Data;

use Magento\Framework\Setup\Patch\DataPatchInterface;
use Magento\Framework\Setup\ModuleDataSetupInterface;
use Magento\Store\Model\Store;
use Magento\Store\Model\StoreManagerInterface;

use Pipe\LandingPage\Setup\Category;
use Pipe\LandingPage\Model\Virtual\Product;


class InstallVirtualProducts implements DataPatchInterface
{
    /**
     * Setup class for category
     *
     * @var Category
     */
    protected $categorySetup;


    /**
     * Setup class for products
     *
     * @var Product
     */
    protected $productSetup;


    /**
     * @var ModuleDataSetupInterface $moduleDataSetup
     */

   /** @var StoreManagerInterface  */
    protected $storeManager;

    /**
     * InstallVirtualProducts constructor.
     * @param Product $productSetup
     * @param Category $categorySetup
     * @param StoreManagerInterface|null $storeManager
     */
    public function __construct(Product $productSetup, Category $categorySetup,StoreManagerInterface $storeManager = null)
    {
        $this->categorySetup = $categorySetup;
        $this->productSetup = $productSetup;
        $this->storeManager = $storeManager ?: \Magento\Framework\App\ObjectManager::getInstance()
            ->get(StoreManagerInterface::class);
    }

    public function apply()
    {
        $this->categorySetup->install(['Pipe_LandingPage::fixtures/Virtual/categories.csv']);
        $this->productSetup->install(
            [
                'Pipe_LandingPage::fixtures/Virtual/products_virtual.csv'
            ],
            [
                'Pipe_LandingPage::fixtures/Virtual/images_virtual.csv'
            ]
        );
    }

    /**
     * {@inheritdoc}
     */
    public function getAliases()
    {
        return [];
    }

    /**
     * {@inheritdoc}
     */
    public static function getDependencies()
    {
        return [];
    }

}
