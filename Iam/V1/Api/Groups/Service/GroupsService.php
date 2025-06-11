<?php
namespace Iam\Groups\Service;

use Iam\Groups\Repository\GroupRepository;
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


}
