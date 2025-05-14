<?php
namespace Academy\News\Model\ResourceModel\News;

use Magento\Framework\Model\ResourceModel\Db\Collection\AbstractCollection;
use Academy\News\Model\News as NewsModel;
use Academy\News\Model\ResourceModel\News as NewsResource;

class Collection extends AbstractCollection
{
    protected function _construct()
    {
        $this->_init(NewsModel::class, NewsResource::class);
    }
}
