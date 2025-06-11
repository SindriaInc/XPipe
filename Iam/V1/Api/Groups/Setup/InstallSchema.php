<?php

namespace Iam\Groups\Setup;

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
            if (!$installer->tableExists('iam_groups')) {
                $table = $installer->getConnection()->newTable(
                    $installer->getTable('iam_groups')
                )
                    ->addColumn(
                        'group_id',
                        Table::TYPE_INTEGER,
                        null,
                        ['identity' => true, 'nullable' => false, 'primary' => true, 'unsigned' => true],
                        'Group ID'
                    )
                    ->addColumn(
                        'slug',
                        Table::TYPE_TEXT,
                        255,
                        ['nullable' => false, 'unsigned' => true, 'unique' => true],
                        'Slug'
                    )
                    ->addColumn(
                        'label',
                        Table::TYPE_TEXT,
                        255,
                        ['nullable' => false],
                        'Label'
                    )
                    ->addColumn(
                        'short',
                        Table::TYPE_TEXT,
                        255,
                        ['nullable' => true],
                        'Short'
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
                    ->setComment('Groups Table');
                $installer->getConnection()->createTable($table);

                // Add unique index on slug
                $installer->getConnection()->addIndex(
                    $installer->getTable('iam_groups'),
                    $setup->getIdxName('iam_groups', ['slug'], \Magento\Framework\DB\Adapter\AdapterInterface::INDEX_TYPE_UNIQUE),
                    ['slug'],
                    \Magento\Framework\DB\Adapter\AdapterInterface::INDEX_TYPE_UNIQUE
                );

                $installer->getConnection()->addIndex(
                    $installer->getTable('iam_groups'),
                    $setup->getIdxName(
                        $installer->getTable('iam_groups'),
                        ['slug', 'label'],
                        \Magento\Framework\DB\Adapter\AdapterInterface::INDEX_TYPE_FULLTEXT
                    ),
                    ['slug', 'label'],
                    \Magento\Framework\DB\Adapter\AdapterInterface::INDEX_TYPE_FULLTEXT
                );
            }



            // iam_user_group table
            if (!$installer->tableExists('iam_user_group')) {
                $table = $installer->getConnection()->newTable(
                    $installer->getTable('iam_user_group')
                )
                    ->addColumn(
                        'user_group_id',
                        Table::TYPE_INTEGER,
                        null,
                        ['nullable' => false, 'primary' => true, 'unsigned' => true],
                        'User Group ID'
                    )
                    ->addColumn(
                        'username',
                        Table::TYPE_TEXT,
                        null,
                        ['nullable' => false],
                        'Username'
                    )
                    ->addColumn(
                        'group_id',
                        Table::TYPE_INTEGER,
                        null,
                        ['nullable' => false, 'unsigned' => true],
                        'Group ID'
                    )
                    ->addIndex(
                        $installer->getIdxName('iam_user_group', ['group_id']),
                        ['group_id']
                    )
                    ->addForeignKey(
                        $installer->getFkName('iam_user_group', 'group_id', 'iam_groups', 'group_id'),
                        'group_id',
                        $installer->getTable('iam_groups'),
                        'group_id',
                        Table::ACTION_CASCADE
                    )

                    ->setComment('Groups to UserGroup View Link Table');

                $installer->getConnection()->createTable($table);
            }

        } catch (\Zend_Db_Exception $e) {
            $this->logger->critical($e);
        }

        $installer->endSetup();
    }
}
