<form action="{{ route('dashboard.policies.attach.store') }}" method="post" enctype="multipart/form-data">

    {{ csrf_field() }}


    <div class="row justify-content-md-center mt-5">

        <div class="col-12">
            <div class="form-group">
                <label for="user_id">*{{ __('dashboard.policies.attach.field.user_id') }}</label>
                <select class="form-control" name="user_id" required>
                    @foreach ($users as $u)
                        <option value="{{ $u->id }}">{{ $u->username }}</option>
                    @endforeach
                </select>
            </div>
        </div>

        <div class="col-12">
            <div class="form-group">
                <label for="policy_id">*{{ __('dashboard.policies.attach.field.policy_id') }}</label>
                <select class="form-control" name="policy_id" required>
                    @foreach ($policies as $policy)
                        <option value="{{ $policy->id }}">{{ $policy->name }}</option>
                    @endforeach
                </select>
            </div>
        </div>

    </div>


    <div class="row">
        <div class="col-12">
            <button type="submit" class="btn btn-primary btn-custom" name="submit">{{ __('global.button.save')  }}</button>
            <button type="reset" class="btn btn-secondary btn-custom" onclick="window.location.href = '{{ route('dashboard.policies')}}'">{{ __('global.button.cancel') }}</button>
        </div>
    </div>

</form>
