<form action="{{ route('dashboard.tournaments.subscribers.edit', $subscriber->id) }}" method="post" enctype="multipart/form-data">

    {{ csrf_field() }}


    {{-- Set current tournament as hidden input - In futuro questo potra' essere sotituito da una select con la lista dei tornei --}}
    <input type="hidden" value="{{ $subscriber->tournament_id }}" name="tournament_id" required>

    <div class="row justify-content-md-center mt-5">
        <div class="col-12">
            <div class="form-group">
                <label for="name">*{{ __('dashboard.tournaments.subscribers.field.name') }}</label>
                <input type="text" class="form-control" placeholder="{{ trans('dashboard.tournaments.subscribers.field.name') }}" name="name" value="{{ $subscriber->name  }}" autocapitalize="words" required>
            </div>
        </div>
    </div>


    <div class="row justify-content-md-center mt-5">
        <div class="col-12">
            <div class="form-group">
                <label for="surname">*{{ __('dashboard.tournaments.subscribers.field.surname') }}</label>
                <input type="text" class="form-control" placeholder="{{ trans('dashboard.tournaments.subscribers.field.name') }}" name="surname" value="{{ $subscriber->surname  }}" autocapitalize="words" required>
            </div>
        </div>
    </div>


    <div class="row justify-content-md-center mt-5">
        <div class="col-12">
            <div class="form-group">
                <label for="birthday">*{{ __('dashboard.tournaments.subscribers.field.birthday') }}</label>
                <input type="date" class="form-control" placeholder="GG/MM/AAAA" name="birthday" value="{{ $subscriber->birthday  }}" required>
            </div>
        </div>
    </div>


    <div class="row justify-content-md-center mt-5">
        <div class="col-12">
            <div class="form-group">
                <label for="email">*{{ __('dashboard.tournaments.subscribers.field.email') }}</label>
                <input type="email" class="form-control" placeholder="Email" name="email" value="{{ $subscriber->email  }}" autocapitalize="none" required>
            </div>
        </div>
    </div>


    <div class="row justify-content-md-center mt-5">
        <div class="col-12">
            <div class="form-group">
                <label for="phone">*{{ __('dashboard.tournaments.subscribers.field.phone') }}</label>
                <input type="number" class="form-control" placeholder="{{ trans('dashboard.tournaments.subscribers.field.phone') }}" name="phone" value="{{ $subscriber->phone  }}" required>
            </div>
        </div>
    </div>


    <div class="row justify-content-md-center mt-5">
        <div class="col-12">
            <div class="form-group">
                <label for="fit">*{{ __('dashboard.tournaments.subscribers.field.fit') }}</label>
                <input type="number" class="form-control" placeholder="{{ trans('dashboard.tournaments.subscribers.field.fit') }}" name="fit" value="{{ $subscriber->fit  }}" required>
            </div>
        </div>
    </div>



    <div class="row justify-content-md-center mt-5">
        <div class="col-12">
            <div class="form-group">
                <label for="club">*{{ __('dashboard.tournaments.subscribers.field.club') }}</label>
                <input type="text" class="form-control" placeholder="{{ trans('dashboard.tournaments.subscribers.field.club') }}" name="club" value="{{ $subscriber->club  }}" autocapitalize="words" required>
            </div>
        </div>
    </div>


    <div class="row justify-content-md-center mt-5">
        <div class="col-12">
            <div class="form-group">
                <label for="score_id">*{{ __('dashboard.tournaments.subscribers.field.score') }}</label>
                <select class="form-control" name="score_id" required>
                    @foreach ($scores as $score)
                        <option value="{{ $score->id }}"{{ $score->id == $subscriber->score_id ? " selected" : ""}}>{{ $score->name }}</option>
                    @endforeach
                </select>
            </div>
        </div>
    </div>


    <div class="row justify-content-md-center mt-5">
        <div class="col-12">
            <div class="form-group">
                <label for="category_id">*{{ __('dashboard.tournaments.subscribers.field.category') }}</label>
                <select class="form-control" name="category_id" required>
                    @foreach ($categories as $category)
                        <option value="{{ $category->id }}"{{ $category->id == $subscriber->category_id ? " selected" : ""}}>{{ $category->name }}</option>
                    @endforeach
                </select>
            </div>
        </div>
    </div>


    <div class="row justify-content-md-center mt-5">
        <div class="col-12">
            <div class="form-group">
                <label for="type_id">*{{ __('dashboard.tournaments.subscribers.field.type') }}</label>
                <select class="form-control" name="type_id" required>
                    @foreach ($types as $type)
                        <option value="{{ $type->id }}"{{ $type->id == $subscriber->type_id ? " selected" : ""}}>{{ $type->name }}</option>
                    @endforeach
                </select>
            </div>
        </div>
    </div>


    <div class="row justify-content-md-center mt-5">
        <div class="col-12">
            <div class="form-group">
                <label for="note">{{ __('dashboard.tournaments.subscribers.field.note') }}</label>
                <textarea type="text" class="form-control" placeholder="{{ trans('dashboard.tournaments.subscribers.field.note') }}" name="note" autocapitalize="words">{{ $subscriber->note  }}</textarea>
            </div>
        </div>
    </div>


    <div class="row">
        <div class="col-12">
            <button type="submit" class="btn btn-primary btn-custom" name="submit">{{ __('global.button.save')  }}</button>
            <button type="reset" class="btn btn-secondary btn-custom" onclick="window.location.href = '{{ route('dashboard.tournaments.details', $subscriber->tournament_id)}}'">{{ __('global.button.cancel') }}</button>
        </div>
    </div>

</form>
