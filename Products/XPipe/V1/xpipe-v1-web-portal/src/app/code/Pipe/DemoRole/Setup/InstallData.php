<?php

namespace Pipe\DemoRole\Setup;

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
            'name' => 'DemoRole',
            'resources' => [
                //'Magento_Backend::myaccount', // Disabled in order to prevent password change from user
                'Magento_Backend::dashboard',
                'Magento_Backend::system_other_settings',
                'Magento_AdminNotification::adminnotification',
                'Magento_AdminNotification::show_toolbar',
                'Magento_AdminNotification::show_list',
                'Magento_AdminNotification::mark_as_read',
                'Magento_AdminNotification::adminnotification_remove',
                'Magento_Backend::global_search',
                'Magento_Backend::system',
                'Lab_Terminal::lab',
                'Lab_Terminal::shell',
                'Lab_Terminal::catalog',
                'Lab_Terminal::select',
                'Lab_Terminal::run',
                'Pipelines_PipeManager::pipelines',
                'Pipelines_PipeManager::pipemanager',
                'Pipelines_PipeManager::listpipelines',
                'Pipelines_PipeManager::searchpipelines',
                'Pipelines_PipeManager::listruns',
                'Pipelines_PipeManager::searchruns',
                'Pipelines_PipeManager::showlogs',
                'Pipelines_PipeManager::newrun',
                'Pipelines_PipeManager::stoprun',
                'Pipelines_PipeManager::deleterun',
                'Pipelines_Configmap::configmap',
                'Pipelines_Configmap::list',
                'Pipelines_Configmap::save',
                'Pipelines_Configmap::delete',
                'Pipelines_TemplateStore::templatestore',
                'Pipelines_TemplateStore::catalog',
                'Pipelines_TemplateStore::selectincluded',
                'Pipelines_TemplateStore::goincluded'

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
