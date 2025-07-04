<?php

namespace Iam\UsersMeta\Setup;

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
            if (!$installer->tableExists('iam_users_meta')) {
                $table = $installer->getConnection()->newTable(
                    $installer->getTable('iam_users_meta')
                )
                    ->addColumn(
                        'user_meta_id',
                        Table::TYPE_INTEGER,
                        null,
                        ['identity' => true, 'nullable' => false, 'primary' => true, 'unsigned' => true],
                        'Users Meta ID'
                    )
                    ->addColumn(
                        'username',
                        Table::TYPE_TEXT,
                        255,
                        ['nullable' => false, 'unsigned' => true, 'unique' => true],
                        'Username'
                    )
                    ->addColumn(
                        'job_title',
                        Table::TYPE_TEXT,
                        255,
                        ['nullable' => true],
                        'Job Title'
                    )
                    ->addColumn(
                        'seniority',
                        Table::TYPE_TEXT,
                        255,
                        ['nullable' => true],
                        'Seniority'
                    )
                    ->addColumn(
                        'location',
                        Table::TYPE_TEXT,
                        255,
                        ['nullable' => true],
                        'Location'
                    )
                    ->addColumn(
                        'work_mode',
                        Table::TYPE_TEXT,
                        255,
                        ['nullable' => true],
                        'Work Mode'
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
                    ->setComment('User Meta Table');
                $installer->getConnection()->createTable($table);

                // Add unique index on username
                $installer->getConnection()->addIndex(
                    $installer->getTable('iam_users_meta'),
                    $setup->getIdxName('iam_users_meta', ['username'], \Magento\Framework\DB\Adapter\AdapterInterface::INDEX_TYPE_UNIQUE),
                    ['username'],
                    \Magento\Framework\DB\Adapter\AdapterInterface::INDEX_TYPE_UNIQUE
                );

                // Index for search

//                $installer->getConnection()->addIndex(
//                    $installer->getTable('iam_groups'),
//                    $setup->getIdxName(
//                        $installer->getTable('iam_groups'),
//                        ['slug', 'label'],
//                        \Magento\Framework\DB\Adapter\AdapterInterface::INDEX_TYPE_FULLTEXT
//                    ),
//                    ['slug', 'label'],
//                    \Magento\Framework\DB\Adapter\AdapterInterface::INDEX_TYPE_FULLTEXT
//                );
            }


        } catch (\Zend_Db_Exception $e) {
            $this->logger->critical($e);
        }

        $installer->endSetup();
    }
}
