<?php
namespace Iam\Groups\Controller\Api;

use Iam\Groups\Api\Data\StatusResponseInterface;
use Iam\Groups\Model\StatusResponse;
use Iam\Groups\Service\GroupsService;
use Iam\Groups\Helper\SystemEnvHelper;
use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\RequestInterface;

class Index
{
    protected GroupsService $groupsService;
    protected RequestInterface $request;

    public function __construct(
        GroupsService    $groupsService,
        RequestInterface $request
    ) {
        $this->groupsService = $groupsService;
        $this->request = $request;
    }

    /**
     * @return array
     */
    public function execute(): array
    {
        try {
            $token = SystemEnvHelper::get('IAM_GROUPS_ACCESS_TOKEN', '1234');

            // TODO: convertire questo in header standard X-Token-XPipe
            if ($token !== $this->request->getParam('token')) {
                LoggerFacade::error('Invalid Token');
                //return new StatusResponse(403, false, 'Invalid Token');
            }

            $data = [];
            $groups = $this->groupsService->getAllGroups();

            // Logging tipo e contenuto
            LoggerFacade::info('GroupsService::getAllGroups() result', [
                'type' => gettype($groups),
                'sample' => is_array($groups) ? array_slice($groups, 0, 1) : $groups
            ]);

            // Se per caso è una stringa JSON, decodificala (fallback safety)
            if (is_string($groups)) {
                LoggerFacade::warning('Groups is a JSON string, decoding...');
                $groups = json_decode($groups, true);
            }

            // Verifica che dopo eventuale decoding sia davvero un array
            if (!is_array($groups)) {
                LoggerFacade::error('Groups is not an array after decoding', ['value' => $groups]);
                //return new StatusResponse(500, false, 'Invalid groups format');
            }

//            $cleanedGroups = [];
//
//            foreach ($groups as $key => $group) {
//                $cleanedGroups[$key] = $group;
//            }
//
//            $encoded = json_encode($cleanedGroups);
//
//            $decoded = json_decode($encoded, true);

            //dd($decoded);

            $data = ['groups' => $groups];

            //dd($data);

            //dd(new StatusResponse(200, true, 'ok', $data));

            //return $this->resultFactory->create(ResultFactory::TYPE_JSON)->setData($groups);

            $response =  new StatusResponse(200, true, 'ok', $data);


            $resultAMinchia = json_decode(json_encode($response), true);

            return $resultAMinchia;

            //return new StatusResponse(200, true, 'ok', $data);

        } catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            //return new StatusResponse(500, false, 'Internal server error');
            return [];
        }
    }
}
