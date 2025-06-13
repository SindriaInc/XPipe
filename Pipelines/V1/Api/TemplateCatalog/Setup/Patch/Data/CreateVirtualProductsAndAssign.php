<?php


namespace Pipelines\TemplateCatalog\Setup\Patch\Data;

use Magento\Framework\Setup\Patch\DataPatchInterface;
use Magento\Catalog\Model\ProductFactory;
use Magento\Catalog\Api\ProductRepositoryInterface;
use Magento\Catalog\Api\CategoryLinkManagementInterface;
use Magento\Catalog\Model\ResourceModel\Category\CollectionFactory as CategoryCollectionFactory;
use Magento\Framework\App\State;
use Magento\Framework\Exception\LocalizedException;

class CreateVirtualProductsAndAssign implements DataPatchInterface
{
    protected $productFactory;
    protected $productRepository;
    protected $categoryLink;
    protected $categoryCollectionFactory;
    protected $state;

    public function __construct(
        ProductFactory                  $productFactory,
        ProductRepositoryInterface      $productRepository,
        CategoryLinkManagementInterface $categoryLink,
        CategoryCollectionFactory       $categoryCollectionFactory,
        State                           $state
    )
    {
        $this->productFactory = $productFactory;
        $this->productRepository = $productRepository;
        $this->categoryLink = $categoryLink;
        $this->categoryCollectionFactory = $categoryCollectionFactory;
        $this->state = $state;
    }

    public function apply()
    {


        // Mappa nome categoria => ID (una volta sola)
        $categoryMap = $this->getCategoryNameIdMap();


        // Definizione prodotti
        $productsData = [
            [
                // Magento Default Attributes
                'sku' => 'deploy-minecraft-kubernetes',
                'name' => 'Deploy Minecraft Kubernetes',
                'price' => 00.00,
                'qty' => 999999,
                'categories' => ['Immutable', 'Kubernetes', 'Games'],
                'description' => 'Deploy a new Minecraft on kubernetes',
                // Custom Attributes
                'cta' => 'deployminecraftkubernetes/index',
                'ri' => 'Pipelines_TemplateStore::images/minecraft.png'
            ],
            [
                // Magento Default Attributes
                'sku' => 'deploy-minecraft-lightsail',
                'name' => 'Deploy Minecraft Lightsail',
                'price' => 00.00,
                'qty' => 999999,
                'categories' => ['Immutable', 'Aws', 'Games'],
                'description' => 'Deploy a new Minecraft on AWS lightsail',
                // Custom Attributes
                'cta' => 'deployminecraftkubernetes/index',
                'ri' => 'Pipelines_TemplateStore::images/minecraft.png'
            ],
            [
                // Magento Default Attributes
                'sku' => 'deploy-minecraft-bare-metal',
                'name' => 'Deploy Minecraft Bare Metal',
                'price' => 00.00,
                'qty' => 999999,
                'categories' => ['Immutable', 'Games'],
                'description' => 'Deploy a new Minecraft on a bare metal',
                // Custom Attributes
                'cta' => 'deployminecraftkubernetes/index',
                'ri' => 'Pipelines_TemplateStore::images/minecraft.png'
            ],
            // Aggiungi altri prodotti qui
        ];

        foreach ($productsData as $data) {
            /** @var \Magento\Catalog\Model\Product $product */
            $product = $this->productFactory->create();
            $product->setSku($data['sku'])
                ->setName($data['name'])
                ->setData('description',"<p>" . $data['description'] . "</p>")
                ->setData('short_description', $data['description'])
                ->setData('meta_description', $data['description'])
                ->setAttributeSetId(4) // default set
                ->setStatus(1) // enabled
                ->setWeight(0)
                ->setVisibility(4) // catalog & search
                ->setTypeId('virtual')
                ->setPrice($data['price'])
                ->setStockData([
                    'use_config_manage_stock' => 1,
                    'is_in_stock' => 1,
                    'qty' => $data['qty']
                ])
                ->setData('cta', $data['cta'])
                ->setData('ri', $data['ri']);

            $savedProduct = $this->productRepository->save($product);

            // Recupera le categorie esistenti
            $existingCategoryIds = $savedProduct->getCategoryIds();

            // Nuove categorie da assegnare
            $newCategoryIds = [];
            foreach ($data['categories'] as $categoryName) {
                if (isset($categoryMap[$categoryName])) {
                    $newCategoryIds[] = $categoryMap[$categoryName];
                }
            }

            // Unione (senza duplicati)
            $finalCategoryIds = array_unique(array_merge($existingCategoryIds, $newCategoryIds));

            if (!empty($finalCategoryIds)) {
                $this->categoryLink->assignProductToCategories(
                    $savedProduct->getSku(),
                    $finalCategoryIds
                );
            }
        }

        return $this;
    }

    /**
     * Mappa nome categoria => ID
     */
    protected function getCategoryNameIdMap()
    {
        $map = [];
        $collection = $this->categoryCollectionFactory->create()
            ->addAttributeToSelect('name');

        foreach ($collection as $cat) {
            $map[$cat->getName()] = $cat->getId();
        }

        return $map;
    }

    public static function getDependencies()
    {
        return [
            \Pipelines\TemplateCatalog\Setup\Patch\Data\CreateTemplateStoreSubcategories::class,
            \Pipelines\TemplateCatalog\Setup\Patch\Data\AddCustomProductAttributes::class
        ];
    }

    public function getAliases()
    {
        return [];
    }
}

