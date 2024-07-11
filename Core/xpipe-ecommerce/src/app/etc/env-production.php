<?php
return [
    'backend' => [
        'frontName' => 'dashboard'
    ],
    'remote_storage' => [
        'driver' => 'file'
    ],
    'queue' => [
        'consumers_wait_for_messages' => 1
    ],
    'crypt' => [
        'key' => getenv('XPIPE_CORE_ECOMMERCE_CRYPT_KEY')
    ],
    'db' => [
        'table_prefix' => '',
        'connection' => [
            'default' => [
                'host' => getenv('XPIPE_CORE_ECOMMERCE_DB_HOST'),
                'dbname' => getenv('XPIPE_CORE_ECOMMERCE_DB_DATABASE'),
                'username' => getenv('XPIPE_CORE_ECOMMERCE_DB_USERNAME'),
                'password' => getenv('XPIPE_CORE_ECOMMERCE_DB_PASSWORD'),
                'model' => 'mysql4',
                'engine' => 'innodb',
                'initStatements' => 'SET NAMES utf8;',
                'active' => '1',
                'driver_options' => [
                    1014 => false
                ]
            ]
        ]
    ],
    'resource' => [
        'default_setup' => [
            'connection' => 'default'
        ]
    ],
    'x-frame-options' => 'SAMEORIGIN',
    'MAGE_MODE' => 'default',
    'session' => [
        'save' => 'files'
    ],
    'cache' => [
        'frontend' => [
            'default' => [
                'id_prefix' => 'be6_'
            ],
            'page_cache' => [
                'id_prefix' => 'be6_'
            ]
        ],
        'allow_parallel_generation' => false
    ],
    'lock' => [
        'provider' => 'db',
        'config' => [
            'prefix' => null
        ]
    ],
    'directories' => [
        'document_root_is_pub' => true
    ],
    'cache_types' => [
        'config' => 1,
        'layout' => 1,
        'block_html' => 1,
        'collections' => 1,
        'reflection' => 1,
        'db_ddl' => 1,
        'compiled_config' => 1,
        'eav' => 1,
        'customer_notification' => 1,
        'config_integration' => 1,
        'config_integration_api' => 1,
        'full_page' => 1,
        'config_webservice' => 1,
        'translate' => 1,
        'vertex' => 1
    ],
    'downloadable_domains' => [
        'xpipe.sindria.org'
    ],
    'install' => [
        'date' => 'Wed, 10 Jul 2024 22:06:24 +0000'
    ]
];
