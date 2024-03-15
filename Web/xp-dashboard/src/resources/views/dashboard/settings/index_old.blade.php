@extends('layouts.skeleton')

@section('title', 'Settings')

@section('content')
    <div class="jumbotron">
        <h1 class="display-1 text-center">{{ __('dashboard.settings.title') }}</h1>

        <div class="container">
            <div class="card card-custom">
                <div class="card-body">
                    @include('components.messages')
                    @include('components.forms.settings.edit')
                </div>
            </div>
        </div>
    </div>
@endsection
