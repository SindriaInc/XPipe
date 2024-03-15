<div class="card card-icon mb-4">
    <div class="row">
        <div class="col-12">
            <div class="card-body py-5">
                <h5 class="card-title">{{ __('dashboard.widgets.main.welcome') }}, {{ $user->introspect->preferred_username }}</h5>
                <p class="card-text">{{ __('dashboard.widgets.main.text') }}</p>
                <a class="btn btn-main btn-sm" href="https://www.ossec.net/download-ossec/" target="_blank">
                    <i class="fa fa-download"></i>
                    {{ __('dashboard.widgets.main.download') }}
                </a>
            </div>
        </div>
    </div>
</div>
