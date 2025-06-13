<?php

namespace Pipelines\TemplateCatalog\Setup\Patch\Data;

use Magento\Framework\Setup\Patch\DataPatchInterface;
use Magento\Catalog\Model\CategoryFactory;
use Magento\Catalog\Api\CategoryRepositoryInterface;

class CreateTemplateStoreCategory implements DataPatchInterface
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

        // ID della "Default Category" (Root visibile in tutti gli store view esistenti)
        $parentCategoryId = 2;
        $parentPath = '1/2/3';

        $category = $this->categoryFactory->create();
        $category->setName('Template Store');
        $category->setIsActive(true);
        $category->setParentId($parentCategoryId);
        $category->setPath($parentPath);
        $category->setIncludeInMenu(true);
        $category->setCustomAttribute('is_anchor', 1); // opzionale: mostra i prodotti dei figli



        // Root Category (livello superiore alla default category)
//        $category = $this->categoryFactory->create();
//        $category->setName('Nuova Root Category');
//        $category->setIsActive(true);
//        $category->setParentId(2); // 0 indica root root
//        $category->setPath('1'); // radice assoluta
//        $category->setLevel(0);
//        $category->setIncludeInMenu(false);

        $this->categoryRepository->save($category);

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
