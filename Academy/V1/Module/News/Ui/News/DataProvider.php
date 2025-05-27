<?php
/**
 * Copyright © Magento, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Academy\News\Ui\News;

use Academy\News\Model\ResourceModel\News\CollectionFactory;
use Academy\News\Model\ResourceModel\News\Collection;
use Magento\Framework\App\Request\DataPersistorInterface;
use Magento\Ui\DataProvider\Modifier\PoolInterface;

/**
 * Class DataProvider
 */
class DataProvider extends \Magento\Ui\DataProvider\ModifierPoolDataProvider
{
    /**
     * @var Collection
     */
    protected $collection;

    /**
     * @var DataPersistorInterface
     */
    protected $dataPersistor;

    /**
     * @var array
     */
    protected $loadedData;

    /**
     * Constructor
     *
     * @param string $name
     * @param string $primaryFieldName
     * @param string $requestFieldName
     * @param CollectionFactory $blockCollectionFactory
     * @param DataPersistorInterface $dataPersistor
     * @param array $meta
     * @param array $data
     * @param PoolInterface|null $pool
     */
    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,
        CollectionFactory $blockCollectionFactory,
        DataPersistorInterface $dataPersistor,
        array $meta = [],
        array $data = [],
        PoolInterface $pool = null
    ) {
        $this->collection = $blockCollectionFactory->create();
        $this->dataPersistor = $dataPersistor;
        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data, $pool);
    }

    /**
     * Get data
     *
     * @return array
     */
    public function getData()
    {
        if (isset($this->loadedData)) {
            return $this->loadedData;
        }
        $items = $this->collection->getItems();
        /** @var \Academy\News\Model\News $news */
        foreach ($items as $news) {
            $this->loadedData[$news->getId()] = ['news' => $news->getData()];
        }

//        $data = $this->dataPersistor->get('academy_news');
        if (!empty($data)) {
            $news = $this->collection->getNewEmptyItem();
            $news->setData($data);
            $this->loadedData[$news->getId()] = $news->getData();
//            $this->dataPersistor->clear('academy_news');
        }

        dd($this->loadedData);

        return $this->loadedData;
    }


}
