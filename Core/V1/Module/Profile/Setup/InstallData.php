<?php

namespace Core\Profile\Setup;

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


            $profileRole = [
                'name' => 'ProfileRole',
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
                ]
            ];


            $role = $this->roleFactory->create();
            $role->setName($profileRole['name'])
                ->setPid(0)
                ->setRoleType(RoleGroup::ROLE_TYPE)
                ->setUserType(UserContextInterface::USER_TYPE_ADMIN);
            $role->save();

            $this->rulesFactory->create()
                ->setRoleId($role->getId())
                ->setResources($profileRole['resources'])
                ->saveRel();

    }
}