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
        UserGroupFactory $userGroupFactory,
        GroupFactory $groupFactory,
        GroupCollectionFactory $groupCollectionFactory,
        UserGroupCollectionFactory $userGroupCollectionFactory
    ) {
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
                    'group_id' => 1,
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

            foreach ($defaultGroups as $group) {

                $groupModel = $this->groupFactory->create();

                if (!$groupModel) {
                    throw new \Exception('Group model is null. Check factory or class instantiation.');
                }

                $groupModel->setData($group);
                $groupModel->save();

                $this->logger->info('Default Group created successfully.', ['defaultGroup' => $group]);
            }

            foreach ($defaultUserGroups as $userGroup) {
                $userGroupModel = $this->userGroupFactory->create();

                if (!$userGroupModel) {
                    throw new \Exception('UserGroup model is null. Check factory or class instantiation.');
                }

                $userGroupModel->setData('user_group_id', $userGroup['user_group_id']);
                $userGroupModel->setData('username', $userGroup['username']);
                $userGroupModel->setData('group_id', $userGroup['group_id']);

                $userGroupModel->save();

                $this->logger->info('User group mapping created.', ['userGroup' => $userGroup]);
            }

        } catch (\Exception $e) {
            $setup->endSetup(); // always end setup before rethrow
            throw $e;
        }

        $setup->endSetup();
    }
}
