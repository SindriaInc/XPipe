# Module QueryBuilder

Magento 2 module to add query builder utils.

## Setup module

- Require your custom module: `composer require core/module-query-builder:@dev` OR `composer require core/module-query-builder:1.0.0`
- Run setup upgrade: `php bin/magento setup:upgrade`


## Usage

```php
use Core\QueryBuilder\Facade\QueryFacade;

// Usage
$table = 'iam_groups';
$sql = "Select * FROM " . $table;
$exampleQuery = QueryFacade::query($table, $sql);
// Fetch all with native SQL without ORM
$groups = $exampleQuery->fetchAll();
```

## Manual Initialization
```php
use Magento\Framework\App\ObjectManager;
use Core\QueryBuilder\Factory\QueryBuilderHelperFactory;
use Core\QueryBuilder\Facade\QueryFacade;


    public function __construct(
        GroupService     $groupService,
        RequestInterface $request
    ) {
        $this->groupService = $groupService;
        $this->request = $request;

        $queryBuilderHelperFactory = new QueryBuilderHelperFactory(ObjectManager::getInstance());
        $helper = $queryBuilderHelperFactory->create();
        QueryFacade::init($helper);
    }

// Usage
$table = 'iam_groups';
$sql = "Select * FROM " . $table;
$exampleQuery = QueryFacade::query($table, $sql);
// Fetch all with native SQL without ORM
$groups = $exampleQuery->fetchAll();
```


