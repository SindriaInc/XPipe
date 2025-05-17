<?php
namespace Cms\Faq\Model\ResourceModel;

use Magento\Framework\Model\AbstractModel;
use Magento\Framework\Model\ResourceModel\Db\AbstractDb;

class Faq extends AbstractDb
{
    protected function _construct()
    {
        $this->_init('cms_faq', 'faq_id');
    }

    protected function _beforeSave(AbstractModel $object)
    {
        $object->setData('updated_at', date('Y-m-d H:i:s'));
        return parent::_beforeSave($object);
    }

    protected function _afterSave(AbstractModel $object)
    {
        $connection = $this->getConnection();
        $id = (int)$object->getId();


        $connection->delete($this->getTable('cms_faq_store'), ['faq_id = ?' => $id]);

        $storeIds = (array)$object->getStoreIds();
        foreach ($storeIds as $storeId) {
            $connection->insert(
                $this->getTable('cms_faq_store'),
                ['faq_id' => $id, 'store_id' => (int)$storeId]
            );
        }

        return parent::_afterSave($object);
    }

    protected function _afterLoad(AbstractModel $object)
    {
        $connection = $this->getConnection();
        $select = $connection->select()
            ->from($this->getTable('cms_faq_store'), 'store_id')
            ->where('faq_id = ?', $object->getId());

        $storeIds = $connection->fetchCol($select);

        $object->setData('store_id', $storeIds);

        return parent::_afterLoad($object);
    }
}
