<?php
namespace Core\SystemCommand\Plugin;

use Magento\Framework\App\FrontControllerInterface;
use Magento\Framework\App\RequestInterface;
use Core\SystemCommand\Helper\SystemCommandHelper;
use Core\SystemCommand\Facade\SystemCommandFacade;

class InitSystemCommandPlugin
{
    protected SystemCommandHelper $helper;

    public function __construct(SystemCommandHelper $helper)
    {
        $this->helper = $helper;
    }

    public function beforeDispatch(FrontControllerInterface $subject, RequestInterface $request)
    {
        if (!SystemCommandFacade::isInitialized()) {
            SystemCommandFacade::init($this->helper);
        }
    }
}
