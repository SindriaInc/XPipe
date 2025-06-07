<?php

namespace Sindria\Iam\Modal;

use Sindria\Toolkit\Modal\Modal;

class WarningModal extends Modal
{

    /**
     * Make warning modal - typically used to confirm entity delete from the database
     *
     * @override
     * @return void
     */
    protected function makeModal()
    {
        ?>

        <!-- The Modal -->
        <div id="<?= $this->id ?>" class="modal">

            <!-- Modal content -->
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="<?= $this->id . "-label" ?>"><?= trans('global.modals.warning.title') ?></h5>
                    <span id="<?= $this->id . "-close" ?>" class="close">&times;</span>
                </div>
                <div class="modal-body">
                    <?= trans('global.modals.warning.p1') ?> <br /><?= trans('global.modals.warning.p2') ?><br /><br /><?= trans('global.modals.warning.p3') ?>
                </div>
                <div class="modal-footer">
                    <form id="<?= $this->id . "-form" ?>" name="warning-form" action="<?= cms_dashboard_page_route('') ?>" method="<?= $this->method ?>">
                        <?= csrf_field() ?>
                        <input name="page" type="hidden" value="<?= $this->action ?>">
                        <input name="id" type="hidden" value="<?= $this->entityId ?>">

                        <button id="<?= $this->id . "-submit" ?>" type="submit" data-dismiss="" value="" class="button button-primary"><?= trans('global.button.apply') ?></button>
                        <button id="<?= $this->id . "-cancel" ?>" class="cancel button button-secondary" type="button" data-dismiss="modal"><?= trans('global.button.cancel') ?></button>
                    </form>
                </div>
            </div>

        </div>

        <?php
    }


}
