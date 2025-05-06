<?php
namespace Sindria\DefaultPages\Setup\Patch\Data;

use Magento\Cms\Api\PageRepositoryInterface;
use Magento\Framework\Setup\ModuleDataSetupInterface;
use Magento\Framework\Setup\Patch\DataPatchInterface;
use Magento\Store\Model\Store;
use Magento\Cms\Model\PageFactory;
use Magento\Framework\Exception\LocalizedException;

class OverrideCmsPages implements DataPatchInterface
{
    private $moduleDataSetup;
    private $pageFactory;
    private $pageRepository;

    public function __construct(
        ModuleDataSetupInterface $moduleDataSetup,
        PageFactory $pageFactory,
        PageRepositoryInterface $pageRepository
    ) {
        $this->moduleDataSetup = $moduleDataSetup;
        $this->pageFactory = $pageFactory;
        $this->pageRepository = $pageRepository;
    }

    public function apply()
    {
        $this->moduleDataSetup->getConnection()->startSetup();

        $pages = [
            [
                'title' => ' Privacy and Cookie Policy ',
                'content_heading' => 'Privacy and Cookie Policy',
                'page_layout' => '1column',
                'identifier' => 'privacy-policy-cookie-restriction-mode',
                'content' => '<h1>Custom Privacy Policy</h1><p>Il tuo nuovo contenuto personalizzato qui.</p>',
            ],
            [
                'title' => 'Page Not Found',
                'content_heading' => 'Oops! Pagina non trovata',
                //page_layout' => '2columns-right',
                'page_layout' => '1column',
                'identifier' => 'no-route',
                'content' => '<h1>Oops! Pagina non trovata</h1><p>Questa pagina non esiste più o è stata rimossa.</p>',
            ],
        ];

        foreach ($pages as $data) {
            try {
                $page = $this->pageFactory->create()->load($data['identifier'], 'identifier');
                if ($page->getId()) {
                    $page->setTitle($data['title']);
                    $page->setContentHeading($data['content_heading']);
                    $page->setPageLayout($data['page_layout']);
                    $page->setContent($data['content']);
                    $this->pageRepository->save($page);
                }
            } catch (LocalizedException $e) {
                // Log or handle the exception if necessary
                throw new LocalizedException(__($e->getMessage()));
            }
        }

        $this->moduleDataSetup->getConnection()->endSetup();
    }

    public static function getDependencies() {
        return [];
    }

    public function getAliases() {
        return [];
    }
}
