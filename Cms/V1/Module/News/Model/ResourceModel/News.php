<?php
namespace Cms\News\Model\ResourceModel;

use Magento\Framework\Model\AbstractModel;
use Magento\Framework\Model\ResourceModel\Db\AbstractDb;

class News extends AbstractDb
{
    protected function _construct()
    {
        $this->_init('cms_news', 'news_id');
    }

    protected function _beforeSave(AbstractModel $object)
    {
        $object->setData('updated_at', date('Y-m-d H:i:s'));
        return parent::_beforeSave($object);
    }
}
