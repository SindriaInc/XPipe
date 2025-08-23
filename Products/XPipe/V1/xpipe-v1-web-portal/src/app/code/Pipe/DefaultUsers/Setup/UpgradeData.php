<?php

namespace Pipe\DefaultUsers\Setup;

use Magento\Framework\Setup\ModuleContextInterface;
use Magento\Framework\Setup\ModuleDataSetupInterface;
use Magento\Framework\Setup\UpgradeDataInterface;
use Magento\User\Model\UserFactory;
use Magento\User\Model\ResourceModel\User as UserResource;
use Magento\Authorization\Model\Role;
use Magento\Framework\Encryption\EncryptorInterface;

class UpgradeData implements UpgradeDataInterface
{
    protected $userFactory;
    protected $userResource;
    protected $roleModel;
    protected $encryptor;

    public function __construct(
        UserFactory $userFactory,
        UserResource $userResource,
        Role $roleModel,
        EncryptorInterface $encryptor
    ) {
        $this->userFactory = $userFactory;
        $this->userResource = $userResource;
        $this->roleModel = $roleModel;
        $this->encryptor = $encryptor;
    }

    public function upgrade(ModuleDataSetupInterface $setup, ModuleContextInterface $context)
    {
        $users = [
            [
                'username' => 'demo.user',
                'firstname' => 'Demo',
                'lastname' => 'User',
                'email' => 'demo.user@sindria.org',
                'password' => 'admin123',
                'role_name' => 'DemoRole'
            ],
            [
                'username' => 'beta.user',
                'firstname' => 'Beta',
                'lastname' => 'User',
                'email' => 'beta.user@sindria.org',
                'password' => 'admin123',
                'role_name' => 'BetaRole'
            ],
            [
                'username' => 'dev.user',
                'firstname' => 'Dev',
                'lastname' => 'User',
                'email' => 'dev.user@sindria.org',
                'password' => 'admin123',
                'role_name' => 'DevRole'
            ],
            [
                'username' => 'pro.user',
                'firstname' => 'Pro',
                'lastname' => 'User',
                'email' => 'pro.user@sindria.org',
                'password' => 'admin123',
                'role_name' => 'ProRole'
            ],
            [
                'username' => 'enterprise.user',
                'firstname' => 'Enterprise',
                'lastname' => 'User',
                'email' => 'enterprise.user@sindria.org',
                'password' => 'admin123',
                'role_name' => 'EnterpriseRole'
            ],
            [
                'username' => 'owner.user',
                'firstname' => 'Owner',
                'lastname' => 'User',
                'email' => 'owner.user@sindria.org',
                'password' => 'admin123',
                'role_name' => 'OwnerRole'
            ],
            [
                'username' => 'profile.user',
                'firstname' => 'Profile',
                'lastname' => 'User',
                'email' => 'profile.user@sindria.org',
                'password' => 'admin123',
                'role_name' => 'ProfileRole'
            ]
        ];

        foreach ($users as $data) {
            // Carica il ruolo custom
            $role = $this->roleModel->load($data['role_name'], 'role_name');
            if (!$role->getId()) {
                throw new \Exception("Role '{$data['role_name']}' does not exist.");
            }

            // Controlla se l'utente esiste già
            $user = $this->userFactory->create()->loadByUsername($data['username']);
            if ($user->getId()) {
                echo "User '{$data['username']}' already exists. Skipping.\n";
                continue;
            }

            // Crea hash sicuro della password
            $hashedPassword = $this->encryptor->getHash($data['password'], true);

            $user->setUsername($data['username'])
                ->setFirstname($data['firstname'])
                ->setLastname($data['lastname'])
                ->setEmail($data['email'])
                ->setPassword($hashedPassword)
                ->setInterfaceLocale('en_US')
                ->setIsActive(1)
                ->setRpToken(null)
                ->setRpTokenCreatedAt(null)
                ->setData('reload_acl_flag', 1);

            $this->userResource->save($user);

            // Assegna il ruolo e salva
            $user->setRoleId($role->getId());
            $this->userResource->save($user);

            echo "User '{$data['username']}' created successfully with role '{$data['role_name']}'.\n";
        }
    }
}
