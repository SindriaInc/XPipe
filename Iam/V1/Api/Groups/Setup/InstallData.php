<?php

namespace Iam\Groups\Setup;

use Magento\Framework\App\ObjectManager;
use Magento\Framework\Setup\InstallDataInterface;
use Magento\Framework\Setup\ModuleContextInterface;
use Magento\Framework\Setup\ModuleDataSetupInterface;
use Psr\Log\LoggerInterface;
use Iam\Groups\Model\UserGroupFactory;
use Iam\Groups\Model\GroupFactory;
use Iam\Groups\Model\ResourceModel\Group\CollectionFactory as GroupCollectionFactory;
use Iam\Groups\Model\ResourceModel\UserGroup\CollectionFactory as UserGroupCollectionFactory;

/**
 * @codeCoverageIgnore
 */
class InstallData implements InstallDataInterface
{
    private UserGroupFactory $userGroupFactory;
    private GroupFactory $groupFactory;
    private GroupCollectionFactory $groupCollectionFactory;
    private UserGroupCollectionFactory $userGroupCollectionFactory;

    private LoggerInterface $logger;

    public function __construct(
        \Iam\Groups\Model\UserGroupFactory $userGroupFactory,
        \Iam\Groups\Model\GroupFactory     $groupFactory,
        GroupCollectionFactory             $groupCollectionFactory,
        UserGroupCollectionFactory         $userGroupCollectionFactory

    )
    {
        $this->userGroupFactory = $userGroupFactory;
        $this->groupFactory = $groupFactory;
        $this->groupCollectionFactory = $groupCollectionFactory;
        $this->userGroupCollectionFactory = $userGroupCollectionFactory;
        $this->logger = ObjectManager::getInstance()->get(LoggerInterface::class);
    }

    /**
     * @throws \Exception
     */
    public function install(ModuleDataSetupInterface $setup, ModuleContextInterface $context)
    {
        $defaultGroups[] = [
            'group_id' => 1,
            'slug' => 'xpipe-system',
            'label' => 'XPipe System',
            'short' => 'XPS',
        ];

        $defaultUserGroups[] = [
            'user_group_id' => 1,
            'username' => 'carbon.user',
            'group_id' => 1,
        ];

        $existingGroup = $this->groupCollectionFactory->create()
            ->addFieldToFilter('slug', 'xpipe-system')
            ->getFirstItem();

        $isDefaultGroupEmpty = empty($existingGroup->getData());


        if ($isDefaultGroupEmpty === true) {

            foreach ($defaultGroups as $group) {


                $this->groupFactory->create()
                    ->setGroupId($group['group_id'])
                    ->setSlug($group['slug'])
                    ->setLabel($group['label'])
                    ->setShort($group['short'])
                    ->save();

                $this->logger->info('Default Group created successfully.', [
                    'defaultGroup' => $group,
                ]);

            }
        } else {
            $this->logger->critical('Default Group xpipe-system already exists.');
            throw new \Exception('Default Group xpipe-system already exists.');
        }

        foreach ($defaultUserGroups as $userGroup) {
            $existingUserGroup = $this->userGroupCollectionFactory->create()
                ->addFieldToFilter('username', $userGroup['username'])
                ->addFieldToFilter('group_id', $userGroup['group_id'])
                ->getFirstItem();

            $isDefaultUserGroupEmpty = empty($existingUserGroup->getData());

            if ($isDefaultUserGroupEmpty === true) {

                $this->userGroupFactory->create()
                    ->setUserGroupId($userGroup['user_group_id'])
                    ->setUsername($userGroup['username'])
                    ->setGroupId($userGroup['group_id'])
                    ->save();

            } else {
                $this->logger->warning('User already attached to group', [
                    'defaultGroup' => $userGroup,
                ]);
            }
        }
    }
}
