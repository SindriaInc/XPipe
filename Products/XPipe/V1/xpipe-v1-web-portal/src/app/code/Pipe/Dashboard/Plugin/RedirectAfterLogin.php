<?php
namespace Pipe\Dashboard\Plugin;

use Magento\Backend\Model\Auth\Session;
use Magento\Framework\Controller\Result\Redirect;
use Magento\Framework\Controller\ResultFactory;
use Pipe\Dashboard\Helper\DashboardHelper;
use Magento\Backend\Controller\Adminhtml\Dashboard\Index as DashboardController;

class RedirectAfterLogin
{
    private Session $authSession;
    private ResultFactory $resultFactory;

    public function __construct(
        Session $authSession,
        ResultFactory $resultFactory
    ) {
        $this->authSession = $authSession;
        $this->resultFactory = $resultFactory;
    }

    public function afterExecute(DashboardController $subject, $result)
    {

        if (!$this->authSession->isLoggedIn()) {
            return $result;
        }

        $roleName = $this->authSession->getUser()->getRole()->getRoleName();

        $dashboardRoute = DashboardHelper::selectDashboardRoute($roleName);

        /** @var Redirect $resultRedirect */
        $resultRedirect = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);

        switch ($dashboardRoute) {
            case 0:
                return $resultRedirect->setPath('pipedashboard/superadmin/index');
            case 1:
                return $resultRedirect->setPath('pipedashboard/profile/index');
            case 2:
                return $resultRedirect->setPath('pipedashboard/individual/index');
            case 3:
                return $resultRedirect->setPath('pipedashboard/demo/index');
            case 4:
                return $resultRedirect->setPath('pipedashboard/dev/index');
            default:
                return $resultRedirect->setPath('pipedashboard/superadmin/index');
        }
    }
}
