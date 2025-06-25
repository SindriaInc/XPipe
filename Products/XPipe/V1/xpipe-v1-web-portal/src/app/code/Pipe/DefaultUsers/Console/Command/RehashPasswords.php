<?php

namespace Pipe\DefaultUsers\Console\Command;

use Magento\Framework\Encryption\EncryptorInterface;
use Magento\User\Model\UserFactory;
use Magento\User\Model\ResourceModel\User as UserResource;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputArgument;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;

class RehashPasswords extends Command
{
    const ARG_USERNAME = 'username';
    const ARG_PASSWORD = 'password';

    protected $userFactory;
    protected $userResource;
    protected $encryptor;

    public function __construct(
        UserFactory $userFactory,
        UserResource $userResource,
        EncryptorInterface $encryptor
    ) {
        $this->userFactory = $userFactory;
        $this->userResource = $userResource;
        $this->encryptor = $encryptor;
        parent::__construct();
    }

    protected function configure()
    {
        $this->setName('pipe:users:rehash-passwords')
            ->setDescription('Rigenera gli hash delle password degli utenti di default o uno specifico')
            ->addArgument(self::ARG_USERNAME, InputArgument::OPTIONAL, 'Username da aggiornare (opzionale)')
            ->addArgument(self::ARG_PASSWORD, InputArgument::OPTIONAL, 'Nuova password (opzionale)');
    }

    protected function execute(InputInterface $input, OutputInterface $output)
    {
        $defaultUsers = ['demo.user', 'beta.user', 'dev.user', 'profile.user', 'customer.user'];
        $defaultPassword = 'admin123';

        $username = $input->getArgument(self::ARG_USERNAME);
        $password = $input->getArgument(self::ARG_PASSWORD) ?? $defaultPassword;

        $targetUsers = $username ? [$username] : $defaultUsers;

        foreach ($targetUsers as $userCode) {
            $user = $this->userFactory->create()->loadByUsername($userCode);

            if (!$user->getId()) {
                $output->writeln("<error>Utente '$userCode' non trovato.</error>");
                continue;
            }

            // It doesn't work but still help
            //$hashed = $this->encryptor->getHash($password, true);
            //$user->setPassword($hashed);

            $user->setPassword($password);


            $this->userResource->save($user);

            $output->writeln("<info>Password aggiornata per $userCode</info>");
        }

        return 0; // <- SUCCESS per Symfony 4.x / Magento 2.4.3
    }
}
