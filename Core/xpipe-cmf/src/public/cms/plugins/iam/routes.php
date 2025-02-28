<?php

return [
    'routes' => [
        // IAM
        'iam' => '\Sindria\Iam\Controller@users',
        // Users
        'users' => '\Sindria\Iam\Controller@users',
        'users-search' => '\Sindria\Iam\Controller@usersSearch',
        'users-export' => '\Sindria\Iam\Controller@usersExport',
        'details-user' => '\Sindria\Iam\Controller@detailsUser',
        'add-user' => '\Sindria\Iam\Controller@addUser',
        'store-user' => '\Sindria\Iam\Controller@storeUser',
        'show-user' => '\Sindria\Iam\Controller@showUser',
        'edit-user' => '\Sindria\Iam\Controller@editUser',
        'delete-user' => '\Sindria\Iam\Controller@deleteUser',
        'manage-policies' => '\Sindria\Iam\Controller@managePolicies',
        // Policies
        'policies' => '\Sindria\Iam\Controller@policies',
        'policies-search' => '\Sindria\Iam\Controller@policiesSearch',
        'policies-export' => '\Sindria\Iam\Controller@policiesExport',
        'details-policy' => '\Sindria\Iam\Controller@detailsPolicy',
        'add-policy' => '\Sindria\Iam\Controller@addPolicy',
        'store-policy' => '\Sindria\Iam\Controller@storePolicy',
        'show-policy' => '\Sindria\Iam\Controller@showPolicy',
        'edit-policy' => '\Sindria\Iam\Controller@editPolicy',
        'delete-policy' => '\Sindria\Iam\Controller@deletePolicy',
        'attach-policy' => '\Sindria\Iam\Controller@attachPolicy',
        'attach-store' => '\Sindria\Iam\Controller@attachStorePolicy',
        'detach-policy' => '\Sindria\Iam\Controller@detachPolicy',
        'detach-store' => '\Sindria\Iam\Controller@detachStorePolicy',
    ]
];
