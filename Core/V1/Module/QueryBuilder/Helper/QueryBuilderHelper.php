<?php
namespace Core\QueryBuilder\Helper;

use Magento\Framework\App\Helper\AbstractHelper;
use Magento\Framework\App\Helper\Context;
use Magento\Framework\App\ResourceConnection;

class QueryBuilderHelper extends AbstractHelper
{
    protected $resourceConnection;
    private $connection;

    public function __construct(Context $context, ResourceConnection $resourceConnection)
    {
        $this->resourceConnection = $resourceConnection;
        $this->connection = $resourceConnection->getConnection();
        parent::__construct($context);
    }

    public function query(string $table, string $sql): \Zend_Db_Statement_Interface
    {
        $this->connection->getTableName($table);
        return $this->connection->query($sql);
    }
}
