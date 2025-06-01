<?php
namespace Pipelines\Configmap\Model\Form;

use Magento\Framework\Data\Collection as DataCollection;
use Magento\Framework\Data\Collection\EntityFactoryInterface;
use Core\Logger\Facade\LoggerFacade;
use Pipelines\Configmap\Model\Configmap;

class ConfigmapCollection extends DataCollection
{
    protected $pageSize = null;
    protected $curPage = 1;

    public function __construct(EntityFactoryInterface $entityFactory, Configmap $form)
    {
        parent::__construct($entityFactory);

        LoggerFacade::debug('ConfigmapCollection::__construct', ['form' => $form]);

        $this->addItem($form);

    }

    public function addOrder($field, $direction)
    {
        LoggerFacade::debug('ConfigmapCollection::addOrder', ['field' => $field, 'direction' => $direction]);
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
        LoggerFacade::debug('ConfigmapCollection::setPageSize', ['size' => $size]);
        return $this;
    }

    public function setCurPage($page)
    {
        $this->curPage = (int)$page;
        LoggerFacade::debug('ConfigmapCollection::setCurPage', ['curPage' => $page]);
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

    public function addFieldToFilter($field, $condition = null)
    {
        return $this;
    }
}
