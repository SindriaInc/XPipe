@extends('layouts.skeleton')

@section('title', 'Policies')

@section('content')

    <div class="jumbotron">
        <h1 class="display-1 text-center">{{ __('dashboard.policies.title') }}</h1>

        @include('components.messages')

        <div class="card-body">


            <nav class="navbar navbar-expand-lg navbar-light bg-light">
                <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarSupportedContent">

                    <ul class="navbar-nav mr-auto">

                        <li class="nav-item">
                            <form class="form-inline my-2 my-lg-0" action="{{ route('dashboard.policies.search') }}" method="post">
                                {{ csrf_field() }}

                                <div class="input-group">
                                    <input class="form-control mr-sm-2" type="search" placeholder="{{ __('dashboard.policies.search.placeholder') }}" aria-label="Search" name="query" value="{{ $current_query }}">
                                    <button class="btn btn-outline-main my-2 my-sm-0" type="submit">
                                        <i class="fa fa-fw fa-search"></i>
                                    </button>
                                </div>

                            </form>
                        </li>

                    </ul>


                    <ul class="navbar-nav ml-auto">

                        <li class="nav-item global-activities">
                            <a  href="{{ route('dashboard.policies.attach') }}">
                                <button type="button" class="btn btn-small btn-outline-main" name="attach">
                                    <i class="fa fa-fw fa-user-plus"></i>
                                </button>
                            </a>
                        </li>

                        <li class="nav-item global-activities">
                            <a  href="{{ route('dashboard.policies.add') }}">
                                <button type="button" class="btn btn-small btn-outline-main" name="add">
                                    <i class="fa fa-fw fa-plus"></i>
                                </button>
                            </a>
                        </li>

                        <li class="nav-item global-activities">
                            <a  href="{{ route('dashboard.policies.export')  }}">
                                <button type="button" class="btn btn-small btn-outline-main" name="export">
                                    <i class="fa fa-fw fa-download"></i>
                                </button>
                            </a>
                        </li>

                    </ul>
                </div>
            </nav>


            @if(isset($policies->content))
            <div class="table-responsive">
                <table class="table table-hover table-light">
                    <thead>
                    <tr>
                        <th scope="col">{{ __('dashboard.policies.field.name') }}</th>
                        <th scope="col">{{ __('global.text.activities') }}</th>
                    </tr>
                    </thead>

                    <tbody>
                    @foreach($policies->content as $policy)
                        <tr>
                            <td>{{ $policy->name }}</td>
                            <td>

                                <a href="{{ route('dashboard.policies.details', $policy->id) }}">
                                    <button type="submit" class="btn btn-small btn-logo-secondary" name="details">
                                        <i class="fa fa-fw fa-info"></i>
                                    </button>
                                </a>

                                <a href="{{ route('dashboard.policies.show', $policy->id) }}">
                                    <button type="button" class="btn btn-small btn-main" name="edit">
                                        <i class="fa fa-fw fa-edit"></i>
                                    </button>
                                </a>

                                <a data-toggle="modal" data-target="{{ "#deleteModal_".$policy->id }}" href="#">
                                    <button type="button" class="btn btn-small btn-danger" name="delete">
                                        <i class="fa fa-fw fa-user-times"></i>
                                    </button>
                                </a>
                                @include('components.modals.policies.delete')

                            </td>
                        </tr>
                    @endforeach

                    </tbody>
                </table>
                @else
                    No result found
                @endif

            </div>

            @if(isset($policies->totalPages))
            <nav class="navbar navbar-expand-lg navbar-light bg-light">
                <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarSupportedContent">

                    @if($policies->totalPages > 1)
                    <ul class="navbar-nav mr-auto pagination justify-content-center">

                        @if(app('request')->input('page') == 1 || app('request')->input('page') == NULL)
                            <li class="page-item disabled"><a class="page-link" href="#">Previous</a></li>
                        @else
                            <li class="page-item"><a class="page-link" href="{{ route('dashboard.policies') . '?page=' . (app('request')->input('page') - 1) }}">Previous</a></li>
                        @endif

                        @for($i = 1; $i <= $policies->totalPages; $i++ )
                            <li class="page-item {{ app('request')->input('page')  == $i ? " active" : "" }}"><a class="page-link" href="{{ route('dashboard.policies') . '?page=' . $i }}">{{ $i }}</a></li>
                        @endfor

                        @if(app('request')->input('page') == $policies->totalPages)
                            <li class="page-item disabled"><a class="page-link" href="">Next</a></li>
                        @else
                            <li class="page-item"><a class="page-link" href="{{ route('dashboard.policies') . '?page=' . (app('request')->input('page') + 1) }}">Next</a></li>
                        @endif

                    </ul>
                    @endif


                    <ul class="navbar-nav ml-auto">

                        <li id="language" class="nav-item dropdown no-arrow mx-1 language-dropdown global-activities">

                            <form action="{{ route('pagination.quantity') }}" method="post">
                                {{ csrf_field() }}

                                <div class="input-group">

                                    <select onchange="this.form.submit()" class="dropdown-toggle quantity-select" name="quantity">
                                        @foreach (config('dashboard.pagination') as $availableValue)
                                            <option class="dropdown-item language-option" value="{{ $availableValue }}"{{ session('quantity')  == $availableValue ? " selected" : "" }}>{{ $availableValue }}</option>
                                        @endforeach
                                    </select>

                                </div>
                            </form>

                        </li>

                    </ul>
                </div>

            </nav>
            @endif


            <div class="pagination justify-content-center">
                {{--$policy->links("pagination::bootstrap-4")--}}
            </div>

        </div>
    </div>
@endsection
