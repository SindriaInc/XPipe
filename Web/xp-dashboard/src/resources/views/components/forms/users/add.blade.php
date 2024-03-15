<form action="{{ route('dashboard.users.store') }}" method="post" enctype="multipart/form-data">

    {{ csrf_field() }}


    <div class="row justify-content-md-center mt-5">

        <div class="col-12">
            <div class="form-group">
                <label for="name">*{{ __('dashboard.users.field.name') }}</label>
                <input type="text" class="form-control" placeholder="{{ trans('dashboard.users.field.name') }}" name="name" value="{{ old('name') }}" autocapitalize="words" required>
            </div>
        </div>

        <div class="col-12">
            <div class="form-group">
                <label for="surname">*{{ __('dashboard.users.field.surname') }}</label>
                <input type="text" class="form-control" placeholder="{{ trans('dashboard.users.field.surname') }}" name="surname" value="{{ old('surname') }}" autocapitalize="words" required>
            </div>
        </div>


        <div class="col-12">
            <div class="form-group">
                <label for="email">*{{ __('dashboard.users.field.email') }}</label>
                <input type="email" class="form-control" placeholder="{{ trans('dashboard.users.field.email') }}" name="email" value="{{ old('email') }}" autocapitalize="words" required>
            </div>
        </div>


        <div class="col-12">
            <div class="form-group">
                <label for="username">*{{ __('dashboard.users.field.username') }}</label>
                <input type="text" class="form-control" placeholder="{{ trans('dashboard.users.field.username') }}" name="username" value="{{ old('username') }}" autocapitalize="words" required>
            </div>
        </div>

        <div class="col-12">
            <div class="form-group">
                <label for="enabled">{{ trans('dashboard.users.field.status') }}</label>
                <select class="form-control" name="enabled" required>
                        <option value="0" selected>{{ trans('dashboard.users.field.status.false') }}</option>
                        <option value="1">{{ trans('dashboard.users.field.status.true') }}</option>
                </select>
            </div>
        </div>


    </div>


    <div class="row">
        <div class="col-12">
            <button type="submit" class="btn btn-primary btn-custom" name="submit">{{ __('global.button.save')  }}</button>
            <button type="reset" class="btn btn-secondary btn-custom" onclick="window.location.href = '{{ route('dashboard.users')}}'">{{ __('global.button.cancel') }}</button>
        </div>
    </div>

</form>
