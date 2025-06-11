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

    public function getGroups($params)
    {

        if (count($params) === 0) {
            return $this->groupRepository->all()->getData();
        } elseif ($params['q'] && !empty($params['q']) !== null) {

        }

//        dd($params);
//        switch ($params) {
//            case count($params) === 0:
//                return $this->groupRepository->all()->getData();
//                break;
//            case $params['q'] && !empty($params['q']) !== null:
//                dd('Ricerca');
//
//            case $params['off'] && !empty($params['off']) !== null && $params['sze'] && !empty($params['sze']) !== null :
//                dd('PAGINAZIONE');
//                break;
//        }

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


    /**
     * @throws \Exception
     * @throws NoSuchEntityException
     */
    public function deleteGroup(string $slug): array
    {
        $this->findGroupBySlug($slug);
        return $this->groupRepository->delete($slug)->getData();
    }


}
