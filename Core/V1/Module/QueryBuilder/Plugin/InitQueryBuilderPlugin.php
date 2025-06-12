<?php
namespace Core\QueryBuilder\Plugin;

use Core\QueryBuilder\Factory\QueryBuilderHelperFactory;
use Core\QueryBuilder\Facade\QueryFacade;

class InitQueryBuilderPlugin
{
    protected $factory;

    public function __construct(QueryBuilderHelperFactory $factory)
    {
        $this->factory = $factory;
    }

    public function beforeDispatch(\Magento\Framework\App\FrontControllerInterface $subject, \Magento\Framework\App\RequestInterface $request)
    {
        if (!QueryFacade::isInitialized()) {
            $helper = $this->factory->create();
            QueryFacade::init($helper);
        }
    }
}
