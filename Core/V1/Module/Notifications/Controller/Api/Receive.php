<?php
namespace Core\Notifications\Controller\Api;

use Magento\Framework\Controller\Result\Json;
use Magento\Framework\Controller\Result\JsonFactory;
use Magento\Framework\App\RequestInterface;
use Magento\Framework\Webapi\Exception as WebapiException;
use Core\Notifications\Service\NotificationService;
use Core\Logger\Facade\LoggerFacade;

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
        $result = $this->resultJsonFactory->create();

        try {
            $token = getenv('NOTIFICATIONS_TOKEN') ?? '';
            $payload = json_decode($this->request->getContent(), true);

            if (!is_array($payload)) {
                throw new WebapiException(
                    __('Invalid or malformed JSON payload'),
                    0,
                    WebapiException::HTTP_BAD_REQUEST
                );
            }

            if ($token === $this->request->getParam('token')) {
                $this->notificationService->addNotification($payload);
                return $result->setData(['success' => true]);
            }

            LoggerFacade::error('Invalid Token');
            throw new WebapiException(
                __('Invalid Token'),
                0,
                WebapiException::HTTP_FORBIDDEN
            );

        } catch (WebapiException $e) {
            throw $e; // lascio passare eccezioni Web API con status code già settato
        } catch (\Exception $e) {
            LoggerFacade::error('Error while receiving notification', ['error' => $e]);
            throw new WebapiException(
                __('Internal error'),
                0,
                WebapiException::HTTP_INTERNAL_ERROR
            );
        }
    }
}
