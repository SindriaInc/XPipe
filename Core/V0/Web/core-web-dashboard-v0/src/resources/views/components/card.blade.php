<div class="col-xl-4 col-sm-6 mb-3">
    <div class="card text-white bg-{{ (!isset($side['color']) || is_null($side['color'])) ? 'success' : $side['color'] }} o-hidden h-100">
        <div class="card-body">
            <div class="card-body-icon">
                <i class="fa fa-fw fa-{{ isset($side['icon']) ? $side['icon'] : 'link' }}"></i>
            </div>
            <div class="mr-5">{{ trans('backend'.".".$side['key']) }}</div>
        </div>
        <a class="card-footer text-white clearfix small z-1" data-toggle="{{ (!isset($side['modal']) || is_null($side['modal'])) ? '' :  'modal' }}" data-target="{{ (!isset($side['modal']) || is_null($side['modal'])) ? '' :  $side['modal'] }}"  href="{{ (!isset($side['modal']) || is_null($side['modal'])) ? route($side_route) : '#'  }}" title="{{ trans('backend'.".".$side['key']) }}">
            <span class="float-left">Go</span>
            <span class="float-right">
                <i class="fa fa-angle-right"></i>
            </span>
        </a>
    </div>
</div>

