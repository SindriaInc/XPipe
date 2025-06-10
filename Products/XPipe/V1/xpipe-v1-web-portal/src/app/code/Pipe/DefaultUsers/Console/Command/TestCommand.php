<?php

namespace Pipe\DefaultUsers\Console\Command;

use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;

class TestCommand extends Command
{
    protected function configure()
    {
        $this->setName('pipe:test')
            ->setDescription('Command di test per verifica registrazione');
    }

    protected function execute(InputInterface $input, OutputInterface $output)
    {
        $output->writeln('<info>Test CLI OK!</info>');
        return Command::SUCCESS;
    }
}
