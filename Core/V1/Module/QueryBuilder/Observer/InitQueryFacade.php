<?php
namespace Core\QueryBuilder\Observer;

use Magento\Framework\Event\ObserverInterface;
use Magento\Framework\Event\Observer;
use Core\QueryBuilder\Facade\QueryFacade;
use Core\QueryBuilder\Factory\QueryBuilderHelperFactory;

class InitQueryFacade implements ObserverInterface
{
    protected $factory;

    public function __construct(QueryBuilderHelperFactory $factory)
    {
        $this->factory = $factory;
    }

    public function execute(Observer $observer)
    {
        $helper = $this->factory->create();
        QueryFacade::init($helper);
    }
}
