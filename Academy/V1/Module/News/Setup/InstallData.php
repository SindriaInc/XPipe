<?php

namespace Academy\News\Setup;

use Magento\Framework\Setup\InstallDataInterface;
use Magento\Framework\Setup\ModuleContextInterface;
use Magento\Framework\Setup\ModuleDataSetupInterface;

/* For get RoleType and UserType for create Role   */;
use Magento\Authorization\Model\Acl\Role\Group as RoleGroup;
use Magento\Authorization\Model\UserContextInterface;

/**
 * @codeCoverageIgnore
 */
class InstallData implements InstallDataInterface
{
    /**
     * RoleFactory
     *
     * @var roleFactory
     */
    private $roleFactory;

    /**
     * RulesFactory
     *
     * @var rulesFactory
     */
    private $rulesFactory;
    /**
     * Init
     *
     * @param \Magento\Authorization\Model\RoleFactory $roleFactory
     * @param \Magento\Authorization\Model\RulesFactory $rulesFactory
     */
    public function __construct(
        \Magento\Authorization\Model\RoleFactory $roleFactory, /* Instance of Role*/
        \Magento\Authorization\Model\RulesFactory $rulesFactory /* Instance of Rule */
        /*this define that which resource permitted to wich role */
    )
    {
        $this->roleFactory = $roleFactory;
        $this->rulesFactory = $rulesFactory;
    }

    /**
     * {@inheritdoc}
     * @SuppressWarnings(PHPMD.ExcessiveMethodLength)
     */
    public function install(ModuleDataSetupInterface $setup, ModuleContextInterface $context)
    {

        $defaultAcademyNewsRoles = [
            [
                'name' => 'NewsReadOnlyRole',
                'resources' => [
                    'Magento_Backend::dashboard',
                    'Magento_Backend::system_other_settings',
                    'Magento_AdminNotification::adminnotification',
                    'Magento_AdminNotification::show_toolbar',
                    'Magento_AdminNotification::show_list',
                    'Magento_AdminNotification::mark_as_read',
                    'Magento_AdminNotification::adminnotification_remove',
                    'Magento_Backend::global_search',
                    'Magento_Backend::system',
                    'Academy_News::news',
                    'Academy_News::show',

                ]
            ],
            [
                'name' => 'NewsWriteRole',
                'resources' => [
                    'Magento_Backend::dashboard',
                    'Magento_Backend::system_other_settings',
                    'Magento_AdminNotification::adminnotification',
                    'Magento_AdminNotification::show_toolbar',
                    'Magento_AdminNotification::show_list',
                    'Magento_AdminNotification::mark_as_read',
                    'Magento_AdminNotification::adminnotification_remove',
                    'Magento_Backend::global_search',
                    'Magento_Backend::system',
                    'Academy_News::news',
                    'Academy_News::show',
                    'Academy_News::add',
                    'Academy_News::edit',
                ]
            ],
            [
                'name' => 'NewsAdminRole',
                'resources' => [
                    'Magento_Backend::dashboard',
                    'Magento_Backend::system_other_settings',
                    'Magento_AdminNotification::adminnotification',
                    'Magento_AdminNotification::show_toolbar',
                    'Magento_AdminNotification::show_list',
                    'Magento_AdminNotification::mark_as_read',
                    'Magento_AdminNotification::adminnotification_remove',
                    'Magento_Backend::global_search',
                    'Magento_Backend::system',
                    'Academy_News::news',
                    'Academy_News::show',
                    'Academy_News::add',
                    'Academy_News::edit',
                    'Academy_News::delete',

                ]
            ]
        ];


        foreach ($defaultAcademyNewsRoles as $data) {

            $role = $this->roleFactory->create();
            $role->setName($data['name'])
                ->setPid(0)
                ->setRoleType(RoleGroup::ROLE_TYPE)
                ->setUserType(UserContextInterface::USER_TYPE_ADMIN);
            $role->save();

            $this->rulesFactory->create()
                ->setRoleId($role->getId())
                ->setResources($data['resources'])
                ->saveRel();
        }
    }
}