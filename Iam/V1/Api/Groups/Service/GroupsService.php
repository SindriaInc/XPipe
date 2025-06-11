<?php
namespace Iam\Groups\Service;


use Iam\Groups\Model\Group;
use Iam\Groups\Repository\GroupRepository;
use Magento\Framework\Exception\AlreadyExistsException;
use Magento\Framework\Exception\NoSuchEntityException;

class GroupsService
{

    private GroupRepository $groupRepository;

    public function __construct(GroupRepository $groupRepository)
    {
        $this->groupRepository = $groupRepository;
    }

    public function getAllGroups()
    {
        return $this->groupRepository->all()->getData();
    }

    /**
     * @throws NoSuchEntityException
     */
    public function findGroupBySlug(string $slug)
    {
        return $this->groupRepository->find($slug)->getData();
    }

    /**
     * @throws AlreadyExistsException
     */
    public function createGroup(array $payload): array
    {
        return $this->groupRepository->save($payload)->getData();
    }


    /**
     * @throws \Iam\Groups\Exception\GroupSlugException|NoSuchEntityException
     *
     */
    public function editGroup(array $payload): array
    {
        $groupData = $this->findGroupBySlug($payload['slug']);

        if ($payload['slug'] !== $groupData['slug']) {
            throw new \Iam\Groups\Exception\GroupSlugException('Group slug cannot be changed');
        }

        return $this->groupRepository->update($groupData, $payload)->getData();
    }


}
