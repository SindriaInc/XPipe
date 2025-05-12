{{-- Upload Modal--}}
<div class="modal fade" id="uploadModal" tabindex="-1" role="dialog" aria-labelledby="uploadModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="uploadModalLabel">{{ trans('dashboard.modals.upload.title') }}</h5>
                <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <form id="uploadForm" action="{{ route($side_route)  }}" method="{{ (!isset($side['method']) || is_null($side['method'])) ? 'post' : $side['method']  }}" enctype="multipart/form-data">
                {{ csrf_field() }}

                <div class="modal-body">

                    <div class="form-group">
                        <input type="file" class="form-control-file" name="file">
                        <small class="form-text text-muted">File CSV (MAX 10MB)</small>
                    </div>

                </div>

                <div class="modal-footer">
                    <button id="uploadSubmit" type="submit" class="btn btn-{{ (!isset($side['color']) || is_null($side['color'])) ? 'success' : $side['color'] }}" name="submit">{{ trans('global.button.apply') }}</button>
                    <button class="btn btn-secondary" type="button" data-dismiss="modal">{{ trans('global.button.cancel') }}</button>
                </div>
            </form>
        </div>
    </div>
</div>

