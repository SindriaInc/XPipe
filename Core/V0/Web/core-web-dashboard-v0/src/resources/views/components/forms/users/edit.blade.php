<form action="{{ route('dashboard.users.edit', $u->id) }}" method="post" enctype="multipart/form-data">

    {{ csrf_field() }}


    <div class="row justify-content-md-center mt-5">

        <div class="col-12">
            <div class="form-group">
                <label for="name">*{{ __('dashboard.users.field.name') }}</label>
                <input type="text" class="form-control" placeholder="{{ trans('dashboard.users.field.name') }}" name="name" value="{{ $u->firstName }}" autocapitalize="words" required>
            </div>
        </div>

        <div class="col-12">
            <div class="form-group">
                <label for="surname">*{{ __('dashboard.users.field.surname') }}</label>
                <input type="text" class="form-control" placeholder="{{ trans('dashboard.users.field.surname') }}" name="surname" value="{{ $u->lastName }}" autocapitalize="words" required>
            </div>
        </div>


        <div class="col-12">
            <div class="form-group">
                <label for="email">*{{ __('dashboard.users.field.email') }}</label>
                <input type="email" class="form-control" placeholder="{{ trans('dashboard.users.field.email') }}" name="email" value="{{ $u->email }}" autocapitalize="words" required>
            </div>
        </div>


        <div class="col-12">
            <div class="form-group">
                <label for="username">*{{ __('dashboard.users.field.username') }}</label>
                <input type="text" class="form-control" placeholder="{{ trans('dashboard.users.field.username') }}" name="username" value="{{ $u->username }}" autocapitalize="words" required>
            </div>
        </div>

        <div class="col-12">
            <div class="form-group">
                <label for="enabled">{{ trans('dashboard.users.field.status') }}</label>
                <select class="form-control" name="enabled" required>
                    <option value="0" {{ $u->enabled ? " selected" : "" }}>{{ trans('dashboard.users.field.status.false') }}</option>
                    <option value="1" {{ $u->enabled ? " selected" : "" }}>{{ trans('dashboard.users.field.status.true') }}</option>
                </select>
            </div>
        </div>

        <div class="col-12">
            <div class="form-group">
                <label for="email_verified">{{ trans('dashboard.users.field.email_verified') }}</label>
                <select class="form-control" name="email_verified" required>
                    <option value="0" {{ $u->emailVerified ? " selected" : "" }}>{{ trans('dashboard.users.field.email_verified.false') }}</option>
                    <option value="1" {{ $u->emailVerified ? " selected" : "" }}>{{ trans('dashboard.users.field.email_verified.true') }}</option>
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
