<?php

namespace Core\News\Model;

use Magento\Framework\Model\AbstractModel;
use Sindria\News\Api\Data\NewsInterface;

class News extends AbstractModel implements NewsInterface
{

    private const NEWS_ID = 'news_id';
    private const TITLE = 'title';
    private const CONTENT = 'content';
    private const CREATED_AT = 'created_at';
    private const UPDATED_AT = 'updated_at';
    private const IS_ACTIVE = 'is_active';
    protected function _construct()
    {
        $this->_eventPrefix = 'Core_news';
        $this->_eventObject = 'news';
        $this->_idFieldName = self::NEWS_ID;
        $this->_init(\Sindria\News\Model\ResourceModel\News::class);
    }

    public function getNewsId(): int
    {
        return (int) $this->getData(self::NEWS_ID);
    }

    public function setNewsId(int $id)
    {
        $this->setData(self::NEWS_ID, $id);
    }

    public function getTitle(): string
    {
        return $this->getData(self::TITLE);
    }

    public function setTitle(string $title)
    {
        $this->setData(self::TITLE, $title);
    }

    public function getContent(): string
    {
        return $this->getData(self::CONTENT);
    }

    public function setContent(string $content)
    {
        $this->setData(self::CONTENT, $content);
    }

    public function getCreatedAt(): string
    {
        return $this->getData(self::CREATED_AT);
    }

    public function setCreatedAt(string $createdAt)
    {
        $this->setData(self::CREATED_AT, $createdAt);
    }

    public function getUpdatedAt(): string
    {
       return $this->getData(self::UPDATED_AT);
    }

    public function setUpdatedAt(string $updatedAt)
    {
        $this->setData(self::UPDATED_AT, $updatedAt);
    }

    public function isActive(): int
    {
        return $this->getData(self::IS_ACTIVE);
    }

    public function setIsActive(int $active)
    {
        $this->setData(self::IS_ACTIVE, $active);
    }


}
