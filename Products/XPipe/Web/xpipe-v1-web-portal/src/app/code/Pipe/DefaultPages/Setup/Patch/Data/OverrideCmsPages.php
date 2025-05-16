<?php
namespace Sindria\DefaultPages\Setup\Patch\Data;

use Magento\Cms\Api\PageRepositoryInterface;
use Magento\Cms\Model\PageFactory;
use Magento\Framework\Exception\LocalizedException;
use Magento\Framework\Setup\ModuleDataSetupInterface;
use Magento\Framework\Setup\Patch\DataPatchInterface;
use Magento\Store\Model\Store;
use Magento\Store\Model\StoreManagerInterface;

class OverrideCmsPages implements DataPatchInterface
{
    private $moduleDataSetup;
    private $pageFactory;
    private $pageRepository;
    private $storeManager;

    public function __construct(
        ModuleDataSetupInterface $moduleDataSetup,
        PageFactory $pageFactory,
        PageRepositoryInterface $pageRepository,
        StoreManagerInterface $storeManager
    ) {
        $this->moduleDataSetup = $moduleDataSetup;
        $this->pageFactory = $pageFactory;
        $this->pageRepository = $pageRepository;
        $this->storeManager = $storeManager;
    }

    public function apply()
    {
        $this->moduleDataSetup->getConnection()->startSetup();

        // Get Italian store ID
        $italianStore = $this->storeManager->getStore('italian');
        $italianStoreId = $italianStore->getId();

        // Default (EN) overrides
        $pagesEn = [
            [
                'title' => 'Privacy and Cookie Policy',
                'content_heading' => 'Privacy and Cookie Policy - XPipe',
                'page_layout' => '1column',
                'identifier' => 'privacy-policy-cookie-restriction-mode',
                'content' => '<p><strong>Sindria DI L. P.</strong>(“Sindria”), based in Via Terraggio 7, 20123 Milan, Italy, VAT ID 13017830962, certified email:<a href="mailto:luca.pitzoi@pec.it">luca.pitzoi@pec.it</a>, as Data Controller, is committed to protecting your personal data in accordance with Regulation (EU) 2016/679 (“GDPR”).</p><h2>Types of data collected</h2><ul><li>Identification and contact data</li><li>Technical browsing data (IP, browser, OS)</li><li>Information necessary to provide the requested service</li></ul><h2>Purposes and legal basis</h2><p>We process personal data to:</p><ul><li>deliver and manage XPipe PaaS services;</li><li>provide secure user authentication via federated identity;</li><li>handle technical support and customer inquiries;</li><li>fulfill legal and contractual obligations;</li><li>perform anonymous aggregated analytics, without profiling.</li></ul><h2>Cookies</h2><p>XPipe only uses essential technical cookies for platform operation. Optional cookies are only used with explicit consent. No third-party tracking or profiling is implemented.</p><h2>Security</h2><p>Data is processed with modern technologies in secured environments accessible only to authorized personnel. All communications are encrypted and access is authenticated.</p><h2>User rights</h2><p>You may exercise your rights (access, rectification, erasure, restriction, portability, objection) by contacting<a href="mailto:privacy@sindria.org"> privacy@sindria.org</a>.</p><p>Last update: 2025-04-30</p>',
            ],
            [
                'title' => 'Page Not Found',
                'content_heading' => 'Oops! Page Not Found',
                'page_layout' => '1column',
                'identifier' => 'no-route',
                'content' => '<p>This page no longer exists or has been removed.</p>',
            ],
        ];

        foreach ($pagesEn as $data) {
            try {
                $page = $this->pageFactory->create()->load($data['identifier'], 'identifier');
                if ($page->getId()) {
                    $page->setTitle($data['title']);
                    $page->setContentHeading($data['content_heading']);
                    $page->setPageLayout($data['page_layout']);
                    $page->setContent($data['content']);
                    $page->setStores([Store::DEFAULT_STORE_ID]); // Apply only to default store
                    $this->pageRepository->save($page);
                }
            } catch (LocalizedException $e) {
                throw new LocalizedException(__($e->getMessage()));
            }
        }

        // Italian (IT) localized pages
        $pagesIt = [
            [
                'title' => 'Privacy e Cookie Policy',
                'content_heading' => 'Informativa sulla Privacy - XPipe',
                'page_layout' => '1column',
                'identifier' => 'privacy-policy-cookie-restriction-mode',
                'content' => '<p><strong>Sindria DI L. P.</strong>(“Sindria”), con sede legale in Via Terraggio 7, 20123 Milano (MI), Italia, P.IVA 13017830962, PEC:<a href="mailto:luca.pitzoi@pec.it">luca.pitzoi@pec.it</a>, in qualità di Titolare del trattamento, si impegna a proteggere i dati personali degli utenti nel rispetto del Regolamento (UE) 2016/679 (“GDPR”).</p><h2>Tipologie di dati raccolti</h2><ul><li>Dati identificativi e di contatto</li><li>Dati tecnici di navigazione (IP, browser, sistema operativo)</li><li>Informazioni funzionali al servizio acquistato</li></ul><h2>Finalità e base giuridica del trattamento</h2><p>I dati raccolti vengono trattati per:</p><ul><li>l’erogazione e gestione del servizio PaaS XPipe;</li><li>l’autenticazione sicura degli utenti tramite sistema federato;</li><li>l’assistenza tecnica e la gestione di richieste commerciali;</li><li>adempimenti normativi e contrattuali;</li><li>analisi statistiche anonime aggregate, senza profilazione.</li></ul><h2>Cookie</h2><p>XPipe utilizza cookie tecnici essenziali per il funzionamento della piattaforma e cookie opzionali solo previo consenso. Nessun tracciamento di terze parti o profilazione è attualmente implementato.</p><h2>Sicurezza</h2><p>I dati sono trattati con tecnologie moderne, in ambienti infrastrutturali controllati e con accesso riservato al personale autorizzato. L’accesso ai servizi è protetto da cifratura e procedure di autenticazione sicure.</p><h2>Diritti dell’utente</h2><p>L’interessato può esercitare i propri diritti (accesso, rettifica, cancellazione, limitazione, portabilità, opposizione) scrivendo a<a href="mailto:privacy@sindria.org"> privacy@sindria.org</a>.</p><p>Ultimo aggiornamento: 2025-04-30</p>',
            ],
            [
                'title' => 'Pagina Non Trovata',
                'content_heading' => 'Oops! Pagina non trovata',
                'page_layout' => '1column',
                'identifier' => 'no-route',
                'content' => '<p>La pagina cercata non esiste più o è stata rimossa.</p>',
            ],
        ];

        foreach ($pagesIt as $data) {
            try {
                // Create a new localized page only for the Italian store
                $localizedPage = $this->pageFactory->create();
                $localizedPage->setTitle($data['title']);
                $localizedPage->setContentHeading($data['content_heading']);
                $localizedPage->setPageLayout($data['page_layout']);
                $localizedPage->setIdentifier($data['identifier']);
                $localizedPage->setContent($data['content']);
                $localizedPage->setIsActive(true);
                $localizedPage->setStores([$italianStoreId]);
                $this->pageRepository->save($localizedPage);
            } catch (LocalizedException $e) {
                throw new LocalizedException(__($e->getMessage()));
            }
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
