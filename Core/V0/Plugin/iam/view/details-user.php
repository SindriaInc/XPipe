<?php

/**
 * @var \Sindria\Iam\ViewModel\UserInfoViewModel $viewModel
 */
$viewModel = \Sindria\Iam\ViewModel\UserInfoViewModel::getInstance();



?>


<div class="wrap">
    <h1 class="wp-heading-inline"><?= trans('iam.users.details') ?></h1>

    <hr class="wp-header-end">

    <!-- Messages -->
    <?php require_once ( __DIR__ . '/components/messages.php') ?>

    <?php //wp_editor('', 'user', array()); ?>

    <?php //dump($viewModel->data); ?>
    <?php //dd($viewModel->infoTable); ?>

    <?php //dd($viewModel->data); ?>
    <?php //dd($viewModel); ?>

    <?php $viewModel->infoTable->display(); ?>


    <h1 class="wp-heading-inline"><?= trans('iam.users.policies.title') ?></h1>

    <?php //echo $viewModel->addNewAction; ?>

    <hr class="wp-header-end">

    <!-- User Policies Table -->
    <?php $viewModel->userPoliciesTable->display(); ?>

    <!-- Modals -->
    <?php

//    foreach ($viewModel->userPoliciesTable->items as $item) {
//        $modal = $item['modal'];
//        $modal->display();
//    }

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
