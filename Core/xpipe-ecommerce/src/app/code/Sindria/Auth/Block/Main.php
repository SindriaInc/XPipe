<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Sindria\Auth\Block;


use Magento\Framework\View\Element\Template;
use Magento\Framework\View\Element\Template\Context;
use Magento\Catalog\Model\ResourceModel\Product\CollectionFactory;
use Magento\Catalog\Model\Product\Attribute\Source\Status;
use Magento\Catalog\Model\ResourceModel\Product\Collection;

use Magento\Framework\Pricing\Helper\Data;

//use Magento\Framework\View\Page\Config;
//use Magento\Catalog\Model\CategoryFactory;
//use Magento\Framework\Registry;
//use Magento\Catalog\Model\Product\Visibility;



/**
 * Landing Page Block
 *
 * @override
 * @api
 * @author      Luca Pitzoi <luca.pitzoi@sindria.org>
 * @since 100.0.2
 */
class Main extends Template
{
    /**
     * @var CollectionFactory
     */
    protected $_productCollection;

    /**
     * @var \Magento\Catalog\Model\ResourceModel\Product\Collection
     */
    public $productCollection;

    public $individualProductItem;
    public $proProductItem;
    public $enterpriseProductItem;

    protected $priceHelper;

    /**
     * @param Context $context
     * @param CollectionFactory $productCollection
     * @param array $data
     */
    public function __construct(Context $context, CollectionFactory $productCollection, Data $priceHelper, array $data = [])
    {
        $this->_productCollection = $productCollection;
        $this->priceHelper = $priceHelper;
        parent::__construct($context, $data);

        $this->productCollection = $this->setProductCollection();

        $this->individualProductItem = NULL;
        $this->proProductItem = NULL;
        $this->enterpriseProductItem = NULL;

        $this->setProductCollectionItems();
    }

    /**
     * @return \Magento\Catalog\Model\ResourceModel\Product\Collection
     */
    public function setProductCollection() : Collection
    {

        $collection = $this->_productCollection->create();
        $collection->addAttributeToSelect('*');
        $collection->addAttributeToFilter('status',Status::STATUS_ENABLED);

        return $collection;
    }

    /**
     * @return \Magento\Catalog\Model\ResourceModel\Product\Collection
     */
    public function getProductCollection() : Collection
    {
        return $this->productCollection;
    }

    public function setProductCollectionItems() : void
    {
        $i = 0;

        foreach ($this->productCollection as $product) {

            switch ($i) {
                case 0:
                    $this>$this->individualProductItem = $product;
                    break;
                case 1:
                    $this>$this->proProductItem = $product;
                    break;
                case 2:
                    $this>$this->enterpriseProductItem = $product;
                    break;

            }

            $i++;

        }
    }



    public function getIndividualProduct()
    {
        return $this->individualProductItem;
    }

    public function getProProduct()
    {
        return $this->proProductItem;
    }

    public function getEnterpriseProduct()
    {
        return $this->enterpriseProductItem;
    }

    public function getFormattedPrice($price)
    {
        return $this->priceHelper->currency($price, true, false);
    }




//    /**
//     * @var \Magento\Framework\Registry $registry
//     */
//    private $_registry;
//
//    /**
//     * @var \Magento\Catalog\Model\CategoryFactory $categoryfactory
//     */
//    private $_categoryFactory;
//    /**
//     * @var Magento\Catalog\Model\ResourceModel\Product\CollectionFactory $productCollectionFactory
//     */
//    protected $_productCollectionFactory;
//
//    /**
//     * Constructor
//     * @param \Magento\Framework\App\Helper\Context $context
//     * @param \Magento\Catalog\Model\Session $catalogSession
//     **/
//    public function __construct(
//        Context $context,
//        Config $pageConfig,
//        CollectionFactory $productCollectionFactory,
//        Registry $registry,
//        CategoryFactory $categoryfactory,
//        array $data = []
//    ) {
//        parent::__construct($context, $data);
//        $this->pageConfig = $pageConfig;
//        $this->_registry = $registry;
//        $this->_categoryFactory = $categoryfactory;
//        $this->_productCollectionFactory = $productCollectionFactory;
//    }
//
//
//    /**
//     *@return array|void
//     **/
//    public function getProductDataCollection()
//    {
//
//        //$category_load = $this->_registry->registry('xpipe-plans');
//        //return $category_load;
//        //if($category_load):
//            //$categoryId = $category_load->getId();
//            $categoryId = 3;
//            $category_product_collection = $this->_categoryFactory->create()->load($categoryId);
//            $collection = $this->_productCollectionFactory->create();
//            $collection->addAttributeToSelect('*');
//            $collection->addCategoryFilter($category_product_collection);
//            $collection->addAttributeToFilter('visibility', Visibility::VISIBILITY_BOTH);
//            $collection->addAttributeToFilter('status', Status::STATUS_ENABLED);
//            //$collection->setPageSize(4);
//
//
//            //return $category_product_collection;
//            return $collection;
//
//        //endif;
//
//    }


















//    protected $_productFactory;
//
//    public function __construct(Context $context, ProductFactory $productFactory, array $data = [])
//    {
//        $this->_productFactory = $productFactory;
//        parent::__construct($context, $data);
//    }
//
//    public function getProductCollection()
//    {
//        $productCollection = $this->_productFactory->create()->getCollection();
//        return $productCollection;
//    }









//    /**
//     * @var \Magento\Catalog\Model\ResourceModel\Category\CollectionFactory
//     */
//    protected $_categoryFactory;
//
//    /**
//     * @var \Magento\Store\Model\StoreManagerInterface
//     */
//    protected $_storeManager;
//
//    /**
//     * @var \Magento\Catalog\Model\Category
//     */
//    protected $cat;
//
//    /**
//     * @var \Psr\Log\LoggerInterface
//     */
//    public $logger;
//
//    /**
//     * @var \Magento\Catalog\Model\ResourceModel\Product\CollectionFactory
//     */
//    protected $coreProductCollectionFactory;
//
//    /**
//     * @param \Magento\Catalog\Block\Product\Context $context
//     * @param \Magento\Catalog\Model\ResourceModel\Product\CollectionFactory $productCollectionFactory
//     * @param \Magento\Catalog\Model\Product\Visibility $catalogProductVisibility
//     * @param \Magento\Framework\App\Http\Context $httpContext
//     * @param \Magento\Rule\Model\Condition\Sql\Builder $sqlBuilder
//     * @param \Emthemes\FilterProduct\Model\Rule $rule
//     * @param \Magento\Widget\Helper\Conditions $conditionsHelper
//     * @param array $data
//     */
//    public function __construct(
//        \Magento\Catalog\Block\Product\Context $context,
//        \Emthemes\FilterProduct\Model\ResourceModel\Product\CollectionFactory $productCollectionFactory,
//        \Emthemes\FilterProduct\Model\ResourceModel\Bestsellers\CollectionFactory $bestSellerCollectionFactory,
//        \Magento\Catalog\Model\Product\Visibility $catalogProductVisibility,
//        \Magento\Framework\App\Http\Context $httpContext,
//        \Magento\Rule\Model\Condition\Sql\Builder $sqlBuilder,
//        \Emthemes\FilterProduct\Model\Rule $rule,
//        \Magento\Widget\Helper\Conditions $conditionsHelper,
//        \Magento\Catalog\Helper\ImageFactory $imageHelperFactory,
//        \Magento\Framework\Url\Helper\Data $urlHelper,
//        \Magento\Review\Model\ReviewFactory $reviewFactory,
//        \Emthemes\FilterProduct\Block\ImageBuilderFactory $customImageBuilderFactory,
//        \Magento\Catalog\Model\ResourceModel\Category\CollectionFactory $categoryFactory,
//        \Magento\Catalog\Model\Category $cat,
//        \Magento\Store\Model\StoreManagerInterface $storeManager,
//        array $data = [],
//        CF $coreProductCollectionFactory
//    )
//    {
//        $this->productCollectionFactory = $productCollectionFactory;
//        $this->bestSellerCollectionFactory = $bestSellerCollectionFactory;
//        $this->catalogProductVisibility = $catalogProductVisibility;
//        $this->httpContext = $httpContext;
//        $this->sqlBuilder = $sqlBuilder;
//        $this->rule = $rule;
//        $this->urlHelper = $urlHelper;
//        $this->conditionsHelper = $conditionsHelper;
//        $this->imageHelperFactory = $imageHelperFactory;
//        $this->_categoryFactory = $categoryFactory;
//        $this->cat = $cat;
//        $this->_storeManager = $storeManager;
//        $this->coreProductCollectionFactory = $coreProductCollectionFactory;
//        parent::__construct(
//            $context,
//            $productCollectionFactory,
//            $bestSellerCollectionFactory,
//            $catalogProductVisibility,
//            $httpContext,
//            $sqlBuilder,
//            $rule,
//            $conditionsHelper,
//            $imageHelperFactory,
//            $urlHelper,
//            $reviewFactory,
//            $customImageBuilderFactory,
//            $data
//        );
//    }
//
//    public function getProductCollection()
//    {
//        $categoryName = $this->getData('category_name');
//        $category = $this->_categoryFactory->create()->addAttributeToFilter('name',$categoryName)->addIsActiveFilter()->setPageSize(1);
//        $storeId = $this->_storeManager->getStore()->getId();
//        $categorySet = $this->_storeManager->getStore($storeId)->getRootCategoryId();
//        if($category->getSize()){
//            $categorySet = $this->cat->load($category->getFirstItem()->getId());
//        }
//        $collection = $this->coreProductCollectionFactory->create();                  $collection->addAttributeToSelect('*')
//        ->addCategoryFilter($categorySet)
//        ->setVisibility($this->catalogProductVisibility->getVisibleInCatalogIds())          ->addAttributeToFilter('visibility', \Magento\Catalog\Model\Product\Visibility::VISIBILITY_BOTH)->addAttributeToFilter('status',\Magento\Catalog\Model\Product\Attribute\Source\Status::STATUS_ENABLED)
//        ->setPageSize(50);
//        print_r((new \Magento\Framework\DataObject(['items' => $collection]))->debug());
//        return $collection;
//    }
//
//    /**
//     * Prepare and return product collection
//     *
//     * @return \Magento\Catalog\Model\ResourceModel\Product\Collection
//     */
//    public function createCollection()
//    {
//        $displayType = $this->getDisplayType();
//        $collection = null;
//        switch($displayType)
//        {
//            case 'all_products': $collection = $this->_getAllProductProductCollection();break;
//            case 'bestseller_products': $collection = $this->_getBestSellerProductCollection();break;
//            case 'category_filter': $collection = $this->getProductCollection();break;
//        }
//        $sort = explode(' ', $this->getData('order_by'));
//        $collection->addAttributeToSort($sort[0],$sort[1]);
//        $this->reviewFactory->create()->appendSummary($collection);
//        return $collection;
//    }





//    /**
//     * @var string
//     */
//    protected $_template = 'Sindria_LandingPage::landing_page.phtml';
//
//
//    protected $_categoryFactory;
//    protected $categoryRepository;
//    protected $_category;
//    protected $_productCollectionFactory;
//    protected $_productloader;
//
//    public function __construct(
//        \Magento\Catalog\Model\ResourceModel\Product\CollectionFactory $productCollectionFactory,
//        \Magento\Framework\View\Element\Template\Context $context,
//        \Magento\Catalog\Model\CategoryFactory $categoryFactory,
//        \Magento\Catalog\Model\ProductFactory $_productloader,
//        \Magento\Catalog\Model\CategoryRepository $categoryRepository
//    ) {
//        $this->_productCollectionFactory = $productCollectionFactory;
//        $this->_categoryFactory = $categoryFactory;
//        $this->bestverkochtCategoryId = "";
//        $this->_productloader = $_productloader;
//        $this->categoryRepository = $categoryRepository;
//
//        parent::__construct($context);
//    }
//
//    /**
//     * Get category object
//     *
//     * @return \Magento\Catalog\Model\Category
//     */
//    public function getCategory($categoryId) {
//        $this->_category = $this->_categoryFactory->create();
//        $this->_category->load($categoryId);
//
//        return $this->_category;
//    }
//
//    public function getProductCollection($categoryId, $limit = 1000) {
//        $category = $this->getCategory($categoryId);
//        $collection = $this->_productCollectionFactory->create();
//        $collection->addAttributeToSelect('*');
//        $collection->addCategoryFilter($category);
//        //$collection->addAttributeToFilter('visibility', \Magento\Catalog\Model\Product\Visibility::VISIBILITY_BOTH);
//        //$collection->addAttributeToFilter('status',\Magento\Catalog\Model\Product\Attribute\Source\Status::STATUS_ENABLED);
//        //NOPE//$sortFilter = ['category.category_id' => $categoryId];
//        //NOPE//$collection->addSortFilterParameters('position', 'category.position', 'category', $sortFilter);
//        $collection->setPageSize($limit);
//        $collection->setOrder('position','asc');
//
//        return $collection;
//    }





//    public function getWelcomeText()
//    {
//        return 'Hello World';
//    }


//    /**
//     * @var string
//     */
//    protected $_template = 'Sindria_LandingPage::landing_page.phtml';
//
//    protected $_productCollectionFactory;
//
//    public function __construct(
//        \Magento\Backend\Block\Template\Context $context,
//        \Magento\Catalog\Model\ResourceModel\Product\CollectionFactory $productCollectionFactory,
//        array $data = []
//    )
//    {
//        $this->_productCollectionFactory = $productCollectionFactory;
//        parent::__construct($context, $data);
//    }
//
//    public function getProductCollection()
//    {
//        $collection = $this->_productCollectionFactory->create();
//        //dd($collection);
//        $collection->addAttributeToSelect('*');
//        $collection->setPageSize(3); // fetching only 3 products
//        return $collection;
//    }









}
