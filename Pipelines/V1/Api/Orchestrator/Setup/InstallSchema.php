<?php
namespace Pipelines\Orchestrator\Setup;

use Psr\Log\LoggerInterface;
use Magento\Framework\Setup\InstallSchemaInterface;
use Magento\Framework\Setup\SchemaSetupInterface;
use Magento\Framework\Setup\ModuleContextInterface;
use Magento\Framework\DB\Ddl\Table;

class InstallSchema implements InstallSchemaInterface
{

    /**
     * @var LoggerInterface
     */
    protected $logger;

    /**
     * @param LoggerInterface $logger
     */
    public function __construct(LoggerInterface $logger)
    {
        $this->logger = $logger;
    }


    public function install(SchemaSetupInterface $setup, ModuleContextInterface $context)
    {
        $installer = $setup;
        $installer->startSetup();

        try {

            // Tabella pipeline
            if (!$installer->tableExists('pipelines_orchestrator_pipeline')) {
                $table = $installer->getConnection()->newTable(
                    $installer->getTable('pipelines_orchestrator_pipeline')
                )
                    ->addColumn(
                        'pipeline_id',
                        Table::TYPE_INTEGER,
                        null,
                        ['identity' => true, 'nullable' => false, 'primary' => true, 'unsigned' => true],
                        'Pipeline ID'
                    )
                    ->addColumn(
                        'slug',
                        Table::TYPE_TEXT,
                        255,
                        ['nullable' => false, 'unsigned' => true, 'unique' => true],
                        'Pipeline Slug'
                    )
                    ->addColumn(
                        'template',
                        Table::TYPE_TEXT,
                        255,
                        ['nullable' => false],
                        'Pipeline Template'
                    )
                    ->addColumn(
                        'owner',
                        Table::TYPE_TEXT,
                        255,
                        ['nullable' => false],
                        'Pipeline Owner'
                    )
                    ->addColumn(
                        'configmap_tenant',
                        Table::TYPE_TEXT,
                        255,
                        ['nullable' => false],
                        'Configmap Tenant'
                    )
                    ->addColumn(
                        'configmap_name',
                        Table::TYPE_TEXT,
                        255,
                        ['nullable' => false],
                        'Configmap Name'
                    )
                    ->addColumn(
                        'created_at',
                        Table::TYPE_TIMESTAMP,
                        null,
                        ['nullable' => false, 'default' => Table::TIMESTAMP_INIT],
                        'Created At'
                    )->addColumn(
                        'updated_at',
                        Table::TYPE_TIMESTAMP,
                        null,
                        ['nullable' => false, 'default' => Table::TIMESTAMP_INIT_UPDATE],
                        'Updated At')
                    ->setComment('Pipeline orchestration data source');
                $installer->getConnection()->createTable($table);

                // Add unique index on slug
                $installer->getConnection()->addIndex(
                    $installer->getTable('pipelines_orchestrator_pipeline'),
                    $setup->getIdxName('pipelines_orchestrator_pipeline', ['slug'], \Magento\Framework\DB\Adapter\AdapterInterface::INDEX_TYPE_UNIQUE),
                    ['slug'],
                    \Magento\Framework\DB\Adapter\AdapterInterface::INDEX_TYPE_UNIQUE
                );

                // Add index to search fields
                $installer->getConnection()->addIndex(
                    $installer->getTable('pipelines_orchestrator_pipeline'),
                    $setup->getIdxName(
                        $installer->getTable('pipelines_orchestrator_pipeline'),
                        ['slug', 'template', 'owner', 'configmap_tenant', 'configmap_name'],
                        \Magento\Framework\DB\Adapter\AdapterInterface::INDEX_TYPE_FULLTEXT
                    ),
                    ['slug', 'template', 'owner', 'configmap_tenant', 'configmap_name'],
                    \Magento\Framework\DB\Adapter\AdapterInterface::INDEX_TYPE_FULLTEXT
                );
            }



            // Tabella ledger
            if (!$installer->tableExists('pipelines_orchestrator_ledger')) {
                $table = $installer->getConnection()->newTable(
                    $installer->getTable('pipelines_orchestrator_ledger')
                )
                    ->addColumn(
                        'ledger_id',
                        Table::TYPE_INTEGER,
                        null,
                        ['identity' => true, 'nullable' => false, 'primary' => true, 'unsigned' => true],
                        'Ledger ID'
                    )
                    ->addColumn(
                        'pipeline_id',
                        Table::TYPE_INTEGER,
                        null,
                        ['nullable' => false, 'unsigned' => true],
                        'Pipeline ID'
                    )
                    ->addColumn(
                        'status',
                        Table::TYPE_TEXT,
                        32,
                        ['nullable' => false],
                        'Status'
                    )
                    ->addColumn(
                        'step',
                        Table::TYPE_TEXT,
                        255,
                        ['nullable' => true],
                        'Execution Step'
                    )
                    ->addColumn(
                        'message',
                        Table::TYPE_TEXT,
                        '64k',
                        ['nullable' => true],
                        'Status Message'
                    )
                    ->addColumn(
                        'payload',
                        Table::TYPE_TEXT,
                        '64k',
                        ['nullable' => true],
                        'Execution Payload Snapshot'
                    )
                    ->addColumn(
                        'retries',
                        Table::TYPE_INTEGER,
                        null,
                        ['default' => 0],
                        'Retry Count'
                    )
                    ->addColumn(
                        'dispatched_at',
                        Table::TYPE_TIMESTAMP,
                        null,
                        ['nullable' => true, 'default' => null],
                        'Dispatch Timestamp'
                    )
                    ->addColumn(
                        'created_at',
                        Table::TYPE_TIMESTAMP,
                        null,
                        ['nullable' => false, 'default' => Table::TIMESTAMP_INIT],
                        'Created At'
                    )->addColumn(
                        'updated_at',
                        Table::TYPE_TIMESTAMP,
                        null,
                        ['nullable' => false, 'default' => Table::TIMESTAMP_INIT_UPDATE],
                        'Updated At')
                    ->addIndex(
                        $installer->getIdxName('pipelines_orchestrator_ledger', ['pipeline_id']),
                        ['pipeline_id']
                    )
                    ->addForeignKey(
                        $installer->getFkName('pipelines_orchestrator_ledger', 'pipeline_id', 'pipelines_orchestrator_pipeline', 'pipeline_id'),
                        'pipeline_id',
                        $installer->getTable('pipelines_orchestrator_pipeline'),
                        'pipeline_id',
                        Table::ACTION_CASCADE
                    )
                    ->setComment('Pipeline orchestration execution ledger');

                $installer->getConnection()->createTable($table);
            }

        } catch (\Zend_Db_Exception $e) {
            $this->logger->critical($e);
        }

        $installer->endSetup();
    }

}
