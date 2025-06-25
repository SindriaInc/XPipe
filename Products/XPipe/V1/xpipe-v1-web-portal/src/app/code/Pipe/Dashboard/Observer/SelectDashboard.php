<?php
namespace Pipe\Dashboard\Observer;

use Core\Logger\Facade\LoggerFacade;

use Magento\Framework\Event\ObserverInterface;
use Magento\Framework\Event\Observer;
use Magento\Backend\Model\Auth\Session as AuthSession;
use Pipe\Dashboard\Helper\DashboardHelper;
use Magento\Framework\View\Result\PageFactory;
use Magento\Framework\Controller\ResultFactory;


class SelectDashboard implements ObserverInterface
{

    private AuthSession $authSession;


    /**
     * Application Event Dispatcher
     *
     * @var \Magento\Framework\Event\ManagerInterface
     */
    protected $_eventManager;

    /**
     * @var ResultFactory
     */
    protected $_resultFactory;

    public function __construct(
        AuthSession $authSession,
        ResultFactory $resultFactory
    )
    {
        $this->authSession = $authSession;
        $this->_resultFactory = $resultFactory;
    }

    public function execute(Observer $observer)
    {

        // UNUSED see plugin RedirectAfterLogin.php

        LoggerFacade::info('SelectDashboard::execute - triggered event');

        $currentRoleName = $this->authSession->getUser()->getRole()->getRoleName();
        $dashboardRoute = DashboardHelper::selectDashboardRoute($currentRoleName);
        $resultRedirect = $this->_resultFactory->create(ResultFactory::TYPE_REDIRECT);

//        switch ($dashboardRoute) {
//            case 0:
//                return $resultRedirect->setPath('pipedashboard/superadmin/index');
//            case 1:
//                return $resultRedirect->setPath('pipedashboard/profile/index');
//            case 2:
//                return $resultRedirect->setPath('pipedashboard/individual/index');
//            case 3:
//                return $resultRedirect->setPath('pipedashboard/demo/index');
//            case 4:
//                return $resultRedirect->setPath('pipedashboard/dev/index');
//            default:
//                return $resultRedirect->setPath('pipedashboard/superadmin/index');
//        }
//        dd($resultRedirect->setPath('pipedashboard/superadmin/index'));

//        dump($resultRedirect->setPath('pipedashboard/superadmin/index'));
        LoggerFacade::info('SelectDashboard::execute ',
            ['object' => json_encode($resultRedirect->setPath('pipedashboard/superadmin/index'))]);

        echo $resultRedirect->setPath('pipedashboard/superadmin/index');


    }
}
