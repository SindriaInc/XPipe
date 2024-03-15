@extends('layouts.master')
@extends('layouts.sidebar')
@extends('layouts.footer')

@section('master-content')
    <div class="content-wrapper">
        @yield('content')
    </div>
@endsection
