<?php

return [

    /**
     *  Copyright Sindria Inc.
     *
     *  Dashboard translation
     *
     */


    // Sidebar
    'sidebar.back'               => 'Back to Home',
    'sidebar.dashboard'          => 'Dashboard',
    'sidebar.pipelines'          => 'Pipelines',
    'sidebar.alerts'             => 'Alerts',
    'sidebar.users'              => 'Users',
    'sidebar.policies'           => 'Policies',
    'sidebar.settings'           => 'Settings',


    // Dashboard
    'dashboard.title'   => 'Dashboard',

    // Widgets
    'widgets.main.welcome'  => 'Welcome',
    'widgets.main.text'     => 'For additional installation and configuration help, see our documentation.',
    'widgets.main.download' => 'Download Agent',

    // Pipelines
    'pipelines.title'                       => 'Pipelines',
    'pipelines.add'                         => 'Create Pipeline',
    'pipelines.attach'                      => 'Attach Pipeline',
    'pipelines.edit'                        => 'Edit Pipeline',
    'pipelines.field.info'                  => 'Pipeline Info',
    'pipelines.field.id'                    => 'ID',
    'pipelines.field.name'                  => 'Name',
    'pipelines.field.content'               => 'Content',
    'pipelines.field.created_at'            => 'Created at',
    // Pipelines Search
    'pipelines.search.placeholder'          => 'Search pipeline...',
    // Policies Users
    'pipelines.users.title'              => 'Users',
    'pipelines.users.search.placeholder' => 'Search Users...',
    'pipelines.users.field.id'           => 'ID',
    'pipelines.users.field.username'     => 'Username',
    'pipelines.attach.field.user_id'     => 'Select User',
    'pipelines.attach.field.pipeline_id' => 'Select Pipeline',

    // Users
    'users.title'                       => 'Users',
    'users.add'                         => 'Add User',
    'users.edit'                        => 'Edit User',
    'users.field.info'                  => 'User Info',
    'users.field.id'                    => 'ID',
    'users.field.username'              => 'Username',
    'users.field.email'                 => 'Email',
    'users.field.name'                  => 'Name',
    'users.field.surname'               => 'Surname',
    'users.field.status'                => 'Status',
    'users.field.status.true'           => 'Active',
    'users.field.status.false'          => 'Disabled',
    'users.field.email_verified'        => 'Email Verified',
    'users.field.email_verified.true'   => 'Yes',
    'users.field.email_verified.false'  => 'No',
    'users.field.created_at'            => 'Created at',
    // Users Search
    'users.search.placeholder'          => 'Search users...',
    // Users Policies
    'users.policies.title'              => 'Policies',
    'users.policies.search.placeholder' => 'Search Policy...',
    'users.policies.field.name'         => 'Name',

    // Policies
    'policies.title'                       => 'Policies',
    'policies.add'                         => 'Add Policy',
    'policies.attach'                      => 'Attach Policy',
    'policies.edit'                        => 'Edit Policy',
    'policies.field.info'                  => 'Policy Info',
    'policies.field.id'                    => 'ID',
    'policies.field.name'                  => 'Name',
    'policies.field.type'                  => 'Type',
    'policies.field.type.select'           => 'Select Type',
    'policies.field.content'               => 'Content',
    'policies.field.created_at'            => 'Created at',
    // Policies Search
    'policies.search.placeholder'          => 'Search policy...',
    // Policies Users
    'policies.users.title'              => 'Users',
    'policies.users.search.placeholder' => 'Search Users...',
    'policies.users.field.id'           => 'ID',
    'policies.users.field.username'     => 'Username',
    'policies.attach.field.user_id'     => 'Select User',
    'policies.attach.field.policy_id'   => 'Select Policy',


    // Tournaments Subscribers
    'users.subscribers.title'                    => 'Subscribers',
    'users.subscribers.add'                      => 'Add Subscriber',
    'users.subscribers.show'                     => 'Edit Subscriber',
    'users.subscribers.edit'                     => 'Subscriber changed successfully',
    'users.subscribers.list'                     => 'List Subscribers',
    'users.subscribers.store'                    => 'Subscriber added successfully',
    'users.subscribers.store.error'              => 'Subscriber already registered',
    'users.subscribers.delete'                   => 'Subscriber deleted successfully',
    'users.subscribers.field.code'               => 'Subscriber code',
    'users.subscribers.field.data'               => 'Personal data',
    'users.subscribers.field.name'               => 'Name',
    'users.subscribers.field.surname'            => 'Surname',
    'users.subscribers.field.birthday'           => 'Birthday',
    'users.subscribers.field.email'              => 'Email',
    'users.subscribers.field.phone'              => 'Phone',
    'users.subscribers.field.info'               => 'Subscriber info',
    'users.subscribers.field.fit'                => 'Fit',
    'users.subscribers.field.club'               => 'Club',
    'users.subscribers.field.score'              => 'Score',
    'users.subscribers.field.category'           => 'Category',
    'users.subscribers.field.type'               => 'Type',
    'users.subscribers.field.note'               => 'Note',
    'users.subscribers.field.note.optional'      => 'Note (optional)',
    'users.subscribers.field.users'        => 'Tournaments',
    // Tournaments Subscribers Search
    'users.subscribers.search.placeholder'       => 'Search Subscribers...',


    // Settings
    'settings.title'                          => 'Settings',
    'settings.field.users'                    => 'Tournaments Settings',
    'settings.field.users.subscriptions'      => 'Enable/Disable Tournaments',


    // Modals
    'modals.warning.title' => 'Are you sure?',
    'modals.warning.p1'    => 'This operation could destroy all data from the database.',
    'modals.warning.p2'    => 'Caution is advised.',
    'modals.warning.p3'    => 'Select "Apply" to proceed.',
    'modals.upload.title'  => 'Upload file',
    'modals.logout.title'  => 'Are you sure?',
    'modals.logout.p1'     => 'Select "Logout" if you are ready to end the current session.',
    'modals.loader.title'  => 'Please wait...',
    'modals.loader.text'   => 'This may take some time',
    // Modals Pipelines
    'modals.pipelines.delete.title' => 'Are you sure?',
    'modals.pipelines.delete.p1'    => 'This operation will destroy all pipeline data',
    'modals.pipelines.delete.p2'    => 'Caution!',
    'modals.pipelines.delete.p3'    => 'Select "Confirm" to proceed.',
    // Modals Users
    'modals.users.delete.title' => 'Are you sure?',
    'modals.users.delete.p1'    => 'This operation will destroy all user data',
    'modals.users.delete.p2'    => 'Caution!',
    'modals.users.delete.p3'    => 'Select "Confirm" to proceed.',
    // Modals Policies
    'modals.policies.delete.title' => 'Are you sure?',
    'modals.policies.delete.p1'    => 'This operation will destroy all policy data',
    'modals.policies.delete.p2'    => 'Caution!',
    'modals.policies.delete.p3'    => 'Select "Confirm" to proceed.',
    // Modals Policies Detach
    'modals.policies.detach.title' => 'Are you sure?',
    'modals.policies.detach.p1'    => 'This operation will detach this policy from a user',
    'modals.policies.detach.p2'    => 'Caution!',
    'modals.policies.detach.p3'    => 'Select "Confirm" to proceed.',
    // Modals Tournaments Archive
    'modals.users.archive.title' => 'Are you sure?',
    'modals.users.archive.p1'    => 'This operation archive tournament permanent',
    'modals.users.archive.p2'    => 'Select "Confirm" to proceed.',
    // Modals Tournaments Subscribers
    'modals.users.subscribers.delete.title' => 'Are you sure?',
    'modals.users.subscribers.delete.p1'    => 'This operation will destroy all subscriber data',
    'modals.users.subscribers.delete.p2'    => 'Select "Confirm" to proceed.',







];
