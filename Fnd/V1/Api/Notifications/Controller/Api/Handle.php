<?php
namespace Fnd\Notifications\Controller\Api;

use Fnd\Notification\Model\Publisher\NotificationPublisher;
use Fnd\Notifications\Helper\NotificationsHelper;
use Fnd\Notifications\Model\Consumer\NotificationsData;
use Magento\Framework\App\RequestInterface;
use Magento\Framework\MessageQueue\PublisherInterface;


use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\Logger\Facade\LoggerFacade;
use Core\MicroFramework\Action\ValidateAccessTokenTrait;


class Handle
{
    use ValidateAccessTokenTrait;


    protected RequestInterface $request;

    private string $accessToken;
    protected $publisher;
    private $notificationsData;

    public function __construct(
        RequestInterface $request,
        PublisherInterface $publisher,
        NotificationsData $notificationsData
    ) {

        $this->request = $request;
        $this->accessToken = NotificationsHelper::getFndNotificationsAccessToken();
        $this->publisher = $publisher;
        $this->notificationsData = $notificationsData;


    }

    /**
     * @return StatusResponseInterface
     */
    public function execute(): StatusResponseInterface
    {
        try {

            $this->validateAccessToken($this->accessToken);

            $isJsonValid = NotificationsHelper::isJson($this->request->getContent());

            if ($isJsonValid === false) {
                return new StatusResponse(400, false, 'Syntax Error: Invalid or malformed JSON payload');
            }

            $payload = json_decode($this->request->getContent(), true);

            $isPayloadValid = NotificationsHelper::validatePayload($payload);

            if ($isPayloadValid === false) {
                LoggerFacade::error('Fnd_Notifications::handle - Semantic Error: Invalid or malformed JSON payload');
                return new StatusResponse(422, false, 'Semantic Error: Invalid or malformed JSON payload');
            }


            $this->notificationsData->setData(json_encode($payload));
            $this->publisher->publish('fnd.topic.notifications', $this->notificationsData);

            return new StatusResponse(200, true, 'Notification dispatched to queue');

        } catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return new StatusResponse(500, false, 'Internal server error');
        }
    }
}
