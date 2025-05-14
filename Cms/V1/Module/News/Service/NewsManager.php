<?php

namespace Cms\News\Service;

use Cms\News\Api\Data\NewsInterface;
use Cms\News\Api\NewsManagerInterface;
use Cms\News\Model\ResourceModel\News\Collection;
use Cms\News\Model\ResourceModel\News\CollectionFactory;

class NewsManager implements NewsManagerInterface
{


    private CollectionFactory $collectionFactory;

    public function __construct(CollectionFactory $collectionFactory)
    {
        $this->collectionFactory = $collectionFactory;
    }

    public function getNews(): NewsInterface
    {
        /** @var NewsInterface $news */
        $news =  $this->getNewsCollection()
            ->addFieldToFilter('is_active', 1)
            ->addOrder('news_id')
            ->getFirstItem();

        return $news;
    }

    public function getNewsCollection(): Collection
    {
        return $this->collectionFactory->create();
    }
}