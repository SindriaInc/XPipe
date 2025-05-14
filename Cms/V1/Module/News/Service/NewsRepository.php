<?php

namespace Cms\News\Service;

use Magento\Framework\Exception\NoSuchEntityException;
use Cms\News\Api\Data\NewsInterface;
use Cms\News\Api\NewsRepositoryInterface;
use Cms\News\Model\NewsFactory;

use Cms\News\Model\ResourceModel\News as NewsResource;

class NewsRepository implements NewsRepositoryInterface
{

    private NewsResource $resource;

    private NewsFactory $factory;
    public function __construct(NewsResource $resource, NewsFactory $factory)
    {
        $this->resource = $resource;
        $this->factory = $factory;
    }

    public function save(NewsInterface $news): void
    {
        $this->resource->save($news);
    }

    public function delete(NewsInterface $news): void
    {
        $this->resource->delete($news);
    }

    public function getNewsById(int $id): NewsInterface
    {
        $news = $this->factory->create();
        $this->resource->load($news, $id);
        if (!$news->getNewsId()) {
            throw new NoSuchEntityException(__('News with id "%1" does not exist.', $id));
        }
        return $news;
    }


}