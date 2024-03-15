<form action="{{ route('dashboard.settings.edit') }}" method="post" enctype="multipart/form-data">

    {{ csrf_field() }}

    <script src="//cdn.tinymce.com/4/tinymce.min.js"></script>
    <script>tinymce.init({
            selector:'textarea',
            menubar: "edit format"
        });
    </script>

    <h3>
        {{ __('dashboard.settings.field.users') }}
    </h3>

    @foreach($settings as $setting)

        @if($setting->type == "yesno")
            <div class="form-group">
                <label for="{{ $setting->name }}">{{ trans('dashboard.settings.field.tournaments.subscriptions') }}</label>
                <select class="form-control" name="{{ $setting->name }}" required>
                    @foreach ($setting->options as $option)
                        <option value="{{ $option->value }}"{{ $option->value == $setting->value ? " selected" : ""}}>{{ $option->label }}</option>
                    @endforeach
                </select>
            </div>
        @endif

        @if($setting->type == "list")
            <div class="form-group">
                <label for="{{ $setting->name }}">{{ trans('dashboard.settings.field.tournaments.subscriptions') }}</label>
                <select class="form-control" name="{{ $setting->name }}" required>
                    <option value="{{ old($setting->name) }}" disabled selected>-- {{ __('backend.orders.customers.select') }} --</option>
                    @foreach ($setting->options as $option)
                        <option value="{{ $option->value }}"{{ $option->value == $setting->value ? " selected" : ""}}>{{ $option->label }}</option>
                    @endforeach
                </select>
            </div>
        @endif

        @if($setting->type == "string")
            <div class="row justify-content-md-center mt-5">
                <div class="col-12">
                    <div class="form-group">
                        <label for="{{ $setting->name }}">{{ trans('dashboard.settings.field.tournaments.subscriptions') }}</label>
                        <input type="text" class="form-control" placeholder="{{ trans('backend.doctors.field.name') }}" name="{{ $setting->name }}" value="{{ $setting->value }}" autocapitalize="words" required>
                    </div>
                </div>
            </div>
        @endif

        @if($setting->type == "content")
            <div class="row justify-content-md-center mt-5">
                <div class="col-12">
                    <div class="form-group">
                        <label for="{{ $setting->name }}">{{ trans('dashboard.settings.field.tournaments.subscriptions') }}</label>
                        <textarea class="form-control" placeholder="{{ __('backend.orders.field.note.optional') }}" name="{{ $setting->name }}" autocapitalize="none">{{ $setting->value }}</textarea>
                    </div>
                </div>
            </div>
        @endif


    @endforeach

    <div class="row">
        <div class="col-12">
            <button type="submit" class="btn btn-primary btn-custom" name="submit">{{ __('global.button.save')  }}</button>
            <button type="reset" class="btn btn-secondary btn-custom" onclick="window.location.href = '{{ route('dashboard')}}'">{{ __('global.button.cancel') }}</button>
        </div>
    </div>

</form>
