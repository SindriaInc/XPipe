<?php
namespace Core\Sso\Controller\Customer;

use Magento\Framework\App\Action\Action;
use Magento\Framework\App\Action\Context;

class Login extends Action
{
    public function execute()
    {
        $authUrl = getenv('KEYCLOAK_AUTH_URL');
        $clientId = getenv('KEYCLOAK_CLIENT_ID_CUSTOMER');
        $redirectUri = $this->_url->getUrl('sso/customer/callback');
        $state = bin2hex(random_bytes(8));
        $scope = 'openid email profile';

        $url = $authUrl . '?client_id=' . urlencode($clientId)
            . '&redirect_uri=' . urlencode($redirectUri)
            . '&response_type=code'
            . '&scope=' . urlencode($scope)
            . '&state=' . $state;

        $this->getResponse()->setRedirect($url);
    }
}
