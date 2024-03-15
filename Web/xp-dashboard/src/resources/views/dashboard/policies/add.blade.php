@extends('layouts.skeleton')

@section('title', 'Add Policy')

@section('content')

    <div class="jumbotron">
        <h1 class="display-1 text-center">{{ __('dashboard.policies.add') }}</h1>

        <div class="container">
            <div class="card card-custom">
                <div class="card-body">
                    @include('components.forms.policies.add')
                </div>
            </div>
        </div>
    </div>

@endsection
