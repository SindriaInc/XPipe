<?php
namespace Iam\UsersMeta\Controller\Api;

use Core\MicroFramework\Action\ValidateAccessTokenTrait;
use Iam\UsersMeta\Helper\UserMetaHelper;
use Iam\UsersMeta\Service\UserMetaService;
use Magento\Framework\App\RequestInterface;

use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\Logger\Facade\LoggerFacade;


class EditUserMeta
{
    use ValidateAccessTokenTrait;
    protected UserMetaService $userMetaService;
    protected RequestInterface $request;
    private string $accessToken;
    public function __construct(
        UserMetaService  $userMetaService,
        RequestInterface $request
    ) {
        $this->userMetaService = $userMetaService;
        $this->request = $request;
        $this->accessToken = UserMetaHelper::getIamUserMetaAccessToken();
    }

    /**
     *
     * @return StatusResponseInterface
     */
    public function execute() : StatusResponseInterface
    {

        try {
            $this->validateAccessToken($this->accessToken);

            $isJsonValid = UserMetaHelper::isJson($this->request->getContent());

            if ($isJsonValid === false) {
                return new StatusResponse(400, false, 'Syntax Error: Invalid or malformed JSON payload');
            }

            $payload = json_decode($this->request->getContent(), true);

            $isPayloadValid = UserMetaHelper::validatePayload($payload);

            if ($isPayloadValid === false) {
                LoggerFacade::error('Iam_Users::EditUser - Semantic Error: Invalid or malformed JSON payload');
                return new StatusResponse(422, false, 'Semantic Error: Invalid or malformed JSON payload');
            }

            $userMeta = $this->userMetaService->editUserMeta($payload);

            $data = ['user_meta' => $userMeta];

            return new StatusResponse(200, true, 'ok', $data);

        }
        catch (\Magento\Framework\Exception\NotFoundException $e) {
            LoggerFacade::error('UserMeta not found', ['error' => $e]);
            return  new StatusResponse(404, false, 'UserMeta not found');
        }
        catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }

    }
}
