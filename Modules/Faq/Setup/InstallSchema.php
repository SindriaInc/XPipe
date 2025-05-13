<?php

namespace Sindria\Faq\Setup;

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
            if (!$installer->tableExists('sindria_faq')) {
                $table = $installer->getConnection()->newTable(
                    $installer->getTable('sindria_faq')
                )
                    ->addColumn(
                        'faq_id',
                        Table::TYPE_INTEGER,
                        null,
                        ['identity' => true, 'nullable' => false, 'primary' => true, 'unsigned' => true],
                        'Faq ID'
                    )
                    ->addColumn(
                        'question',
                        Table::TYPE_TEXT,
                        null,
                        ['nullable' => false],
                        'Question'
                    )
                    ->addColumn(
                        'answer',
                        Table::TYPE_TEXT,
                        null,
                        ['nullable' => false],
                        'Answer'
                    )
                    ->addColumn(
                        'status',
                        Table::TYPE_SMALLINT,
                        1,
                        [],
                        'Faq Status'
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
                    ->setComment('Faq Table');
                $installer->getConnection()->createTable($table);

                $installer->getConnection()->addIndex(
                    $installer->getTable('sindria_faq'),
                    $setup->getIdxName(
                        $installer->getTable('sindria_faq'),
                        ['question', 'answer'],
                        \Magento\Framework\DB\Adapter\AdapterInterface::INDEX_TYPE_FULLTEXT
                    ),
                    ['question', 'answer'],
                    \Magento\Framework\DB\Adapter\AdapterInterface::INDEX_TYPE_FULLTEXT
                );
            }

            if (!$installer->tableExists('sindria_faq_store')) {
                $table = $installer->getConnection()->newTable(
                    $installer->getTable('sindria_faq_store')
                )
                    ->addColumn(
                        'faq_id',
                        Table::TYPE_INTEGER,
                        null,
                        ['nullable' => false, 'primary' => true, 'unsigned' => true],
                        'FAQ ID'
                    )
                    ->addColumn(
                        'store_id',
                        Table::TYPE_SMALLINT,
                        null,
                        ['nullable' => false, 'primary' => true, 'unsigned' => true],
                        'Store ID'
                    )
                    ->addIndex(
                        $installer->getIdxName('sindria_faq_store', ['store_id']),
                        ['store_id']
                    )
                    ->addForeignKey(
                        $installer->getFkName('sindria_faq_store', 'faq_id', 'sindria_faq', 'faq_id'),
                        'faq_id',
                        $installer->getTable('sindria_faq'),
                        'faq_id',
                        Table::ACTION_CASCADE
                    )
                    ->addForeignKey(
                        $installer->getFkName('sindria_faq_store', 'store_id', 'store', 'store_id'),
                        'store_id',
                        $installer->getTable('store'),
                        'store_id',
                        Table::ACTION_CASCADE
                    )
                    ->setComment('FAQ to Store View Link Table');

                $installer->getConnection()->createTable($table);
            }

        } catch (\Zend_Db_Exception $e) {
            $this->logger->critical($e);
        }

        $installer->endSetup();
    }
}
