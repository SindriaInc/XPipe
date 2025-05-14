<?php
namespace Core\SampleApi\Model\ResourceModel\External;

use Magento\Framework\Data\Collection as DataCollection;
use Magento\Framework\Api\Search\SearchResultInterface;

class Collection extends DataCollection implements SearchResultInterface
{
    protected $aggregations;

    public function __construct(
        \Magento\Framework\Data\Collection\EntityFactoryInterface $entityFactory,
        \Psr\Log\LoggerInterface $logger,
        \Magento\Framework\Data\Collection\Db\FetchStrategyInterface $fetchStrategy,
        \Magento\Framework\Event\ManagerInterface $eventManager
    ) {
        parent::__construct($entityFactory);
    }

    public function getAggregations()
    {
        return $this->aggregations;
    }

    public function setAggregations($aggregations)
    {
        $this->aggregations = $aggregations;
    }

    public function getSearchCriteria()
    {
        return null;
    }

    public function setSearchCriteria(\Magento\Framework\Api\SearchCriteriaInterface $searchCriteria = null)
    {
        return $this;
    }

    public function getTotalCount()
    {
        return $this->getSize();
    }

    public function setTotalCount($totalCount)
    {
        return $this;
    }

    public function setItems(array $items = null)
    {
        foreach ($items as $item) {
            $this->addItem($item);
        }
        return $this;
    }


    public function addOrder($field, $direction = self::SORT_ORDER_DESC)
    {
        return $this;
    }

    public function setPageSize($size)
    {
        $this->_pageSize = $size;
        return $this;
    }

    public function setCurPage($page)
    {
        $this->_curPage = $page;
        return $this;
    }

    public function addFieldToFilter($field, $condition = null): Collection
    {
       return $this;
    }
}
