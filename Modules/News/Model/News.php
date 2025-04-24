<?php

namespace Sindria\News\Model;

use Magento\Framework\Api\CustomAttributesDataInterface;
use Magento\Framework\Model\AbstractModel;

class News extends AbstractModel
{
    protected function _construct()
    {
        $this->_init(\Sindria\News\Model\ResourceModel\News::class);
    }
}
