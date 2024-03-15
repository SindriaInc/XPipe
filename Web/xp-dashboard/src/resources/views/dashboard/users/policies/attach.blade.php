@extends('layouts.skeleton')

@section('title', 'Attach Policy')

@section('content')

    <div class="jumbotron">
        <h1 class="display-1 text-center">{{ __('dashboard.tournaments.subscribers.show') }}</h1>

        <div class="container">
            <div class="card card-custom">
                <div class="card-body">
                    @include('components.messages')
                    @include('components.forms.tournaments.subscribers.edit')
                </div>
            </div>
        </div>
    </div>

@endsection
