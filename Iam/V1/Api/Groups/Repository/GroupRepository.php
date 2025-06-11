<?php

namespace Iam\Groups\Repository;

use Iam\Groups\Api\Data\GroupInterface;
use Iam\Groups\Api\Data\GroupRepositoryInterface;
use Iam\Groups\Model\GroupFactory;
use Iam\Groups\Model\ResourceModel\Group as GroupResource;
use Iam\Groups\Model\ResourceModel\Group\Collection;
use Iam\Groups\Model\ResourceModel\Group\CollectionFactory;

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

    public function all(): Collection
    {
        return $this->collectionFactory->create();
    }




}