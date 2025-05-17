<?php

namespace Cms\Faq\Service;

use Magento\Framework\Exception\AlreadyExistsException;
use Magento\Framework\Exception\NoSuchEntityException;
use Cms\Faq\Api\Data\FaqInterface;
use Cms\Faq\Api\FaqRepositoryInterface;
use Cms\Faq\Model\FaqFactory;

use Cms\Faq\Model\ResourceModel\Faq as FaqResource;

class FaqRepository implements FaqRepositoryInterface
{

    private FaqResource $resource;

    private FaqFactory $factory;
    public function __construct(FaqResource $resource, FaqFactory $factory)
    {
        $this->resource = $resource;
        $this->factory = $factory;
    }

    /**
     * @throws AlreadyExistsException
     */
    public function save(FaqInterface $faq): void
    {
        $this->resource->save($faq);
    }

    public function delete(FaqInterface $faq): void
    {
        $this->resource->delete($faq);
    }

    public function getFaqById(int $id): FaqInterface
    {
        $faq = $this->factory->create();
        $this->resource->load($faq, $id);
        if (!$faq->getFaqId()) {
            throw new NoSuchEntityException(__('Faq with id "%1" does not exist.', $id));
        }
        return $faq;
    }


}