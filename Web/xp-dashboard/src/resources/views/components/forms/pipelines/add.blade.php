<form action="{{ route('dashboard.pipelines.store') }}" method="post" enctype="multipart/form-data">

    {{ csrf_field() }}


    <div class="row justify-content-md-center mt-5">

        <div class="col-12">
            <div class="form-group">
                <label for="name">*{{ __('dashboard.pipelines.field.name') }}</label>
                <input type="text" class="form-control" placeholder="{{ trans('dashboard.policies.field.name') }}" name="name" value="{{ old('name') }}" autocapitalize="words" required>
            </div>
        </div>


        <div class="col-12">
            <div class="form-group">
                <label for="content">*{{ __('dashboard.pipelines.field.content') }}</label>
                <textarea class="form-control" placeholder="" name="content" autocapitalize="none"></textarea>
                <div id="editor"></div>
            </div>
        </div>

    </div>


    <div class="row">
        <div class="col-12" style="margin-top: 350px;">
            <button type="submit" class="btn btn-primary btn-custom" name="submit">{{ __('global.button.save')  }}</button>
            <button type="reset" class="btn btn-secondary btn-custom" onclick="window.location.href = '{{ route('dashboard.pipelines')}}'">{{ __('global.button.cancel') }}</button>
        </div>
    </div>

</form>


<style type="text/css" media="screen">
    #editor {
        position: absolute;
        top: 0;
        right: 0;
        bottom: 0;
        left: 0;
        min-height: 300px;
    }
</style>


@push('scripts')
{{--    <script src="https://pagecdn.io/lib/ace/1.4.12/ace.min.js" crossorigin="anonymous" integrity="sha256-T5QdmsCQO5z8tBAXMrCZ4f3RX8wVdiA0Fu17FGnU1vU="></script>--}}
    <script src="https://cdnjs.cloudflare.com/ajax/libs/ace/1.12.5/ace.min.js" integrity="sha512-K7n6K4ZhcyKt/fNqbMfrWj1lj0/kwg2B0BoXnqWpyg3Pzr70hyVdCwQnaBXaVd1vyf6d174/vOuW+zdtuNv0xA==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script>
        var editor = ace.edit("editor");
        editor.setTheme("ace/theme/monokai");
        editor.session.setMode("ace/mode/json");

        var textarea = $('textarea[name="content"]').hide();
        editor.getSession().setValue(textarea.val());
        editor.getSession().on('change', function(){
            textarea.val(editor.getSession().getValue());
        });

    </script>
@endpush
