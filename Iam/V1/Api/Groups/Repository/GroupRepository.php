<?php

namespace Iam\Groups\Repository;

use Iam\Groups\Api\Data\GroupInterface;
use Iam\Groups\Api\Data\GroupRepositoryInterface;
use Iam\Groups\Model\GroupFactory;
use Iam\Groups\Model\ResourceModel\Group as GroupResource;
use Iam\Groups\Model\ResourceModel\Group\Collection;
use Iam\Groups\Model\ResourceModel\Group\CollectionFactory;
use Magento\Framework\Exception\AlreadyExistsException;
use Magento\Framework\Exception\NoSuchEntityException;

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


    /**
     * @throws AlreadyExistsException
     */
    public function save(array $payload): GroupInterface
    {
        $model = $this->factory->create();
        $model->setData($payload);
        $this->resource->save($model);
        return $model;
    }
    public function update(array $existingData, array $payload): GroupInterface
    {
        $model = $this->factory->create();
        $this->resource->load($model, $existingData['slug'], 'slug');

        $model->setData('label',$payload['label']);
        $model->setData('short',$payload['short']);

        $this->resource->save($model);
        return $model;

    }


    /**
     * @throws \Exception
     */
    public function delete(string $slug): GroupInterface
    {
        $model = $this->factory->create();
        $this->resource->load($model, $slug, 'slug');

        $this->resource->delete($model);
        return $model;
    }

    public function getGroupById(int $id): GroupInterface
    {
        $group = $this->factory->create();
        $this->resource->load($group, $id);
        if (!$group->getGroupId()) {
            throw new NoSuchEntityException(__('Group with id "%1" does not exist.', $id));
        }
        return $group;
    }

    public function find(string $slug): GroupInterface
    {
        $group = $this->factory->create();
        $this->resource->load($group, $slug, 'slug');

        if (!$group->getSlug()) {
            throw new NoSuchEntityException(__('Group with slug "%1" does not exist.', $slug));
        }
        return $group;
    }

    public function all(): Collection
    {
        return $this->collectionFactory->create();
    }




}