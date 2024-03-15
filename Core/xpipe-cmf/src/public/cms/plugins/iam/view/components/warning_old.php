<?php
/**
 * @file warning.php
 */
?>

<!-- Warning Modal-->
<div class="modal fade" id="warningModal" tabindex="-1" role="dialog" aria-labelledby="warningModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="warningModalLabel"><?= trans('global.modals.warning.title') ?></h5>
                <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div class="modal-body"><?= trans('global.modals.warning.p1') ?> <br /><?= trans('global.modals.warning.p2') ?><br /><br /><?= trans('global.modals.warning.p3') ?></div>
            <div class="modal-footer">
                <form id="warningForm" action="<?= cms_dashboard_page_route('') ?>" method="get">
                    <?= csrf_field() ?>
                    <input type="hidden" name="page" value="settings-import-points">

                    <button id="warningSubmit" type="submit" data-dismiss="" value="" class="btn btn-<?= (!isset($side['color']) || is_null($side['color'])) ? 'success' : $side['color'] ?>"><?= trans('global.button.apply') ?></button>
                    <button class="btn btn-secondary" type="button" data-dismiss="modal"><?= trans('global.button.cancel') ?></button>
                </form>
            </div>
        </div>
    </div>
</div>

<!--<script>-->
<!---->
<!--    /**-->
<!--     * General backoffice function-->
<!--     *-->
<!--     * @param e-->
<!--     */-->
<!--    $(document).ready(function() {-->
<!---->
<!--        // Hide warningModal when submit and show loader-->
<!--        $('#warningForm').submit(function(event) {-->
<!--            $('#warningModal').modal('hide');-->
<!--            $('#loaderModal').modal('show');-->
<!--        });-->
<!---->
<!--        // Hide uploadModal when submit and show loader-->
<!--        $('#uploadForm').submit(function(event) {-->
<!--            $('#uploadModal').modal('hide');-->
<!--            $('#loaderTopModal').modal('show');-->
<!--        });-->
<!---->
<!--    });-->
<!---->
<!--</script>-->
