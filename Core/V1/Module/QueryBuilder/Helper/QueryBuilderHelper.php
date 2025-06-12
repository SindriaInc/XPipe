<?php

namespace Core\QueryBuilder\Helper;

use Magento\Framework\App\Helper\AbstractHelper;
use Magento\Framework\App\Helper\Context;
use Magento\Framework\App\ResourceConnection;
use Magento\Framework\App\ObjectManager;

use Core\Logger\Facade\LoggerFacade;

class QueryBuilderHelper extends AbstractHelper
{

    protected $resourceConnection;

    private $connection;

    public function __construct(Context $context, ResourceConnection $resourceConnection)
    {
        $this->resourceConnection = $resourceConnection;
        $this->connection = $this->resourceConnection->getConnection();
        parent::__construct($context);
    }

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    /**
     * Get singleton instance
     *
     * @return \Core\QueryBuilder\Helper\QueryBuilderHelper
     */
    public static function getInstance() : \Core\QueryBuilder\Helper\QueryBuilderHelper
    {
        if (!isset(self::$instance)) {
            $className = __CLASS__;
            self::$instance = new $className(new Context(), new ResourceConnection());
        }

        return self::$instance;
    }


    /**
     * Generic query method
     *
     * @param string $table
     * @param string $sql
     * @return \Zend_Db_Statement_Interface
     */
    public function query(string $table, string $sql) : \Zend_Db_Statement_Interface
    {
        $this->connection->getTableName($table);
        $result = $this->connection->query($sql);
        LoggerFacade::debug('Query result: ', (array)print_r($result, true));
        return $result;
    }




//    public function runSqlQuery($table)
//    {
//        $connection = $this->resourceConnection->getConnection();
//        // $table is table name
//        $table = $connection->getTableName('my_custom_table');
//        //For Select query
//        $query = "Select * FROM " . $table;
//        $result = $connection->fetchAll($query);
//        $this->_logger->log(print_r($result, true));
//        $id = 2;
//        $query = "SELECT * FROM `" . $table . "` WHERE id = $id ";
//        $result1 = $connection->fetchAll($query);
//        $this->_logger->log(print_r($result1, true));
//        //For Insert query
//        $tableColumn = ['id', 'name', 'age'];
//        $tableData[] = [5, 'xyz', '20'];
//        $connection->insertArray($table, $tableColumn, $tableData);
//        $query = "INSERT INTO `" . $table . "`(`id`, `name`, `age`) VALUES (7,'mtm',33)";
//        $connection->query($query);
//        // For Update query
//        $id = 1;
//        $query = "UPDATE `" . $table . "` SET `name`= 'test' WHERE id = $id ";
//        $connection->query($query);
//        $query1 = "UPDATE `" . $table . "` SET `name`= 'test', `age` = 14 WHERE id = $id ";
//        $connection->query($query1);
//        // For Delete query
//        $id = 1;
//        $query = "DELETE FROM `" . $table . "` WHERE id = $id ";
//        $connection->query($query);
//    }







}