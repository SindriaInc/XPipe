<?php

/**
 * @var \Sindria\Iam\ViewModel\UserAddViewModel $viewModel
 */
$viewModel = \Sindria\Iam\ViewModel\UserAddViewModel::getInstance();

?>


<div class="wrap">
    <h1 class="wp-heading-inline"><?= trans('iam.users.add') ?></h1>

    <hr class="wp-header-end">

    <?php require_once ( __DIR__ . '/components/messages.php') ?>

    <?php //wp_editor('', 'user', array()); ?>

    <?php $viewModel->form->display(); ?>




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
