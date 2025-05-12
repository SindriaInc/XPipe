<?php // Login Modal ?>
<div class="modal fade" id="loginModal" tabindex="-1" role="dialog" aria-labelledby="loginModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="loginModalLabel">Area riservata</h5>
                <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>

               <?php //@include('components.messages') ?>

            </div>
            <form class="form-horizontal" role="form" method="POST" action="/amministrazione/login">
                <?php //{!! csrf_field() !!} ?>
                <input type="hidden" name="_token" value="kJSH3odLAcZnieyJPsvigulkI6kjnyeSNVSI1xJO">

                <div class="modal-body">

                    <div class="form-group">
                        <label class="col-md-4 control-label">E-Mail Address</label>
                        <div class="col-md-12">
                            <input type="email" class="form-control" name="email" value="" required>
                        </div>
                    </div>

                    <div class="form-group">
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

                    <a style="color:#009f39;" class="btn btn-link" href="/amministrazione/password/reset">
                        Forgot Your Password?
                    </a>
                </div>

                <div class="modal-footer">

                    <button type="submit" class="btn btn-success">
                        <i class="fa fa-btn fa-sign-in"></i>Login
                    </button>

                    <button class="btn btn-secondary" type="button" data-dismiss="modal">
                        <i class="fa fa-btn fa-times"></i>Cancel
                    </button>

                </div>
            </form>
        </div>
    </div>
</div>
