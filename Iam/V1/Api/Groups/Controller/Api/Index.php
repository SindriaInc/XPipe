<?php
namespace Iam\Groups\Controller\Api;

use Iam\Groups\Api\Data\StatusResponseInterface;
use Iam\Groups\Model\StatusResponse;
use Iam\Groups\Service\GroupService;
use Iam\Groups\Helper\SystemEnvHelper;
use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\RequestInterface;
use Magento\Framework\App\ObjectManager;

//use Core\QueryBuilder\Factory\QueryBuilderHelperFactory;
use Core\QueryBuilder\Facade\QueryFacade;

class Index
{
    protected GroupService $groupService;
    protected RequestInterface $request;

    public function __construct(
        GroupService     $groupService,
        RequestInterface $request
    ) {
        $this->groupService = $groupService;
        $this->request = $request;

//        $queryBuilderHelperFactory = new QueryBuilderHelperFactory(ObjectManager::getInstance());
//        $helper = $queryBuilderHelperFactory->create();
//        QueryFacade::init($helper);
    }

    /**
     * @return StatusResponseInterface
     */
    public function execute() : StatusResponseInterface
    {
        try {
            $token = SystemEnvHelper::get('IAM_GROUPS_ACCESS_TOKEN', '1234');

            if ($token !== $this->request->getHeader('X-Token-XPipe')) {
                LoggerFacade::error('Invalid Token');
                return new StatusResponse(403, false, 'Invalid Token');
            }

            $params = $this->request->getParams();

            // Disabled temp
            //$groups = $this->groupService->getGroups($params);

            // Query builder example
            $table = 'iam_groups';
            $sql = "Select * FROM " . $table;
            $exampleQuery = QueryFacade::query($table, $sql);

            //dd($exampleQuery->fetchAll());

            // Fetch all with native SQL without ORM
            $groups = $exampleQuery->fetchAll();


            $data = ['groups' => $groups];

            return new StatusResponse(200, true, 'ok', $data);

        } catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }
    }
}
