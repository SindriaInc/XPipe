{{-- Delete Modal--}}
<div class="modal fade" id="{{ "deleteModal_".$subscriber->id }}" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="deleteModalLabel">{{ trans('dashboard.modals.tournaments.subscribers.delete.title') }}</h5>
                <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div class="modal-body">{{ trans('dashboard.modals.tournaments.subscribers.delete.p1') }} <br /><br />{{ trans('dashboard.modals.tournaments.subscribers.delete.p2') }}</div>
            <div class="modal-footer">
                <form id="deleteForm" action="{{ route('dashboard.tournaments.subscribers.delete', $subscriber->id) }}" method="post">
                    {{ csrf_field() }}

                    <input type="hidden" value="{{ $subscriber->tournament_id }}" name="tournament_id" required>

                    <button id="deleteSubmit" type="submit" class="btn btn-danger">{{ trans('global.button.confirm') }}</button>
                    <button class="btn btn-secondary" type="button" data-dismiss="modal">{{ trans('global.button.cancel') }}</button>
                </form>
            </div>
        </div>
    </div>
</div>
