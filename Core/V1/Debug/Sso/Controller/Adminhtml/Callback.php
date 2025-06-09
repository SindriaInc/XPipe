<?php
namespace Core\Sso\Controller\Adminhtml;

use Magento\Backend\App\Action;
use Magento\User\Model\UserFactory;
use Magento\Backend\Model\Auth\Session as AdminSession;

class Callback extends Action
{
    protected $userFactory;
    protected $adminSession;

    public function __construct(
        \Magento\Backend\App\Action\Context $context,
        UserFactory $userFactory,
        AdminSession $adminSession
    ) {
        $this->userFactory = $userFactory;
        $this->adminSession = $adminSession;
        parent::__construct($context);
    }

    public function execute()
    {
        $code = $this->getRequest()->getParam('code');
        if (!$code) {
            return $this->_redirect('admin');
        }

        $tokenUrl = getenv('KEYCLOAK_TOKEN_URL');
        $clientId = getenv('KEYCLOAK_CLIENT_ID_ADMIN');
        $clientSecret = getenv('KEYCLOAK_CLIENT_SECRET_ADMIN');
        $redirectUri = $this->_url->getUrl('admin_sso/adminhtml/callback');

        $ch = curl_init($tokenUrl);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, [
            'grant_type' => 'authorization_code',
            'code' => $code,
            'redirect_uri' => $redirectUri,
            'client_id' => $clientId,
            'client_secret' => $clientSecret,
        ]);
        $response = curl_exec($ch);
        curl_close($ch);

        $tokenData = json_decode($response, true);
        if (!isset($tokenData['id_token'])) {
            return $this->_redirect('admin');
        }

        $idToken = explode('.', $tokenData['id_token'])[1];
        $userData = json_decode(base64_decode(strtr($idToken, '-_', '+/')), true);

        $email = $userData['email'] ?? null;
        $firstname = $userData['given_name'] ?? '';
        $lastname = $userData['family_name'] ?? '';

        if (!$email) {
            return $this->_redirect('admin');
        }

        $autoprovision = getenv('KEYCLOAK_AUTOPROVISION_ADMIN') === '1';

        $user = $this->userFactory->create()->getCollection()
            ->addFieldToFilter('email', $email)
            ->getFirstItem();

        if (!$user->getId() && !$autoprovision) {
            return $this->_redirect('admin');
        }

        if (!$user->getId() && $autoprovision) {
            $user->setEmail($email);
            $user->setFirstname($firstname);
            $user->setLastname($lastname);
            $user->setUsername($email);
            $user->setPassword(md5(uniqid()));
            $user->setIsActive(1);
            $user->save();
        }

        $this->adminSession->setUser($user);

        return $this->_redirect('admin');
    }
}
