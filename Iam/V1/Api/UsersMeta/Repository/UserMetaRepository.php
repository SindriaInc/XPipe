<?php

namespace Iam\UsersMeta\Repository;

use Iam\UsersMeta\Api\Data\UserMetaInterface;
use Iam\UsersMeta\Api\Data\UserMetaRepositoryInterface;
use Iam\UsersMeta\Model\UserMetaFactory;
use Iam\UsersMeta\Model\ResourceModel\UserMeta as UserMetaResource;
use Iam\UsersMeta\Model\ResourceModel\UserMeta\Collection;
use Iam\UsersMeta\Model\ResourceModel\UserMeta\CollectionFactory;
use Magento\Framework\Exception\AlreadyExistsException;
use Magento\Framework\Exception\NoSuchEntityException;

class UserMetaRepository implements UserMetaRepositoryInterface
{

    private UserMetaResource $resource;

    private UserMetaFactory $factory;

    private CollectionFactory $collectionFactory;

    public function __construct(UserMetaResource $resource, UserMetaFactory $factory, CollectionFactory $collectionFactory)
    {
        $this->resource = $resource;
        $this->factory = $factory;
        $this->collectionFactory = $collectionFactory;
    }


    /**
     * @throws AlreadyExistsException
     */
    public function save(array $payload): UserMetaInterface
    {
        $model = $this->factory->create();
        $model->setData($payload);
        $this->resource->save($model);
        return $model;
    }
    public function update(array $existingData, array $payload): UserMetaInterface
    {
        $model = $this->factory->create();
        $this->resource->load($model, $existingData['username'], 'username');

        $model->setData('job_title',$payload['jobTitle']);
        $model->setData('seniority',$payload['seniority']);
        $model->setData('location',$payload['location']);
        $model->setData('work_mode',$payload['workMode']);

        $this->resource->save($model);
        return $model;

    }


    /**
     * @throws \Exception
     */
    public function delete(string $username): UserMetaInterface
    {
        $model = $this->factory->create();
        $this->resource->load($model, $username, 'username');

        $this->resource->delete($model);
        return $model;
    }

    public function getUserMetaByUsername(string $username): UserMetaInterface
    {
        $userMeta = $this->factory->create();
        $this->resource->load($userMeta, $username);
        if (!$userMeta->getGroupId()) {
            throw new NoSuchEntityException(__('UserMeta for username "%1" does not exist.', $username));
        }
        return $userMeta;
    }

    public function find(string $username): UserMetaInterface
    {
        $userMeta = $this->factory->create();
        $this->resource->load($userMeta, $username, 'username');

        if (!$userMeta->getUsername()) {
            throw new NoSuchEntityException(__('UserMeta for username "%1" does not exist.', $username));
        }
        return $userMeta;
    }

    public function all(): Collection
    {
        return $this->collectionFactory->create();
    }

}