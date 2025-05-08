<?php

namespace Sindria\Faq\Setup;

use Psr\Log\LoggerInterface;

class InstallSchema implements \Magento\Framework\Setup\InstallSchemaInterface
{



    /**
     * @var LoggerInterface
     */
    protected $logger;

    /**
     *
     * @param LoggerInterface $logger
     */
    public function __construct(LoggerInterface $logger)
    {
        $this->logger = $logger;
    }


    public function install(\Magento\Framework\Setup\SchemaSetupInterface $setup, \Magento\Framework\Setup\ModuleContextInterface $context)
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
                        \Magento\Framework\DB\Ddl\Table::TYPE_INTEGER,
                        null,
                        [
                            'identity' => true,
                            'nullable' => false,
                            'primary' => true,
                            'unsigned' => true,
                        ],
                        'Faq ID'
                    )
                    ->addColumn(
                        'question',
                        \Magento\Framework\DB\Ddl\Table::TYPE_TEXT,
                        null,
                        ['nullable => false'],
                        'Question'
                    )
                    ->addColumn(
                        'answer',
                        \Magento\Framework\DB\Ddl\Table::TYPE_TEXT,
                        null,
                        ['nullable => false'],
                        'Answer'
                    )
                    ->addColumn(
                        'status',
                        \Magento\Framework\DB\Ddl\Table::TYPE_SMALLINT,
                        1,
                        [],
                        'Faq Status'
                    )
                    ->addColumn(
                        'created_at',
                        \Magento\Framework\DB\Ddl\Table::TYPE_TIMESTAMP,
                        null,
                        ['nullable' => false, 'default' => \Magento\Framework\DB\Ddl\Table::TIMESTAMP_INIT],
                        'Created At'
                    )->addColumn(
                        'updated_at',
                        \Magento\Framework\DB\Ddl\Table::TYPE_TIMESTAMP,
                        null,
                        ['nullable' => false, 'default' => \Magento\Framework\DB\Ddl\Table::TIMESTAMP_INIT_UPDATE],
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
        } catch (\Zend_Db_Exception $e) {
            $this->logger->critical($e);
        }


        $installer->endSetup();
    }
}