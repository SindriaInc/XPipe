@extends('layouts.skeleton')

@section('title', 'Settings')

@section('content')


    <div class="jumbotron">
        <h1 class="display-1 text-center">{{ __('dashboard.settings.title') }}</h1>

        @include('components.messages')

        <chat-component></chat-component>

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
