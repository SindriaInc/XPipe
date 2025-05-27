<?php
namespace Pipelines\DeployMinecraftKubernetes\Model\Form;

use Magento\Framework\Data\Collection as DataCollection;
use Magento\Framework\DataObject;
use Magento\Framework\Data\Collection\EntityFactoryInterface;
use Core\Logger\Facade\LoggerFacade;
use Pipelines\DeployMinecraftKubernetes\Model\Form;

class Collection extends DataCollection
{
    protected $pageSize = null;
    protected $curPage = 1;

    public function __construct(EntityFactoryInterface $entityFactory, Form $form)
    {
        parent::__construct($entityFactory);

//        LoggerFacade::debug('GitHubActionsCollection::__construct', ['itemsData' => $itemsData]);



//        foreach ($itemsData as $itemData) {

            //TODO: new model that extends data object instead creating the data object with the itemData array
//            $this->addItem($itemData);
            $this->addItem($form);
//        }
    }

    public function addOrder($field, $direction)
    {
        LoggerFacade::debug('GitHubActionsCollection::addOrder', ['field' => $field, 'direction' => $direction]);
        $items = $this->getItems();
        usort($items, function ($a, $b) use ($field, $direction) {
            $v1 = $a->getData($field);
            $v2 = $b->getData($field);
            if ($v1 == $v2) return 0;
            if ($direction == 'ASC' || $direction == 'asc') {
                return ($v1 < $v2) ? -1 : 1;
            } else {
                return ($v1 > $v2) ? -1 : 1;
            }
        });
        $this->_items = [];
        foreach ($items as $item) {
            $this->addItem($item);
        }
        return $this;
    }

    public function setPageSize($size)
    {
        $this->pageSize = (int)$size;
        LoggerFacade::debug('GitHubActionsCollection::setPageSize', ['size' => $size]);
        return $this;
    }

    public function setCurPage($page)
    {
        $this->curPage = (int)$page;
        LoggerFacade::debug('GitHubActionsCollection::setCurPage', ['curPage' => $page]);
        return $this;
    }

    public function getItems()
    {
        $items = parent::getItems();

        if ($this->pageSize !== null) {
            $offset = ($this->curPage - 1) * $this->pageSize;
            $items = array_slice($items, $offset, $this->pageSize);
        }

        return $items;
    }
}
