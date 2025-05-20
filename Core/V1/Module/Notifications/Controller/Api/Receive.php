<?php
namespace Core\Notifications\Controller\Api;

use Magento\Framework\Controller\Result\Json;
use Magento\Framework\Controller\Result\JsonFactory;
use Magento\Framework\App\RequestInterface;
use Core\Notifications\Service\NotificationService;
use Core\Notifications\Helper\SystemEnvHelper;
use Core\Notifications\Helper\ApiResponseHelper;
use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\Webapi\Exception as WebapiException;

class Receive
{
    protected JsonFactory $resultJsonFactory;
    protected NotificationService $notificationService;
    protected RequestInterface $request;

    public function __construct(
        JsonFactory $resultJsonFactory,
        NotificationService $notificationService,
        RequestInterface $request
    ) {
        $this->resultJsonFactory = $resultJsonFactory;
        $this->notificationService = $notificationService;
        $this->request = $request;
    }

    /**
     * Endpoint API per ricezione notifiche.
     *
     * @return Json
     * @throws WebapiException
     */
    public function execute(): Json
    {
        try {
            $token = SystemEnvHelper::get('NOTIFICATIONS_TOKEN', '1234');
            $payload = json_decode($this->request->getContent(), true);

            if (!is_array($payload)) {
                throw ApiResponseHelper::sendError(400, 'Invalid or malformed JSON payload');
            }

            if ($token !== $this->request->getParam('token')) {
                LoggerFacade::error('Invalid Token');
                throw ApiResponseHelper::sendError(403, 'Invalid Token');
            }

            $this->notificationService->addNotification($payload);

            return ApiResponseHelper::sendSuccess($this->resultJsonFactory, 200, 'Notification received', []);
        } catch (WebapiException $e) {
            throw $e; // Già formattata correttamente
        } catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            throw ApiResponseHelper::sendError(500, 'Internal server error');
        }
    }
}
