<?php
/**
 * @file loader.php
 */
?>

<!-- Loader Modal-->
<div class="modal fade loader-overlay" id="loaderModal" tabindex="-1" role="dialog" aria-labelledby="loaderModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="loader-content">
            <div class="loader-title text-center">
                <?= trans('global.modals.loader.title') ?>
            </div>
            <div class="loader-text text-center">
                <?= trans('global.modals.loader.text') ?>
            </div>
            <div class="text-center">
                <i class="fa fa-spinner fa-spin loader-spinner"></i>
            </div>
        </div>
    </div>
</div>
