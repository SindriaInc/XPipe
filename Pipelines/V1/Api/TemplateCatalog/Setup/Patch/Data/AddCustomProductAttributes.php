<?php

namespace Pipelines\TemplateCatalog\Setup\Patch\Data;

use Magento\Eav\Setup\EavSetupFactory;
use Magento\Eav\Model\Entity\Attribute\ScopedAttributeInterface;
use Magento\Framework\Setup\Patch\DataPatchInterface;
use Magento\Framework\Setup\ModuleDataSetupInterface;

class AddCustomProductAttributes implements DataPatchInterface
{
    private $moduleDataSetup;
    private $eavSetupFactory;

    public function __construct(
        ModuleDataSetupInterface $moduleDataSetup,
        EavSetupFactory          $eavSetupFactory
    ) {
        $this->moduleDataSetup = $moduleDataSetup;
        $this->eavSetupFactory = $eavSetupFactory;
    }

    public function apply()
    {
        $eavSetup = $this->eavSetupFactory->create(['setup' => $this->moduleDataSetup]);

        // Attributo 'cta'
        $eavSetup->addAttribute(
            \Magento\Catalog\Model\Product::ENTITY,
            'cta',
            [
                'type' => 'varchar',
                'label' => 'Call To Action',
                'input' => 'text',
                'required' => true,
                'visible_on_front' => true,
                'global' => ScopedAttributeInterface::SCOPE_GLOBAL,
                'user_defined' => true,
                'group' => 'General',
                'sort_order' => 100,
            ]
        );

        // Attributo 'ri'
        $eavSetup->addAttribute(
            \Magento\Catalog\Model\Product::ENTITY,
            'ri',
            [
                'type' => 'varchar',
                'label' => 'Resource Image Path',
                'input' => 'text',
                'required' => false,
                'visible_on_front' => true,
                'global' => ScopedAttributeInterface::SCOPE_GLOBAL,
                'user_defined' => true,
                'group' => 'General',
                'sort_order' => 101,
            ]
        );

        return $this;
    }

    public static function getDependencies()
    {
        return [];
    }

    public function getAliases()
    {
        return [];
    }
}
