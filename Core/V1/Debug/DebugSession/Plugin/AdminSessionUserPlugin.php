<?php
namespace Pipe\DebugSession\Plugin;

use Magento\User\Model\User;
use Psr\Log\LoggerInterface;

class AdminSessionUserPlugin
{
    /**
     * @var LoggerInterface
     */
    private $logger;

    public function __construct(
        LoggerInterface $logger
    ) {
        $this->logger = $logger;
    }

    /**
     * Plugin su setUser di \Magento\Backend\Model\Auth\Session
     */
    public function beforeSetUser($subject, User $user)
    {
        $this->logger->debug('AdminSessionUserPlugin::beforeSetUser - user class: ' . get_class($user));
        $this->logger->debug('AdminSessionUserPlugin::beforeSetUser - user ID: ' . $user->getId());
        $this->logger->debug('AdminSessionUserPlugin::beforeSetUser - user data keys: ', array_keys($user->getData()));
        return [$user];
    }
}
