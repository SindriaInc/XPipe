<?php

namespace Iam\Groups\Repository;

use Iam\Groups\Model\ResourceModel\Group\Collection;
use Magento\Framework\Exception\NoSuchEntityException;
use Iam\Groups\Api\Data\GroupInterface;
use Iam\Groups\Api\GroupRepositoryInterface;
use Iam\Groups\Model\GroupFactory;
use Iam\Groups\Model\ResourceModel\Group\CollectionFactory;

use Iam\Groups\Model\ResourceModel\Group as GroupResource;

class GroupRepository implements GroupRepositoryInterface
{

    private GroupResource $resource;

    private GroupFactory $factory;

    private CollectionFactory $collectionFactory;

    public function __construct(GroupResource $resource, GroupFactory $factory, CollectionFactory $collectionFactory)
    {
        $this->resource = $resource;
        $this->factory = $factory;
        $this->collectionFactory = $collectionFactory;
    }



    public function save(GroupInterface $group): void
    {
        // TODO: Implement save() method.
    }

    public function delete(GroupInterface $group): void
    {
        // TODO: Implement delete() method.
    }

    public function getGroupById(int $id): GroupInterface
    {
        // TODO: Implement getGroupById() method.
    }

    public function all(): array
    {

        return $this->collectionFactory->create()->getItems();
        // TODO: Implement all() method.
    }

//    public function save(NewsInterface $news): void
//    {
//        $this->resource->save($news);
//    }
//
//    public function delete(NewsInterface $news): void
//    {
//        $this->resource->delete($news);
//    }
//
//    public function getNewsById(int $id): NewsInterface
//    {
//        $news = $this->factory->create();
//        $this->resource->load($news, $id);
//        if (!$news->getNewsId()) {
//            throw new NoSuchEntityException(__('News with id "%1" does not exist.', $id));
//        }
//        return $news;
//    }



}