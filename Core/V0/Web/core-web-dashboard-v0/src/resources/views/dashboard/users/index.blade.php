@extends('layouts.skeleton')

@section('title', 'Users')

@section('content')

    <div class="jumbotron">
        <h1 class="display-1 text-center">{{ __('dashboard.users.title') }}</h1>

        @include('components.messages')

        <div class="card-body">


            <nav class="navbar navbar-expand-lg navbar-light bg-light">
                <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarSupportedContent">

                    <ul class="navbar-nav mr-auto">

                        <li class="nav-item">
                            <form class="form-inline my-2 my-lg-0" action="{{ route('dashboard.users.search') }}" method="post">
                                {{ csrf_field() }}

                                <div class="input-group">
                                    <input class="form-control mr-sm-2" type="search" placeholder="{{ __('dashboard.users.search.placeholder') }}" aria-label="Search" name="query" value="{{ $current_query }}">
                                    <button class="btn btn-outline-main my-2 my-sm-0" type="submit">
                                        <i class="fa fa-fw fa-search"></i>
                                    </button>
                                </div>

                            </form>
                        </li>

                    </ul>


                    <ul class="navbar-nav ml-auto">

                        <li class="nav-item global-activities">
                            <a  href="{{ route('dashboard.users.add') }}">
                                <button type="button" class="btn btn-small btn-outline-main" name="add">
                                    <i class="fa fa-fw fa-plus"></i>
                                </button>
                            </a>
                        </li>

                        <li class="nav-item global-activities">
                            <a  href="{{ route('dashboard.users.export')  }}">
                                <button type="button" class="btn btn-small btn-outline-main" name="export">
                                    <i class="fa fa-fw fa-download"></i>
                                </button>
                            </a>
                        </li>

                    </ul>
                </div>
            </nav>


            <div class="table-responsive">
                <table class="table table-hover table-light">
                    <thead>
                    <tr>
                        <th scope="col">{{ __('dashboard.users.field.username') }}</th>
                        <th scope="col">{{ __('dashboard.users.field.name') }}</th>
                        <th scope="col">{{ __('dashboard.users.field.surname') }}</th>
                        <th scope="col">{{ __('dashboard.users.field.status') }}</th>
                        <th scope="col">{{ __('global.text.activities') }}</th>
                    </tr>
                    </thead>

                    <tbody>
                    @foreach($users as $u)
                        <tr>
                            <td>{{ $u->username }}</td>
                            <td>{{ $u->firstName }}</td>
                            <td>{{ $u->lastName }}</td>
                            <td>{{ formatted_status($u->enabled) }}</td>
                            <td>

                                <a href="{{ route('dashboard.users.details', $u->username) }}">
                                    <button type="submit" class="btn btn-small btn-logo-secondary" name="details">
                                        <i class="fa fa-fw fa-info"></i>
                                    </button>
                                </a>

                                <a href="{{ route('dashboard.users.show', $u->username) }}">
                                    <button type="button" class="btn btn-small btn-main" name="edit">
                                        <i class="fa fa-fw fa-edit"></i>
                                    </button>
                                </a>

                                <a data-toggle="modal" data-target="{{ "#deleteModal_".$u->id }}" href="#">
                                    <button type="button" class="btn btn-small btn-danger" name="delete">
                                        <i class="fa fa-fw fa-user-times"></i>
                                    </button>
                                </a>
                                @include('components.modals.users.delete')

                            </td>
                        </tr>
                    @endforeach

                    </tbody>
                </table>

            </div>


            <div class="pagination justify-content-center">
                {{--$u->links("pagination::bootstrap-4")--}}
            </div>

        </div>
    </div>
@endsection
