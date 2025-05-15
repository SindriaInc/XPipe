<?php
namespace Sindria\Faq\Model\ResourceModel\Faq;

use Magento\Framework\Model\ResourceModel\Db\Collection\AbstractCollection;
use Sindria\Faq\Model\Faq as FaqModel;
use Sindria\Faq\Model\ResourceModel\Faq as FaqResource;

class Collection extends AbstractCollection
{
    protected function _construct()
    {
        $this->_init(FaqModel::class, FaqResource::class);
    }


    protected function _afterLoad()
    {
        parent::_afterLoad();

        foreach ($this->getItems() as $item) {
            /** @var \Sindria\Faq\Model\Faq $item */
            $connection = $this->getConnection();
            $select = $connection->select()
                ->from($this->getTable('sindria_faq_store'), 'store_id')
                ->where('faq_id = ?', $item->getId());

            $storeIds = $connection->fetchCol($select);
            $item->setData('store_ids', $storeIds);
            // Per visualizzazione
        }

        return $this;
    }

    public function addFieldToFilter($field, $condition = null)
    {

        if ($field == 'store_ids') {
            $this->getSelect()->join(
                ['store' => $this->getTable('sindria_faq_store')],
                'main_table.faq_id = store.faq_id',
                []
            );

            return parent::addFieldToFilter('store.store_id', $condition);
        }

        return parent::addFieldToFilter($field, $condition);
    }

}
