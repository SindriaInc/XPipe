<?php
namespace Core\StoreViewDefault\Setup\Patch\Data;

use Magento\Framework\Setup\ModuleDataSetupInterface;
use Magento\Framework\Setup\Patch\DataPatchInterface;
use Magento\Store\Model\Store;
use Magento\Store\Model\StoreManagerInterface;

class RenameDefaultStoreView implements DataPatchInterface
{
    protected $moduleDataSetup;
    protected $storeManager;

    public function __construct(
        ModuleDataSetupInterface $moduleDataSetup,
        StoreManagerInterface $storeManager
    ) {
        $this->moduleDataSetup = $moduleDataSetup;
        $this->storeManager = $storeManager;
    }

    public function apply()
    {
        $this->moduleDataSetup->getConnection()->startSetup();

        try {
            $store = $this->storeManager->getStore('default');
            if ($store && $store->getId()) {
                $store->setName('EN');
                $store->setCode('en_US');
                $store->save();
            }
        } catch (\Exception $e) {
            // Log or handle exception if needed
        }

        $this->moduleDataSetup->getConnection()->endSetup();
    }

    public static function getDependencies()
    {
        return [];
    }

    public function getAliases()
    {
        return [];
    }
}
