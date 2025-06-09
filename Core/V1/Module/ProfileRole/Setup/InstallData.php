<?php

namespace Core\ProfileRole\Setup;

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
    private \Magento\Authorization\Model\ResourceModel\Role\CollectionFactory $roleCollectionFactory;

    public function __construct(
        \Magento\Authorization\Model\RoleFactory $roleFactory,
        \Magento\Authorization\Model\RulesFactory $rulesFactory,
        \Magento\Authorization\Model\ResourceModel\Role\CollectionFactory $roleCollectionFactory
    ) {
        $this->roleFactory = $roleFactory;
        $this->rulesFactory = $rulesFactory;
        $this->roleCollectionFactory = $roleCollectionFactory;
    }

    public function install(ModuleDataSetupInterface $setup, ModuleContextInterface $context)
    {
        $demoRole = [
            'name' => 'ProfileRole',
            'resources' => [
                'Magento_Backend::myaccount',
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

        // Cerca se esiste già il ruolo
        $existingRole = $this->roleCollectionFactory->create()
            ->addFieldToFilter('role_name', $demoRole['name'])
            ->getFirstItem();

        if ($existingRole && $existingRole->getId()) {
            // Ruolo esistente: aggiorna le risorse
            $this->rulesFactory->create()
                ->setRoleId($existingRole->getId())
                ->setResources($demoRole['resources'])
                ->saveRel();
        } else {
            // Crea nuovo ruolo
            $role = $this->roleFactory->create();
            $role->setName($demoRole['name'])
                ->setPid(0)
                ->setRoleType(RoleGroup::ROLE_TYPE)
                ->setUserType(UserContextInterface::USER_TYPE_ADMIN);
            $role->save();

            $this->rulesFactory->create()
                ->setRoleId($role->getId())
                ->setResources($demoRole['resources'])
                ->saveRel();
        }
    }

}
