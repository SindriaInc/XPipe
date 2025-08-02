<?php
namespace Core\StoreViewItalian\Setup\Patch\Data;

use Magento\Framework\Setup\Patch\DataPatchInterface;
use Magento\Store\Model\StoreFactory;
use Magento\Store\Model\StoreManagerInterface;
use Magento\Framework\App\Config\Storage\WriterInterface;
use Magento\Store\Model\ScopeInterface;

use Core\StoreViewItalian\Helper\StoreViewItalianHelper;

class CreateItalianStoreView implements DataPatchInterface
{
    protected $storeFactory;
    protected $storeManager;
    protected $configWriter;

    private bool $toggle;

    public function __construct(
        StoreFactory $storeFactory,
        StoreManagerInterface $storeManager,
        WriterInterface $configWriter
    ) {
        $this->storeFactory = $storeFactory;
        $this->storeManager = $storeManager;
        $this->configWriter = $configWriter;

        $this->toggle = StoreViewItalianHelper::getCoreStoreViewItalianToggle();
    }

    public function apply()
    {
        try {
            $existingStore = $this->storeManager->getStore('italian');
            if ($existingStore && $existingStore->getId()) {
                return $this;
            }
        } catch (\Exception $e) {
        }

        dump(getenv('CORE_STOREVIEW_ITALIAN_TOGGLE'));
        dump(gettype($this->toggle));
        dump($this->toggle);

        if ($this->toggle === 1 || $this->toggle === true) {
            $website = $this->storeManager->getWebsite();
            $storeGroupId = $website->getDefaultGroupId();

            $store = $this->storeFactory->create();
            $store->setCode('it_IT')
                ->setWebsiteId($website->getId())
                ->setGroupId($storeGroupId)
                ->setName('IT')
                ->setSortOrder(10)
                ->setIsActive(1)
                ->save();

            // Set only the locale to Italian; no base_url override
            $this->configWriter->save(
                'general/locale/code',
                'it_IT',
                ScopeInterface::SCOPE_STORES,
                $store->getId()
            );

            return $this;
        }

        return $this;
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
