<?php
namespace Core\Sso\Controller\Adminhtml;

use Magento\Backend\App\Action;

class Login extends Action
{
    public function execute()
    {
        $authUrl = getenv('KEYCLOAK_AUTH_URL');
        $clientId = getenv('KEYCLOAK_CLIENT_ID_ADMIN');
        $redirectUri = $this->_url->getUrl('admin_sso/adminhtml/callback');
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
