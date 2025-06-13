<?php

namespace Pipelines\TemplateCatalog\Setup\Patch\Data;

use Magento\Framework\Setup\Patch\DataPatchInterface;
use Magento\Catalog\Model\CategoryFactory;
use Magento\Catalog\Api\CategoryRepositoryInterface;
use Magento\Framework\Exception\LocalizedException;

class CreateTemplateStoreSubcategories implements DataPatchInterface
{
    protected $categoryFactory;
    protected $categoryRepository;

    public function __construct(
        CategoryFactory $categoryFactory,
        CategoryRepositoryInterface $categoryRepository
    ) {
        $this->categoryFactory = $categoryFactory;
        $this->categoryRepository = $categoryRepository;
    }

    public function apply()
    {

        // Recupera "Template Store"
        $templateStoreCategory = $this->getCategoryByName('Template Store');
        if (!$templateStoreCategory || !$templateStoreCategory->getId()) {
            throw new LocalizedException(__('Category Template Store not found.'));
        }

        $parentId = $templateStoreCategory->getId();

        $subcategoryNames = [
            'Mutable',
            'Immutable',
            'Included',
            'Paid',
            'Atomic',
            'Lego',
            'Solutions',
            'Middleware',
            'Monitoring',
            'Apm',
            'Games',
            'Kubernetes',
            'Docker',
            'Aws',
            'Azure',
            'Google Cloud',
            'Digital Ocean',
            'Ovh',
            'Bare Metal',
            'Application',
            'Java',
            'Php',
            'Python',
            'C',
            'C#',
            'C++',
            'Go',
            'Rust',
            'JavaScript',
            'Angular',
            'Vue',
            'React',
            'Sencha',
        ];

        foreach ($subcategoryNames as $name) {
            $subcategory = $this->categoryFactory->create();
            $subcategory->setName($name);
            $subcategory->setIsActive(true);
            $subcategory->setParentId($parentId);
            $subcategory->setPath('1/2/3/4'); // fondamentale: path corretto!
            $subcategory->setIncludeInMenu(true);
            $subcategory->setCustomAttribute('is_anchor', 1);
            $this->categoryRepository->save($subcategory);
        }

        return $this;
    }

    protected function getCategoryByName($name)
    {
        $collection = $this->categoryFactory->create()->getCollection()
            ->addAttributeToFilter('name', $name)
            ->addAttributeToSelect('*')
            ->setPageSize(1);

        return $collection->getFirstItem();
    }

    public static function getDependencies()
    {
        return [
            \Pipelines\TemplateCatalog\Setup\Patch\Data\CreateTemplateStoreCategory::class
        ];
    }

    public function getAliases()
    {
        return [];
    }
}
