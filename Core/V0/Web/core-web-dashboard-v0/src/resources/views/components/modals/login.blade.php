{{-- Login Modal--}}
<div class="modal fade" id="loginModal" tabindex="-1" role="dialog" aria-labelledby="loginModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="loginModalLabel">{{ trans('frontend.login.title') }}</h5>
                <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>

                @include('components.messages')

            </div>
            <form class="form-horizontal" role="form" method="POST" action="{{ url('/login') }}">
                {!! csrf_field() !!}

                <div class="modal-body">

                    <div class="form-group{{ $errors->has('email') ? ' has-error' : '' }}">
                        <label class="col-md-4 control-label">E-Mail Address</label>

                        <div class="col-md-12">
                            <input type="email" class="form-control" name="email"
                                   value="{{ old('email') }}" required>
                        </div>
                    </div>

                    <div class="form-group{{ $errors->has('password') ? ' has-error' : '' }}">
                        <label class="col-md-4 control-label">Password</label>

                        <div class="col-md-12">
                            <input type="password" class="form-control" name="password" required>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-12">
                            <div class="checkbox">
                                <label>
                                    <input type="checkbox" name="remember"> Remember Me
                                </label>
                            </div>
                        </div>
                    </div>

                    <a style="color:#009f39;" class="btn btn-link" href="{{ url('/password/reset') }}">
                            Forgot Your Password?
                    </a>
                </div>

                <div class="modal-footer">

                    <button type="submit" class="btn btn-success">
                        <i class="fa fa-btn fa-sign-in"></i>Login
                    </button>

                    <button class="btn btn-secondary" type="button" data-dismiss="modal">
                        <i class="fa fa-btn fa-times"></i>{{ __('global.button.cancel') }}
                    </button>

                </div>
            </form>
        </div>
    </div>
</div>
