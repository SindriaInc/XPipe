@extends('layouts.skeleton')

@section('title', 'Pagine')

@section('content')

    {{-- 1:1 aspect ratio --}}
    <div class="embed-responsive embed-responsive-1by1">
        <iframe class="embed-responsive-item" frameborder="0" src="{{ pages_url('/wp-login.php') }}"></iframe>
        <iframe class="embed-responsive-item" frameborder="0" src="{{ pages_url('/wp-admin/edit.php?post_type=page') }}"></iframe>
    </div>


    @push('scripts')
        <script type="text/javascript">


            /**
             * Execute
             */
            $(document).ready(function() {


            });



        </script>
    @endpush


@endsection
