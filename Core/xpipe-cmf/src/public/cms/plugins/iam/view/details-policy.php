<?php

/**
 * @var \Sindria\Iam\ViewModel\PolicyInfoViewModel $viewModel
 */
$viewModel = \Sindria\Iam\ViewModel\PolicyInfoViewModel::getInstance();



?>


<div class="wrap">
    <h1 class="wp-heading-inline"><?= trans('iam.policies.details') ?></h1>

    <hr class="wp-header-end">

    <?php //wp_editor('', 'user', array()); ?>

    <?php //dump($viewModel->data); ?>
    <?php //dd($viewModel->infoTable); ?>

    <?php $viewModel->infoTable->display(); ?>




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
