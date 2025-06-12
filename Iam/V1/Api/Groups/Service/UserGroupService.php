<?php
namespace Iam\Groups\Service;

use Iam\Groups\Helper\GroupHelper;
use Iam\Groups\Repository\UserGroupRepository;
use Magento\Framework\Exception\AlreadyExistsException;
use Magento\Framework\Exception\NoSuchEntityException;

class UserGroupService
{

    private UserGroupRepository $userGroupRepository;

    public function __construct(UserGroupRepository $userGroupRepository)
    {
        $this->userGroupRepository = $userGroupRepository;
    }

//    public function getGroups($params)
//    {
//        $function = GroupHelper::selectFunction($params);
//
//        switch ($function) {
//            case 0:
//                return $this->userGroupRepository->all()->getData();
//            case 1:
//                return ['result' => 'ricerca'];
//            case 2:
//                return ['result' => 'paginazione'];
//            default:
//                return ['result' => 'parametri non supportati'];
//        }
//
//
//    }
//
//    /**
//     * @throws NoSuchEntityException
//     */
//    public function findGroupBySlug(string $slug)
//    {
//        return $this->userGroupRepository->find($slug)->getData();
//    }
//
//    /**
//     * @throws AlreadyExistsException
//     */
//    public function createGroup(array $payload): array
//    {
//        return $this->userGroupRepository->save($payload)->getData();
//    }
//
//
//    /**
//     * @throws \Iam\Groups\Exception\GroupSlugException|NoSuchEntityException
//     *
//     */
//    public function editGroup(array $payload): array
//    {
//        $groupData = $this->findGroupBySlug($payload['slug']);
//
//        // Non serve ma e' bella
//        if ($payload['slug'] !== $groupData['slug']) {
//            throw new \Iam\Groups\Exception\GroupSlugException('Group slug cannot be changed');
//        }
//
//        return $this->userGroupRepository->update($groupData, $payload)->getData();
//    }
//
//
//    /**
//     * @throws \Exception
//     * @throws NoSuchEntityException
//     */
//    public function deleteGroup(string $slug): array
//    {
//        $this->findGroupBySlug($slug);
//        return $this->userGroupRepository->delete($slug)->getData();
//    }


}
