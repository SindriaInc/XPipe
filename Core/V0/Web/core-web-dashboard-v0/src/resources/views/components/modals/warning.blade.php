{{-- Warning Modal--}}
<div class="modal fade" id="warningModal" tabindex="-1" role="dialog" aria-labelledby="warningModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="warningModalLabel">{{ trans('dashboard.modals.warning.title') }}</h5>
                <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div class="modal-body">{{ trans('dashboard.modals.warning.p1') }} <br />{{ trans('dashboard.modals.warning.p2') }}<br /><br />{{ trans('dashboard.modals.warning.p3') }}</div>
            <div class="modal-footer">
                <form id="warningForm" action="{{ route($side_route)  }}" method="{{ (!isset($side['method']) || is_null($side['method'])) ? 'post' : $side['method']  }}">
                    {{ csrf_field() }}

                    <button id="warningSubmit" type="submit" data-dismiss="" value="" class="btn btn-{{ (!isset($side['color']) || is_null($side['color'])) ? 'success' : $side['color'] }}">{{ trans('global.button.apply') }}</button>
                    <button class="btn btn-secondary" type="button" data-dismiss="modal">{{ trans('global.button.cancel') }}</button>
                </form>
            </div>
        </div>
    </div>
</div>



