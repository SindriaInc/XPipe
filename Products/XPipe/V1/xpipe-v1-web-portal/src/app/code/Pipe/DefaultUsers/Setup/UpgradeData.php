<?php

namespace Pipe\DefaultUsers\Setup;

use Magento\Framework\Setup\ModuleContextInterface;
use Magento\Framework\Setup\ModuleDataSetupInterface;
use Magento\Framework\Setup\UpgradeDataInterface;
use Magento\User\Model\UserFactory;
use Magento\User\Model\ResourceModel\User as UserResource;
use Magento\Authorization\Model\Role;

class UpgradeData implements UpgradeDataInterface
{
    protected $userFactory;
    protected $userResource;
    protected $roleModel;

    public function __construct(
        UserFactory $userFactory,
        UserResource $userResource,
        Role $roleModel
    ) {
        $this->userFactory = $userFactory;
        $this->userResource = $userResource;
        $this->roleModel = $roleModel;
    }

    public function upgrade(ModuleDataSetupInterface $setup, ModuleContextInterface $context)
    {
        $now = (new \DateTime())->format('Y-m-d H:i:s');

        $users = [
            [
                'username' => 'demo.user',
                'firstname' => 'Demo',
                'lastname' => 'User',
                'email' => 'demo.user@sindria.org',
                'password' => 'Admin1234!',
                'role_name' => 'DemoRole'
            ],
            [
                'username' => 'beta.user',
                'firstname' => 'Beta',
                'lastname' => 'User',
                'email' => 'beta.user@sindria.org',
                'password' => 'Admin1234!',
                'role_name' => 'DemoRole'
            ],
            [
                'username' => 'developer.user',
                'firstname' => 'Developer',
                'lastname' => 'User',
                'email' => 'developer.user@sindria.org',
                'password' => 'Admin1234!',
                'role_name' => 'DemoRole'
            ],
            [
                'username' => 'profile.user',
                'firstname' => 'Profile',
                'lastname' => 'User',
                'email' => 'profile.user@sindria.org',
                'password' => 'Admin1234!',
                'role_name' => 'ProfileRole'
            ]
        ];

        foreach ($users as $data) {
            // Recupera il ruolo custom by name
            $role = $this->roleModel->load($data['role_name'], 'role_name');
            if (!$role->getId()) {
                throw new \Exception("Role '{$data['role_name']}' does not exist.");
            }

            // Check se user esiste già
            $user = $this->userFactory->create()->loadByUsername($data['username']);
            if ($user->getId()) {
                echo "User '{$data['username']}' already exists. Skipping.\n";
                continue;
            }

            $user->setUsername($data['username'])
                ->setFirstname($data['firstname'])
                ->setLastname($data['lastname'])
                ->setEmail($data['email'])
                ->setPassword($data['password']) // In chiaro!
                ->setInterfaceLocale('en_US')
                ->setIsActive(1)
                ->setData('created', $now)
                ->setData('extra', null)
                ->setData('secret', null)
                ->setRpToken(null)
                ->setRpTokenCreatedAt(null)
                ->setData('reload_acl_flag', 0); // Questo sblocca davvero il login!

            $this->userResource->save($user);

            // Assegna ruolo custom e salva ancora
            $user->setRoleId($role->getId());
            $this->userResource->save($user);

            echo "User '{$data['username']}' created successfully with role '{$data['role_name']}'.\n";
        }
    }
}
