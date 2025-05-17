<?php

namespace Cms\Faq\Model;

use Magento\Framework\Model\AbstractModel;
use Cms\Faq\Api\Data\FaqInterface;

class Faq extends AbstractModel implements FaqInterface
{

    private const FAQ_ID = 'faq_id';
    private const QUESTION = 'question';
    private const ANSWER = 'answer';
    private const CREATED_AT = 'created_at';
    private const UPDATED_AT = 'updated_at';
    private const STATUS = 'status';
    protected function _construct()
    {
        $this->_eventPrefix = 'cms_faq';
        $this->_eventObject = 'faq';
        $this->_idFieldName = self::FAQ_ID;
        $this->_init(\Cms\Faq\Model\ResourceModel\Faq::class);
    }

    public function getFaqId(): int
    {
        return (int) $this->getData(self::FAQ_ID);
    }

    public function setFaqId(int $id)
    {
        $this->setData(self::FAQ_ID, $id);
    }

    public function getQuestion(): string
    {
        return $this->getData(self::QUESTION);
    }

    public function setQuestion(string $question)
    {
        $this->setData(self::QUESTION, $question);
    }

    public function getAnswer(): string
    {
        return $this->getData(self::ANSWER);
    }

    public function setAnswer(string $answer)
    {
        $this->setData(self::ANSWER, $answer);
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

    public function getStatus(): int
    {
        return $this->getData(self::STATUS);
    }

    public function setStatus(int $status)
    {
        $this->setData(self::STATUS, $status);
    }

    public function getStoreIds()
    {
        return $this->getData('store_id');
    }

    public function setStoreIds(array $storeIds)
    {
        return $this->setData('store_id', $storeIds);
    }


}
