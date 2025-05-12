@extends('layouts.skeleton')

@section('title', 'Dashboard')

@section('content')

    <div class="jumbotron">
        <h1 class="display-1 text-center">{{ __('dashboard.dashboard.title') }}</h1>

        <div class="card-body text-center">

            <pipeline-log-component></pipeline-log-component>

            @include('components.messages')

            @include('components.widgets.dashboard.main')

{{--            @include('components.widgets.dashboard.summary')--}}

        </div>
    </div>

@endsection
