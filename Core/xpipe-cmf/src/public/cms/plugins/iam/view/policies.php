<?php

/**
 * @var \Sindria\Iam\ViewModel\PoliciesViewModel $viewModel
 */
$viewModel = \Sindria\Iam\ViewModel\PoliciesViewModel::getInstance();

?>


<div class="wrap">
    <h1 class="wp-heading-inline"><?= trans('iam.policies.title') ?></h1>

    <?= $viewModel->addNewAction; ?>
    <?php echo $viewModel->attachPolicyAction; ?>
    <?php //echo $viewModel->detachPolicyAction; ?>

    <hr class="wp-header-end">

    <!-- Messages -->
    <?php require_once ( __DIR__ . '/components/messages.php') ?>

    <!-- Search Box -->
    <?= '<form method="post">' ?>
    <?= csrf_field() ?>
    <input name="page" type="hidden" value="policies-search">
    <?php $viewModel->dataTable->search_box('search', 'policies'); ?>
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
