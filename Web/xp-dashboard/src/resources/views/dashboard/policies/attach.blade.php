@extends('layouts.skeleton')

@section('title', 'Attach Policy')

@section('content')

    <div class="jumbotron">
        <h1 class="display-1 text-center">{{ __('dashboard.policies.attach') }}</h1>

        <div class="container">
            <div class="card card-custom">
                <div class="card-body">
                    @include('components.forms.policies.attach')
                </div>
            </div>
        </div>
    </div>

@endsection
