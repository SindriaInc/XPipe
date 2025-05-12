<!DOCTYPE html>
<html lang="{{ app()->getLocale() }}">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    {{-- CSRF Token --}}
    <meta name="csrf-token" content="{{ csrf_token() }}">
    {{-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> --}}

    <title>@if ($__env->yieldContent('title') != "") @yield('title') @else {{ "Dashboard" }} @endif | {{ config('app.name') }} </title>

    {{-- Favicon  --}}
{{--    <link rel="shortcut icon" href="{{ url('/img/favicon.ico') }}">--}}

    <link href="{{ url('/img/apple-icon-57x57.png') }}" rel="apple-touch-icon" sizes="57x57" />
    <link href="{{ url('/img/apple-icon-60x60.png') }}" rel="apple-touch-icon" sizes="60x60" />
    <link href="{{ url('/img/apple-icon-72x72.png') }}" rel="apple-touch-icon" sizes="72x72" />
    <link href="{{ url('/img/apple-icon-76x76.png') }}" rel="apple-touch-icon" sizes="76x76" />
    <link href="{{ url('/img/apple-icon-114x114.png') }}" rel="apple-touch-icon" sizes="114x114" />
    <link href="{{ url('/img/apple-icon-120x120.png') }}" rel="apple-touch-icon" sizes="120x120" />
    <link href="{{ url('/img/apple-icon-144x144.png') }}" rel="apple-touch-icon" sizes="144x144" />
    <link href="{{ url('/img/apple-icon-152x152.png') }}" rel="apple-touch-icon" sizes="152x152" />
    <link href="{{ url('/img/apple-icon-180x180.png') }}" rel="apple-touch-icon" sizes="180x180" />
    <link href="{{ url('/img/android-icon-192x192.png') }}" rel="icon" type="image/png" sizes="192x192" />
    <link href="{{ url('/img/favicon-32x32.png') }}" rel="icon" type="image/png" sizes="32x32" />
    <link href="{{ url('/img/favicon-96x96.png') }}" rel="icon" type="image/png" sizes="96x96" />
    <link href="{{ url('/img/favicon-16x16.png') }}" rel="icon" type="image/png" sizes="16x16" />
    <link href="{{ url('/img/manifest.json') }}" rel="manifest"  />

    <meta name="msapplication-TileColor" content="#ffffff" />
    <meta name="msapplication-TileImage" content="/img/ms-icon-144x144.png" />
    <meta name="theme-color" content="#ffffff" />

    {{-- Styles --}}
    <link href="{{ asset('css/dashboard.css') }}" rel="stylesheet">

</head>

<body class="fixed-nav sticky-footer bg-custom {{ session('toggled') ? " sidenav-toggled" : "" }}" id="page-top">

{{-- Vue 2.x wrapper --}}
<div id="app">

{{-- Navigation --}}
<nav class="navbar navbar-expand-lg navbar-light bg-custom fixed-top" id="mainNav">
  <span id="brand">
      <a class="navbar-brand nav-link-custom ml-4" href="{{ url('/') }}" title="{{ config('app.name') }}">
          <img src="{{ url('/') }}/img/logo.png" style="height: 35px;" alt="{{ config('app.name') }}">
          {{ config('app.name') }}
      </a>
  </span>
  <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
  </button>


  @yield('sidebar')


    {{-- Right Side Of Navbar --}}
    <ul class="nav navbar-nav ml-auto">
    {{-- Authentication Links --}}
      @if (!isset($user))
          {{-- <li class="nav-item"><a class="nnav-link nav-link-custom" href="{{ route('login') }}">Login</a></li> --}}
          {{-- <li class="nav-item"><a class="nav-link" href="{{ route('register') }}">Register</a></li> --}}
        @else

            <div class="row">

                <li id="xdev" class="nav-item dropdown no-arrow">
                    <a class="nav-link dropdown-toggle profile-dropdown" href="#" id="userDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <span id="xdev">Xdev</span>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userDropdown">

                        <a class="dropdown-item dropdown-item-custom" href="{{ route('dashboard.xdev.gui') }}">
                            <i class="fa fa-fw fa-code"></i> GUI
                        </a>

                        <div class="dropdown-divider"></div>

                        <a class="dropdown-item dropdown-item-custom" href="{{ route('dashboard.xdev.cli') }}">
                            <i class="fa fa-fw fa-terminal"></i> CLI
                        </a>

                    </div>
                </li>

                <li id="language" class="nav-item dropdown no-arrow mx-1 language-dropdown">

                    <form action="{{ route('locale') }}" method="post">
                      {{ csrf_field() }}

                      <div class="input-group">

                          <select onchange="this.form.submit()" class="dropdown-toggle language-select" name="lang">
                              @foreach (config('app.locales') as $availableLocale)
                                  <option class="dropdown-item language-option" value="{{ $availableLocale }}"{{ session('locale')  == $availableLocale ? " selected" : "" }}>{{ strtoupper($availableLocale) }}</option>
                              @endforeach
                          </select>

                      </div>
                    </form>

                </li>

                <li id="user" class="nav-item dropdown no-arrow">
                  <a class="nav-link dropdown-toggle profile-dropdown" href="#" id="userDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                      <i class="fa fa-user-circle fa-fw"></i> <span id="username">{{ $user->introspect->preferred_username }}</span>
                  </a>
                  <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userDropdown">
                      <a class="dropdown-item dropdown-item-custom" href="{{ frontend_url('/') }}">
                          <i class="fa fa-fw fa-home"></i>Home
                      </a>

                      {{--<a class="dropdown-item dropdown-item-custom" href="{{ route('dashboard.settings') }}">
                          <i class="fa fa-fw fa-cog"></i>{{ trans('backend.settings.title') }}
                      </a>--}}

                      <div class="dropdown-divider"></div>
                      <a class="dropdown-item dropdown-item-custom" data-toggle="modal" data-target="#logoutModal" href="#">
                          <i class="fa fa-fw fa-sign-out"></i>Logout
                      </a>
                  </div>
                </li>

            </div>

        @endif
      </ul>

  </nav>

  @section('master-content')
  @show

  @section('footer')
  @show

  {{-- END Vue 2.x wrapper --}}
  </div>


  {{-- Scripts --}}
  <script src="{{ asset('js/dashboard.js') }}"></script>
  @stack('scripts')


   @section('body-end')
   @show


</body>
</html>
