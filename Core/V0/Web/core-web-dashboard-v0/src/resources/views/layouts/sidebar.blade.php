
@section('sidebar')

<div class="collapse navbar-collapse" id="navbarResponsive">
    <ul class="navbar-nav navbar-sidenav" id="exampleAccordion">

        {{-- Show route for guest user --}}
        @if (!isset($user))
            <li class="nav-item" data-toggle="tooltip" data-placement="right" title="{{ trans('dashboard.sidebar.back') }}">
                <a class="nav-link" href="{{ frontend_url('/') }}">
                    <i class="fa fa-fw fa-home"></i>
                    <span class="nav-link-text ml-4">{{ trans('dashboard.sidebar.back') }}</span>
                </a>
            </li>
        @endif

        {{-- Show route for auth user --}}
        @if(isset($user))
            @foreach ($sidebar as $side_route => $side)
                <li class="nav-item{{ Route::currentRouteName() == $side_route ? " active" : "" }}" data-toggle="tooltip" data-placement="right" title="{{ trans('dashboard'.".".$side['key']) }}">
                    <a class="nav-link" href="{{ route($side_route) }}">
                        <i class="fa fa-fw fa-{{ isset($side['icon']) ? $side['icon'] : 'link' }}"></i>
                        <span class="nav-link-text ml-4">{{ trans('dashboard'.".".$side['key']) }}</span>
                    </a>
                </li>
            @endforeach

            {{-- Show route only for admin --}}
{{--            @if($user->privilege_id == 1)--}}
{{--                @foreach ($sidebar_admin as $side_route => $side)--}}
{{--                    <li class="nav-item{{ Route::currentRouteName() == $side_route ? " active" : "" }}" data-toggle="tooltip" data-placement="right" title="{{ trans('dashboard'.".".$side['key']) }}">--}}
{{--                        <a class="nav-link" href="{{ route($side_route) }}">--}}
{{--                            <i class="fa fa-fw fa-{{ isset($side['icon']) ? $side['icon'] : 'link' }}"></i>--}}
{{--                            <span class="nav-link-text ml-4">{{ trans('dashboard'.".".$side['key']) }}</span>--}}
{{--                        </a>--}}
{{--                    </li>--}}
{{--                @endforeach--}}
{{--            @endif--}}

        @endif
    </ul>

    <ul class="navbar-nav sidenav-toggler">
        <li class="nav-item">
            <a class="nav-link text-center" id="sidenavToggler">
                <i class="fa fa-fw fa-angle-left"></i>
            </a>
        </li>
    </ul>
</div>

@endsection
