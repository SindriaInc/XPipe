@extends('layouts.skeleton')

@section('title', 'Edit Pipeline')

@section('content')

    <div class="jumbotron">
        <h1 class="display-1 text-center">{{ __('dashboard.pipelines.edit') }}</h1>

        <div class="container">
            <div class="card card-custom">
                <div class="card-body">
                    @include('components.forms.pipelines.edit')
                </div>
            </div>
        </div>
    </div>

@endsection
