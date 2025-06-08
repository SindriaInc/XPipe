<?php
namespace Pipe\DebugSession\Plugin;

use Magento\Backend\Model\Auth;
use Magento\User\Model\User;
use Core\Logger\Facade\LoggerFacade;

class AuthPlugin
{
    public function afterLogin(Auth $subject, ?User $result)
    {
        if (!$result) {
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
            'serialized_size' => strlen(serialize($result))
        ]);

        return $result;
    }
}
