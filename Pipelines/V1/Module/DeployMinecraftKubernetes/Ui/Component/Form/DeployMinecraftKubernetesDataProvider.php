<?php
namespace Pipelines\DeployMinecraftKubernetes\Ui\Component\Form;

use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\Data\Collection\EntityFactoryInterface;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Pipelines\DeployMinecraftKubernetes\Model\DeployMinecraftKubernetes;
use Pipelines\DeployMinecraftKubernetes\Model\Form\DeployMinecraftKubernetesCollection;


class DeployMinecraftKubernetesDataProvider extends AbstractDataProvider
{

    protected $collection;

    /**
     * @var array
     */
    public $loadedData;


    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,

        EntityFactoryInterface $entityFactory,
        array $meta = [],
        array $data = []
    ) {
        LoggerFacade::debug('DeployMinecraftKubernetesDataProvider::__construct', [
            'name' => $name,
            'primaryFieldName' => $primaryFieldName,
            'requestFieldName' => $requestFieldName
        ]);

        $form = \Pipelines\DeployMinecraftKubernetes\Model\DeployMinecraftKubernetes::getInstance();

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
            'Demo',
            'sindria-mc',
            $players,
            $visibility,
            'Sindria MC',
            $gameMode,
            $difficulty
        );

        $this->collection = new DeployMinecraftKubernetesCollection($entityFactory, $form);

        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData() : array
    {
        $entry = [];

        $entry = $this->collection->getFirstItem();

        $this->loadedData[null] = $entry->getData();

        return $this->loadedData;
    }
}
