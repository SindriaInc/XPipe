{{-- Delete Modal--}}
<div class="modal fade" id="{{ "deleteModal_".$u->id }}" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="deleteModalLabel">{{ trans('dashboard.modals.users.delete.title') }}</h5>
                <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div class="modal-body">{{ trans('dashboard.modals.users.delete.p1') }} <br /><br />{{ trans('dashboard.modals.users.delete.p2') }} <br /><br /> {{ trans('dashboard.modals.users.delete.p3') }}</div>
            <div class="modal-footer">
                <form id="deleteForm" action="{{ route('dashboard.users.delete', $u->id) }}" method="post">
                    {{ csrf_field() }}

                    <button id="deleteSubmit" type="submit" class="btn btn-danger">{{ trans('global.button.confirm') }}</button>
                    <button class="btn btn-secondary" type="button" data-dismiss="modal">{{ trans('global.button.cancel') }}</button>
                </form>
            </div>
        </div>
    </div>
</div>
