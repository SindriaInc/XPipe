<?php
namespace Core\Sso\Controller\Customer;

use Magento\Framework\App\Action\Action;
use Magento\Framework\App\Action\Context;
use Magento\Customer\Model\Session as CustomerSession;
use Magento\Customer\Model\CustomerFactory;

class Callback extends Action
{
    protected $customerSession;
    protected $customerFactory;

    public function __construct(
        Context $context,
        CustomerSession $customerSession,
        CustomerFactory $customerFactory
    ) {
        $this->customerSession = $customerSession;
        $this->customerFactory = $customerFactory;
        parent::__construct($context);
    }

    public function execute()
    {
        $code = $this->getRequest()->getParam('code');
        if (!$code) {
            return $this->_redirect('customer/account/login');
        }

        $tokenUrl = getenv('KEYCLOAK_TOKEN_URL');
        $clientId = getenv('KEYCLOAK_CLIENT_ID_CUSTOMER');
        $clientSecret = getenv('KEYCLOAK_CLIENT_SECRET_CUSTOMER');
        $redirectUri = $this->_url->getUrl('sso/customer/callback');

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
            return $this->_redirect('customer/account/login');
        }

        $idToken = explode('.', $tokenData['id_token'])[1];
        $userData = json_decode(base64_decode(strtr($idToken, '-_', '+/')), true);

        $email = $userData['email'] ?? null;
        $firstname = $userData['given_name'] ?? '';
        $lastname = $userData['family_name'] ?? '';

        if (!$email) {
            return $this->_redirect('customer/account/login');
        }

        $autoprovision = getenv('KEYCLOAK_AUTOPROVISION_CUSTOMER') === '1';

        $customer = $this->customerFactory->create()->getCollection()
            ->addFieldToFilter('email', $email)
            ->getFirstItem();

        if (!$customer->getId() && !$autoprovision) {
            return $this->_redirect('customer/account/login');
        }

        if (!$customer->getId() && $autoprovision) {
            $customer->setEmail($email);
            $customer->setFirstname($firstname);
            $customer->setLastname($lastname);
            $customer->setWebsiteId(1);
            $customer->save();
        }

        $this->customerSession->setCustomerAsLoggedIn($customer);

        return $this->_redirect('customer/account');
    }
}
