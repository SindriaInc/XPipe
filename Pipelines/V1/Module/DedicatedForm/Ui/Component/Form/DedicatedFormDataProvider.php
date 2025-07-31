<?php
namespace Pipelines\DedicatedForm\Ui\Component\Form;

use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\ObjectManager;
use Magento\Framework\Data\Collection\EntityFactoryInterface;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Pipelines\DedicatedForm\Model\DedicatedForm;
use Pipelines\DedicatedForm\Model\Form\DedicatedFormCollection;


class DedicatedFormDataProvider extends AbstractDataProvider
{

    protected $collection;

    /**
     * @var array
     */
    public $loadedData;

    private $templateId;


    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,

        EntityFactoryInterface $entityFactory,
        array $meta = [],
        array $data = []
    ) {
        LoggerFacade::debug('DedicatedFormDataProvider::__construct', [
            'name' => $name,
            'primaryFieldName' => $primaryFieldName,
            'requestFieldName' => $requestFieldName
        ]);


        // Recupera session in modo statico da ObjectManager
        $objectManager = ObjectManager::getInstance();
        $session = $objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class);

        // If session key does not exist, return null as magento expected to render form default parameters without id.
        // It is not a bug, it's a feature.
        $this->templateId = $session->getData('template_id');
        LoggerFacade::debug('DedicatedFormDataProvider::template_id from session', [
            'template_id' => $this->templateId
        ]);

        $form = \Pipelines\DedicatedForm\Model\DedicatedForm::getInstance();

        $configMap[] = [];
        $configMap[0] = ['label' => 'XPipe System', 'value' => 1];
        $configMap[1] = ['label' => 'XPipe PaaS', 'value' => 2];
        $configMap[2] = ['label' => 'Barilla Production', 'value' => 3];
        $configMap[3] = ['label' => 'Samuele Riviera Nuragica Production', 'value' => 4];

        $players[] = [];
        $players[0] = ['label' => 'Max 5 Players', 'value' => 5];
        $players[1] = ['label' => 'Max 10 Players', 'value' => 10];
        $players[2] = ['label' => 'Max 20 Players', 'value' => 20];
        $players[3] = ['label' => 'Max 100 Players', 'value' => 100];

        $visibility[] = [];
        $visibility[0] = ['label' => 'Public', 'value' => 'public'];
        $visibility[1] = ['label' => 'Private', 'value' => 'private'];

        $gameMode[] = [];
        $gameMode[0] = ['label' => 'Survival', 'value' => 'survival'];
        $gameMode[1] = ['label' => 'Creative', 'value' => 'creative'];
        $gameMode[2] = ['label' => 'Other', 'value' => 'other'];


        $difficulty[] = [];
        $difficulty[0] = ['label' => 'Easy', 'value' => 'easy'];
        $difficulty[1] = ['label' => 'Normal', 'value' => 'normal'];
        $difficulty[2] = ['label' => 'Hardcore', 'value' => 'hardcore'];

        $form(
            $this->templateId,
            'mario.rossi',
            $configMap,
            'Demo',
            'Sindria MC',
            'sindria-mc',
            $players,
            $visibility,
            $gameMode,
            $difficulty
        );


        $this->collection = new DedicatedFormCollection($entityFactory, $form);

        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData() : array
    {
        $entry = [];

        $entry = $this->collection->getFirstItem();

        $this->loadedData[$this->templateId] = $entry->getData();

        return $this->loadedData;
    }
}
