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

    /**
     * Query SQL parametrizzata (sintassi compatta).
     * Usa Zend_Db query() internamente (prepare + execute).
     *
     * @param string $table Nome tabella Magento (logico)
     * @param string $sql SQL con parametri :name
     * @param array $params Parametri associativi
     * @return \Zend_Db_Statement_Interface
     */
    public function query(string $table, string $sql, array $params = []): \Zend_Db_Statement_Interface
    {
        $table = $this->connection->getTableName($table);
        return $this->connection->query($sql, $params);
    }

    /**
     * Query SQL non parametrizzata.
     * Usare solo per query statiche, senza input utente!
     *
     * @param string $table
     * @param string $sql
     * @return \Zend_Db_Statement_Interface
     */
    public function simpleQuery(string $table, string $sql): \Zend_Db_Statement_Interface
    {
        $table = $this->connection->getTableName($table);
        return $this->connection->query($sql);
    }

    /**
     * Query SQL con prepare() + execute() esplicito.
     * Utile per log, test, profiling, esecuzione controllata.
     *
     * @param string $table
     * @param string $sql
     * @param array $params
     * @return \Zend_Db_Statement_Interface
     */
    public function manualPrepare(string $table, string $sql, array $params = []): \Zend_Db_Statement_Interface
    {
        $table = $this->connection->getTableName($table);
        $statement = $this->connection->prepare($sql);
        $statement->execute($params);
        return $statement;
    }



}
