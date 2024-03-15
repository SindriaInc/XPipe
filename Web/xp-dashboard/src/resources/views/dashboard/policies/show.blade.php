@extends('layouts.skeleton')

@section('title', 'Edit Policy')

@section('content')

    <div class="jumbotron">
        <h1 class="display-1 text-center">{{ __('dashboard.policies.edit') }}</h1>

        <div class="container">
            <div class="card card-custom">
                <div class="card-body">
                    @include('components.forms.policies.edit')
                </div>
            </div>
        </div>
    </div>

@endsection
