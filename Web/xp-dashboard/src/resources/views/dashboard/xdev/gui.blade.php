@extends('layouts.skeleton')

@section('title', 'Xdev GUI')

@section('content')


    <div class="jumbotron">

        <xdev-gui-component></xdev-gui-component>

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
