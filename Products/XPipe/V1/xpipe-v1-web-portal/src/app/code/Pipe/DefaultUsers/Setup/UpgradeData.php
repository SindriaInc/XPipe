<?php

namespace Pipe\DefaultUsers\Setup;

use Magento\Framework\Setup\ModuleContextInterface;
use Magento\Framework\Setup\ModuleDataSetupInterface;
use Magento\Framework\Setup\UpgradeDataInterface;
use Magento\User\Model\UserFactory;
use Magento\Authorization\Model\RoleFactory;
use Magento\User\Model\ResourceModel\User as UserResource;

class UpgradeData implements UpgradeDataInterface
{
    protected $userFactory;
    protected $roleFactory;
    protected $userResource;

    public function __construct(
        UserFactory $userFactory,
        RoleFactory $roleFactory,
        UserResource $userResource
    ) {
        $this->userFactory = $userFactory;
        $this->roleFactory = $roleFactory;
        $this->userResource = $userResource;
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
                'role_name' => 'DemoRole'
            ],
            [
                'username' => 'pipe.user',
                'firstname' => 'Pipe',
                'lastname' => 'User',
                'email' => 'pipe.user@sindria.org',
                'password' => 'admin123',
                'role_name' => 'DemoRole'
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
            $role = $this->roleFactory->create()->load($data['role_name'], 'role_name');

            if (!$role->getId()) {
                throw new \Exception("Role '{$data['role_name']}' does not exist.");
            }

            $user = $this->userFactory->create()->loadByUsername($data['username']);

            if ($user->getId()) {
                echo 'User Already Exists';
                continue;
            }

            $user->setUsername($data['username'])
                ->setFirstname($data['firstname'])
                ->setLastname($data['lastname'])
                ->setEmail($data['email'])
                ->setPassword($data['password'])
                ->setInterfaceLocale('en_US')
                ->setIsActive(1)
                ->save();

            $user->setRoleId($role->getId())->save();
        }
    }
}
