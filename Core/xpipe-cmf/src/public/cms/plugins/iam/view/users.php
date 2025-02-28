<?php

/**
 * @var \Sindria\Iam\ViewModel\UsersViewModel $viewModel
 */
$viewModel = \Sindria\Iam\ViewModel\UsersViewModel::getInstance();

?>


<div class="wrap">
    <h1 class="wp-heading-inline"><?= trans('iam.users.title') ?></h1>

    <?= $viewModel->addNewAction; ?>
    <?= $viewModel->managePoliciesAction; ?>

    <hr class="wp-header-end">

    <!-- Messages -->
    <?php require_once ( __DIR__ . '/components/messages.php') ?>

    <!-- Search Box -->
    <?= '<form method="post">' ?>
    <?= csrf_field() ?>
    <input name="page" type="hidden" value="users-search">
    <?php $viewModel->dataTable->search_box('search', 'users'); ?>
    <?= '</form>' ?>

    <!-- DataTable -->
    <?php $viewModel->dataTable->display(); ?>

    <!-- Modals -->
    <?php

    foreach ($viewModel->dataTable->items as $item) {
        $modal = $item['modal'];
        $modal->display();
    }

    ?>


</div>

<style>

</style>


<script>

    function ready(callback) {
        // in case the document is already rendered
        if (document.readyState!='loading') callback();
        // modern browsers
        else if (document.addEventListener) document.addEventListener('DOMContentLoaded', callback);
        // IE <= 8
        else document.attachEvent('onreadystatechange', function() {
                if (document.readyState=='complete') callback();
            });
    }





    /**
     * Execute
     */
    ready(function() {



    });


</script>
