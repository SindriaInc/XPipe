<?php
namespace Pipelines\Orchestrator\Setup;

use Magento\Framework\Setup\InstallSchemaInterface;
use Magento\Framework\Setup\ModuleContextInterface;
use Magento\Framework\Setup\SchemaSetupInterface;
use Magento\Framework\DB\Ddl\Table;

class InstallSchema implements InstallSchemaInterface
{
    public function install(SchemaSetupInterface $setup, ModuleContextInterface $context)
    {
        $setup->startSetup();
        $connection = $setup->getConnection();

        // Tabella pipeline
        if (!$connection->isTableExists('pipelines_orchestrator_pipeline')) {
            $table = $connection->newTable($setup->getTable('pipelines_orchestrator_pipeline'))
                ->addColumn(
                    'pipeline_id',
                    Table::TYPE_INTEGER,
                    null,
                    ['identity' => true, 'unsigned' => true, 'nullable' => false, 'primary' => true],
                    'Pipeline ID'
                )
                ->addColumn(
                    'slug',
                    Table::TYPE_TEXT,
                    255,
                    ['nullable' => false],
                    'Pipeline Slug'
                )
                ->addColumn(
                    'template',
                    Table::TYPE_TEXT,
                    255,
                    ['nullable' => false],
                    'GitHub Template'
                )
                ->addColumn(
                    'environment',
                    Table::TYPE_TEXT,
                    100,
                    ['nullable' => false],
                    'Environment'
                )
                ->addColumn(
                    'variables',
                    Table::TYPE_TEXT,
                    '64k',
                    ['nullable' => true],
                    'JSON Variables'
                )
                ->addColumn(
                    'repo_name',
                    Table::TYPE_TEXT,
                    255,
                    ['nullable' => false],
                    'Repository Name'
                )
                ->addColumn(
                    'created_at',
                    Table::TYPE_TIMESTAMP,
                    ['nullable' => false, 'default' => Table::TIMESTAMP_INIT],
                    'Created At'
                )
                ->addIndex(
                    $setup->getIdxName('pipelines_orchestrator_pipeline', ['slug'], \Magento\Framework\DB\Adapter\AdapterInterface::INDEX_TYPE_UNIQUE),
                    ['slug'],
                    ['type' => \Magento\Framework\DB\Adapter\AdapterInterface::INDEX_TYPE_UNIQUE]
                )
                ->setComment('Pipeline configuration table');
            $connection->createTable($table);
        }

        // Tabella ledger
        if (!$connection->isTableExists('pipelines_orchestrator_ledger')) {
            $table = $connection->newTable($setup->getTable('pipelines_orchestrator_ledger'))
                ->addColumn(
                    'ledger_id',
                    Table::TYPE_INTEGER,
                    null,
                    ['identity' => true, 'unsigned' => true, 'nullable' => false, 'primary' => true],
                    'Ledger ID'
                )
                ->addColumn(
                    'pipeline_id',
                    Table::TYPE_INTEGER,
                    null,
                    ['unsigned' => true, 'nullable' => false],
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
                    ['nullable' => true, 'default' => null],
                    'Dispatch Timestamp'
                )
                ->addColumn(
                    'created_at',
                    Table::TYPE_TIMESTAMP,
                    ['nullable' => false, 'default' => Table::TIMESTAMP_INIT],
                    'Created At'
                )
                ->addColumn(
                    'updated_at',
                    Table::TYPE_TIMESTAMP,
                    ['nullable' => false, 'default' => Table::TIMESTAMP_INIT_UPDATE],
                    'Updated At'
                )
                ->addForeignKey(
                    $setup->getFkName('pipelines_orchestrator_ledger', 'pipeline_id', 'pipelines_orchestrator_pipeline', 'pipeline_id'),
                    'pipeline_id',
                    $setup->getTable('pipelines_orchestrator_pipeline'),
                    'pipeline_id',
                    Table::ACTION_CASCADE
                )
                ->setComment('Pipeline orchestration execution ledger');
            $connection->createTable($table);
        }

        $setup->endSetup();
    }
}
