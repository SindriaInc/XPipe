@extends('layouts.skeleton')

@section('title', 'User Detail')

@section('content')

    <div class="jumbotron">
        <h1 class="display-1 text-center">{{ $u->username }}</h1>

        @include('components.messages')

        <div class="card-body">
            <div class="row">
                <div class="table-responsive col-12">
                    <nav class="navbar navbar-expand-lg navbar-light rounded">
                        <h3>{{ __('dashboard.users.field.info') }}</h3>
                    </nav>

                    <table class="table table-hover table-light">
                        <thead>

                        </thead>

                        <tbody>
                        <tr>
                            <th scope="col">{{ __('dashboard.users.field.id') }}</th>
                            <td>{{ $u->id }}</td>
                        </tr>
                        <tr>
                            <th scope="col">{{ __('dashboard.users.field.name') }}</th>
                            <td>{{ $u->firstName }}</td>
                        </tr>
                        <tr>
                            <th scope="col">{{ __('dashboard.users.field.surname') }}</th>
                            <td>{{ $u->lastName }}</td>
                        </tr>
                        <tr>
                            <th scope="col">{{ __('dashboard.users.field.username') }}</th>
                            <td>{{ $u->username }}</td>
                        </tr>
                        <tr>
                            <th scope="col">{{ __('dashboard.users.field.email') }}</th>
                            <td>{{ $u->email }}</td>
                        </tr>
                        <tr>
                            <th scope="col">{{ __('dashboard.users.field.status') }}</th>
                            <td>{{ $u->enabled ? trans('dashboard.users.field.status.true') : trans('dashboard.users.field.status.false') }}</td>
                        </tr>
                        <tr>
                            <th scope="col">{{ __('dashboard.users.field.email_verified') }}</th>
                            <td>{{ $u->emailVerified ? trans('dashboard.users.field.email_verified.true') : trans('dashboard.users.field.email_verified.false') }}</td>
                        </tr>
                        <tr>
                            <th scope="col">{{ __('dashboard.users.field.created_at') }}</th>
                            <td>{{--formatted_date($u->createdTimestamp)--}}</td>
                        </tr>
                        </tbody>
                    </table>

                </div>

            </div>


            <nav class="navbar navbar-expand-lg navbar-light rounded">
                <h1>{{ __('dashboard.users.policies.title') }}</h1>
            </nav>

            <nav class="navbar navbar-expand-lg navbar-light bg-light">
                <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarSupportedContent">

                    <ul class="navbar-nav mr-auto">

{{--                        <li class="nav-item">--}}
{{--                            <form class="form-inline my-2 my-lg-0" action="--}}{{--route('dashboard.users.subscribers.search')--}}{{--" method="post">--}}
{{--                                {{ csrf_field() }}--}}

{{--                                <div class="input-group">--}}
{{--                                    <input class="form-control mr-sm-2" type="search" placeholder="{{ __('dashboard.users.policies.search.placeholder') }}" aria-label="Search" name="query" value="--}}{{--$current_query--}}{{--">--}}
{{--                                    <button class="btn btn-outline-success my-2 my-sm-0" type="submit">--}}
{{--                                        <i class="fa fa-fw fa-search"></i>--}}
{{--                                    </button>--}}
{{--                                </div>--}}

{{--                            </form>--}}
{{--                        </li>--}}

                    </ul>


                    <ul class="navbar-nav ml-auto">

{{--                        <li class="nav-item global-activities">--}}
{{--                            <a  href="--}}{{--env('FRONTEND_URL') . "/api/v1/users/subscribers/export?t=" . $u->id--}}{{--">--}}
{{--                                <button type="button" class="btn btn-small btn-outline-info" name="export">--}}
{{--                                    <i class="fa fa-fw fa-download"></i>--}}
{{--                                </button>--}}
{{--                            </a>--}}
{{--                        </li>--}}

                    </ul>
                </div>
            </nav>


            <div class="table-responsive">
                <table class="table table-hover table-light">
                    <thead>
                    <tr>
                        <th scope="col">{{ __('dashboard.users.policies.field.name') }}</th>
                        <th scope="col">{{ __('global.text.activities') }}</th>
                    </tr>
                    </thead>

                    <tbody>
                    @foreach($policies as $policy)
                        <tr>
                            <td>{{ $policy->name }}</td>
                            <td>

                                <a href="{{ route('dashboard.policies.details', $policy->id) }}">
                                    <button type="submit" class="btn btn-small btn-logo-secondary" name="details">
                                        <i class="fa fa-fw fa-info"></i>
                                    </button>
                                </a>

                                <a data-toggle="modal" data-target="{{ "#deleteModal_".$policy->id }}" href="#">
                                    <button type="button" class="btn btn-small btn-danger" name="delete">
                                        <i class="fa fa-fw fa-user-times"></i>
                                    </button>
                                </a>
                                @include('components.modals.policies.detach')

                            </td>
                        </tr>
                    @endforeach

                    </tbody>
                </table>

            </div>


            <div class="pagination juserstify-content-center">
                {{--$subscribers->links("pagination::bootstrap-4")--}}
            </div>


            <button type="reset" class="btn btn-main" onclick="window.location.href = '{{ route('dashboard.users')}}'">
                <i class="fa fa-fw fa-chevron-left"></i>
            </button>
        </div>
    </div>
@endsection
