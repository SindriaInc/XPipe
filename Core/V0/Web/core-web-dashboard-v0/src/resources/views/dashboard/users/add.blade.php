@extends('layouts.skeleton')

@section('title', 'Add User')

@section('content')

    <div class="jumbotron">
        <h1 class="display-1 text-center">{{ __('dashboard.users.add') }}</h1>

        <div class="container">
            <div class="card card-custom">
                <div class="card-body">
                    @include('components.forms.users.add')
                </div>
            </div>
        </div>
    </div>

@endsection
