
@section('footer')

<footer class="sticky-footer">
    <div class="container-fluid">
        <div class="text-center">
            <small>Copyright © <a title="XPipe" style="color: inherit;" href="https://xpipe.sindria.org">XPipe</a> <?= date('Y'); ?></small>
            <small class="ml-auto" style="float: right; margin-right: 5px;">version {{ app_version() }}</small>
        </div>
    </div>
</footer>

{{-- Scroll to Top Button--}}
<a class="scroll-to-top rounded" href="#page-top">
	<i class="fa fa-angle-up"></i>
</a>

@include('components.modals.logout')
@include('components.modals.loader-top')
@include('components.modals.loader')

@endsection
