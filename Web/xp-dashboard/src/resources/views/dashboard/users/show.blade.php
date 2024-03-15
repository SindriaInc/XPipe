@extends('layouts.skeleton')

@section('title', 'Edit User')

@section('content')

    <div class="jumbotron">
        <h1 class="display-1 text-center">{{ __('dashboard.users.edit') }}</h1>

        <div class="container">
            <div class="card card-custom">
                <div class="card-body">
                    @include('components.forms.users.edit')
                </div>
            </div>
        </div>
    </div>

@endsection
