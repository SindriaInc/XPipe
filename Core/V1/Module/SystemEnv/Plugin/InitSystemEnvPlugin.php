<?php
namespace Core\SystemEnv\Plugin;

use Magento\Framework\App\FrontControllerInterface;
use Magento\Framework\App\RequestInterface;
use Core\SystemEnv\Helper\SystemEnvHelper;
use Core\SystemEnv\Facade\SystemEnvFacade;

class InitSystemEnvPlugin
{
    protected SystemEnvHelper $helper;

    public function __construct(SystemEnvHelper $helper)
    {
        $this->helper = $helper;
    }

    public function beforeDispatch(FrontControllerInterface $subject, RequestInterface $request)
    {
        if (!SystemEnvFacade::isInitialized()) {
            SystemEnvFacade::init($this->helper);
        }
    }
}
