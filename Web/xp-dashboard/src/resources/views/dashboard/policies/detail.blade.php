@extends('layouts.skeleton')

@section('title', 'Policy Detail')

@section('content')

    <div class="jumbotron">
        <h1 class="display-1 text-center">{{ $policy->name }}</h1>

        @include('components.messages')

        <div class="card-body">
            <div class="row">
                <div class="table-responsive col-12">
                    <nav class="navbar navbar-expand-lg navbar-light rounded">
                        <h3>{{ __('dashboard.policies.field.info') }}</h3>
                    </nav>

                    <table class="table table-hover table-light">
                        <thead>

                        </thead>

                        <tbody>
                        <tr>
                            <th scope="col">{{ __('dashboard.policies.field.id') }}</th>
                            <td>{{ $policy->id }}</td>
                        </tr>
                        <tr>
                            <th scope="col">{{ __('dashboard.policies.field.name') }}</th>
                            <td>{{ $policy->name }}</td>
                        </tr>
                        <tr>
                            <th scope="col">{{ __('dashboard.policies.field.type') }}</th>
                            <td>{{ $policy->type->name }}</td>
                        </tr>
                        <tr>
                            <th scope="col">{{ __('dashboard.policies.field.content') }}</th>
                            <td><pre>{{ $policy->content }}</pre></td>
                        </tr>
                        <tr>
                            <th scope="col">{{ __('dashboard.policies.field.created_at') }}</th>
                            <td>{{--formatted_date($u->createdTimestamp)--}}</td>
                        </tr>
                        </tbody>
                    </table>

                </div>

            </div>


            <nav class="navbar navbar-expand-lg navbar-light rounded">
                <h1>{{ __('dashboard.policies.users.title') }}</h1>
            </nav>

            <nav class="navbar navbar-expand-lg navbar-light bg-light">
                <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarSupportedContent">

                    <ul class="navbar-nav mr-auto">

{{--                        <li class="nav-item">--}}
{{--                            <form class="form-inline my-2 my-lg-0" action="--}}{{--route('dashboard.policies.subscribers.search')--}}{{--" method="post">--}}
{{--                                {{ csrf_field() }}--}}

{{--                                <div class="input-group">--}}
{{--                                    <input class="form-control mr-sm-2" type="search" placeholder="{{ __('dashboard.policies.subscribers.search.placeholder') }}" aria-label="Search" name="query" value="--}}{{--$current_query--}}{{--">--}}
{{--                                    <button class="btn btn-outline-success my-2 my-sm-0" type="submit">--}}
{{--                                        <i class="fa fa-fw fa-search"></i>--}}
{{--                                    </button>--}}
{{--                                </div>--}}

{{--                            </form>--}}
{{--                        </li>--}}

                    </ul>


                    <ul class="navbar-nav ml-auto">

{{--                        <li class="nav-item global-activities">--}}
{{--                            <a  href="--}}{{--env('FRONTEND_URL') . "/api/v1/policies/subscribers/export?t=" . $u->id--}}{{--">--}}
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
                        <th scope="col">{{ __('dashboard.policies.users.field.id') }}</th>
                        <th scope="col">{{ __('dashboard.policies.users.field.username') }}</th>
                        <th scope="col">{{ __('global.text.activities') }}</th>
                    </tr>
                    </thead>

                    <tbody>
                    @foreach($users as $u)
                        <tr>
                            <td>{{ $u->id }}</td>
                            <td>{{ $u->username }}</td>
                            <td>

                                <a href="{{ route('dashboard.users.details', $u->username) }}">
                                    <button type="submit" class="btn btn-small btn-logo-secondary" name="details">
                                        <i class="fa fa-fw fa-info"></i>
                                    </button>
                                </a>

                            </td>
                        </tr>
                    @endforeach

                    </tbody>
                </table>

            </div>


            <div class="pagination jpoliciestify-content-center">
                {{--$subscribers->links("pagination::bootstrap-4")--}}
            </div>


            <button type="reset" class="btn btn-main" onclick="window.location.href = '{{ route('dashboard.policies')}}'">
                <i class="fa fa-fw fa-chevron-left"></i>
            </button>
        </div>
    </div>
@endsection
