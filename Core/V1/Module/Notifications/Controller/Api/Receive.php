<?php
namespace Core\Notifications\Controller\Api;

use Magento\Framework\Controller\Result\Json;
use Magento\Framework\Controller\Result\JsonFactory;
use Magento\Framework\App\RequestInterface;
use Core\Notifications\Service\NotificationService;
use Core\Logger\Facade\LoggerFacade;
class Receive
{
    protected $resultJsonFactory;
    protected $notificationService;
    protected $request;

    public function __construct(
        JsonFactory         $resultJsonFactory,
        NotificationService $notificationService,
        RequestInterface    $request
    ) {
        $this->resultJsonFactory = $resultJsonFactory;
        $this->notificationService = $notificationService;
        $this->request = $request;
    }

    /**
     * Load the page defined in view/adminhtml/layout/exampleadminnewpage_helloworld_index.xml
     *
     * @return Json
     */
    public function execute(): Json
    {

        $result = $this->resultJsonFactory->create();
        try {
            $token = getenv('NOTIFICATIONS_TOKEN') ?? '';

            $payload = json_decode($this->request->getContent(), true);

            if ($token === $this->request->getParam('token')) {
                $this->notificationService->addNotification($payload);
                return $result->setData(['success' => true]);
            }

            LoggerFacade::error('Invalid Token');
            return  $result->setData(['success' => false])->setHttpResponseCode(403);

        } catch (\Exception $e) {
            LoggerFacade::error('Error while getting the notification', ['error' => $e]);
            return $result->setData(['success' => false, 'message' => 'Internal error']);
        }
    }
}
