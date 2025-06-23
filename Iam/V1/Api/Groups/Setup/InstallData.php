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
use Iam\Groups\Api\Data\GroupRepositoryInterface;
use Iam\Groups\Api\Data\UserGroupRepositoryInterface;

/**
 * @codeCoverageIgnore
 */
class InstallData implements InstallDataInterface
{
    private UserGroupFactory $userGroupFactory;
    private GroupFactory $groupFactory;
    private GroupCollectionFactory $groupCollectionFactory;
    private UserGroupCollectionFactory $userGroupCollectionFactory;
    private GroupRepositoryInterface $groupRepository;
    private UserGroupRepositoryInterface $userGroupRepository;
    private LoggerInterface $logger;

    public function __construct(
        UserGroupFactory $userGroupFactory,
        GroupFactory $groupFactory,
        GroupCollectionFactory $groupCollectionFactory,
        UserGroupCollectionFactory $userGroupCollectionFactory,
        GroupRepositoryInterface $groupRepository,
        UserGroupRepositoryInterface $userGroupRepository
    ) {
        $this->userGroupFactory = $userGroupFactory;
        $this->groupFactory = $groupFactory;
        $this->groupCollectionFactory = $groupCollectionFactory;
        $this->userGroupCollectionFactory = $userGroupCollectionFactory;
        $this->groupRepository = $groupRepository;
        $this->userGroupRepository = $userGroupRepository;
        $this->logger = ObjectManager::getInstance()->get(LoggerInterface::class);
    }

    /**
     * @throws \Exception
     */
    public function install(ModuleDataSetupInterface $setup, ModuleContextInterface $context)
    {
        $setup->startSetup();

        try {
            $connection = $setup->getConnection();

            $groupTable = $setup->getTable('iam_group');
            $userGroupTable = $setup->getTable('iam_user_group');

            $groupCount = $this->groupCollectionFactory->create()->getSize();
            $userGroupCount = $this->userGroupCollectionFactory->create()->getSize();

            $groupStatus = $connection->fetchRow("SHOW TABLE STATUS LIKE '{$groupTable}'");
            $userGroupStatus = $connection->fetchRow("SHOW TABLE STATUS LIKE '{$userGroupTable}'");

            $groupAutoIncrement = isset($groupStatus['Auto_increment']) ? (int)$groupStatus['Auto_increment'] : 0;
            $userGroupAutoIncrement = isset($userGroupStatus['Auto_increment']) ? (int)$userGroupStatus['Auto_increment'] : 0;

            $isGroupAutoIncrementValid = $groupAutoIncrement <= 1;
            $isUserGroupAutoIncrementValid = $userGroupAutoIncrement <= 1;

            $isGroupTableClean = $groupCount === 0 && $isGroupAutoIncrementValid;
            $isUserGroupTableClean = $userGroupCount === 0 && $isUserGroupAutoIncrementValid;

            if (!$isGroupTableClean || !$isUserGroupTableClean) {
                $this->logger->critical('Install aborted: Tables must be empty and AUTO_INCREMENT must be reset to 1.', [
                    'group_count' => $groupCount,
                    'user_group_count' => $userGroupCount,
                    'group_auto_increment' => $groupAutoIncrement,
                    'user_group_auto_increment' => $userGroupAutoIncrement,
                ]);
                throw new \Exception('InstallData requires empty and truncated tables with AUTO_INCREMENT reset to 1.');
            }

            $defaultGroups = [
                [
                    //'group_id' => 1,
                    'slug' => 'xpipe-system',
                    'label' => 'XPipe System',
                    'short' => 'XPS',
                ]
            ];

            $defaultUserGroups = [
                [
                    'user_group_id' => 1,
                    'username' => 'carbon.user',
                    'group_id' => 1,
                ]
            ];

            // Create default groups using repository
            foreach ($defaultGroups as $group) {
                try {
                    $this->groupRepository->save($group);
                    $this->logger->info('Default Group created successfully.', ['defaultGroup' => $group]);
                } catch (\Exception $e) {
                    $this->logger->error('Failed to create default group', [
                        'defaultGroup' => $group,
                        'error' => $e->getMessage()
                    ]);
                    throw $e;
                }
            }

            // Attach default user groups using repository
            foreach ($defaultUserGroups as $userGroup) {
                try {
                    $existing = $this->userGroupRepository
                        ->getUserGroupByUsernameAndGroupId($userGroup['username'], $userGroup['group_id']);
                    if ($existing && $existing->getId()) {
                        $this->logger->info('User group mapping already exists', ['userGroup' => $userGroup]);
                        continue;
                    }

                    $this->userGroupRepository->attach(
                        $userGroup['username'],
                        $userGroup['group_id']
                    );
                    $this->logger->info('User group mapping created.', ['userGroup' => $userGroup]);
                } catch (\Exception $e) {
                    $this->logger->error('Failed to attach user group', [
                        'userGroup' => $userGroup,
                        'error' => $e->getMessage()
                    ]);
                    throw $e;
                }
            }
        } catch (\Exception $e) {
            $setup->endSetup(); // always close setup on error
            throw $e;
        }

        $setup->endSetup();
    }
}
