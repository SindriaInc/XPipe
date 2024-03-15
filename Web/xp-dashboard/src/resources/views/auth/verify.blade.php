@extends('layouts.app')

@section('content')
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">{{ __('verify.template.title') }}</div>

                <div class="card-body">
                    @if (session('resent'))
                        <div class="alert alert-success" role="alert">
                            {{ __('verify.template.resent') }}
                        </div>
                    @endif

                    {{ __('verify.template.content') }}
                    {{ __('verify.template.message') }}, <a href="{{ route('verification.resend') }}">{{ __('verify.template.message.link') }}</a>.
                </div>
            </div>
        </div>
    </div>
</div>
@endsection
