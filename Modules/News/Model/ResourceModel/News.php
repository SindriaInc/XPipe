<?php
namespace Sindria\News\Model\ResourceModel;

use Magento\Framework\Model\ResourceModel\Db\AbstractDb;

class News extends AbstractDb
{
    protected function _construct()
    {
        $this->_init('sindria_news', 'news_id');
    }
}
