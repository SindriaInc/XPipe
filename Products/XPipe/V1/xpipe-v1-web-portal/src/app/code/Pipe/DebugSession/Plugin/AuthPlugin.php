<?php

namespace Pipe\DebugSession\Plugin;

use Magento\Backend\Model\Auth;
use Magento\Backend\Model\Auth\Session as AdminSession;
use Magento\User\Model\User;
use Core\Logger\Facade\LoggerFacade;

class AuthPlugin
{
    /**
     * Logga info dopo il login (successo o fallito)
     */
    public function afterLogin(Auth $subject, ?User $result)
    {
        if (!$result || !$result->getId()) {
            LoggerFacade::warning('AuthPlugin::afterLogin - login failed or no result');
            return $result;
        }

        $data = $result->getData();
        $summary = [];

        foreach ($data as $key => $value) {
            $summary[$key] = is_scalar($value) ? $value : gettype($value);
        }

        LoggerFacade::info('AuthPlugin::afterLogin - user data', [
            'user_id' => $result->getId(),
            'username' => $result->getUserName(),
            'data_keys' => array_keys($data),
            'summary' => $summary,
            'serialized_size' => strlen(serialize($result)),
            'class' => get_class($result)
        ]);

        return $result;
    }

    /**
     * Logga quando viene settato l'utente nella sessione
     */
    public function beforeSetUser(AdminSession $subject, User $user)
    {
        LoggerFacade::debug('AuthPlugin::beforeSetUser - user being set in admin session', [
            'user_id' => $user->getId(),
            'username' => $user->getUserName(),
            'class' => get_class($user),
            'serialized_size' => strlen(serialize($user))
        ]);
    }
}
