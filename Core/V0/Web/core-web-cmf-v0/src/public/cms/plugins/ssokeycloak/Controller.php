<?php

namespace Sindria\SsoKeycloak;

use Sindria\OAuth2\Client\Passport;
use Sindria\SsoKeycloak\Helper;

class Controller
{
    /**
     * @var Passport $passport
     */
    public Passport $passport;

    /**
     * @var Service $service
     */
    public Service $service;


    /**
     * Controller constructor
     *
     */
    public function __construct(Service $service)
    {
        $this->service = $service;

        $this->passport = Passport::init([
            'authServerUrl' => Helper::getAuthApiBaseUrl(),
            'realm' => Helper::getAuthRealm(),
            'clientId' => Helper::getAuthClientId(),
            'clientSecret' => Helper::getAuthClientSecret(),
            'redirectUri' => Helper::getAuthCallback(),
        ]);

    }

    /**
     * Handle callback request from keycloak
     *
     * @return mixed|void
     */
    public function handle()
    {
        try {

            if ($this->needHandle()) {

                $login = $this->passport->checkLogin();

                if (! $this->passport->isLogin()) {
                    $this->sendLoginFailResponse('sso_error');
                }

                $username = $login->getAttr('preferred_username');
                $user = get_user_by('login', $username);

                if (! $user) {

                    if (Helper::getAutoprofileToggle()) {
                        $id = $this->service->createUserProfile($username, $login->getEmail(), $login->getAttr('given_name'), $login->getAttr('family_name'));
                        $user = get_user_by('ID', $id);
                    } else {
                        $this->sendLoginFailResponse('profile_error');
                    }
                }

                $uuid = $login->getAttr('sub');
                $accessToken = $this->passport->getAccessToken();
                $this->service->createUserSession($user, $uuid, $accessToken);

                // LOGIN SUCCESS AFTER USER SESSION HOOK
                do_action('ssokeycloak_login_success_after_user_session');

                // LOGIN SUCCESS BEFORE REDIRECT HOOK
                do_action('ssokeycloak_login_success_before_redirect');

                // TODO: rendere generico per plugin standalone
                $redirectTo = env('APP_URL'). '/auth/login?u=' . $user->user_login;
                $this->redirectIfUserLoggedIn($redirectTo);
            }

        } catch (\Exception $e) {
            report($e);
            $this->sendLoginFailResponse('sso_exception');
        }
    }

    /**
     * Send login fail response
     *
     * @param $errorType
     * @return void
     */
    private function sendLoginFailResponse($errorType) : void
    {
        if ($this->passport->isLogin()) {
           $this->performLogout();
        }

        wp_redirect(get_permalink( 93 ) . "?login=failed&reason=" . $errorType);
        exit;
    }



    /**
     * Check if request need to handle
     *
     * @return bool
     */
    private function needHandle() : bool
    {
        return $this->isWpLogin() && $this->isCallback() ? true : false;
    }

    /**
     * Determine if current page is wp-login.php
     *
     * @return bool
     */
    private function isWpLogin() : bool
    {
        global $pagenow;

        if ($pagenow == 'wp-login.php') {
            return true;
        }

        return false;
    }

    /**
     * Determine if request is a valid callback
     *
     * @return bool
     */
    private function isCallback() : bool
    {
        $action = NULL;

        if (isset($_GET['action'])) {
            $action = $_GET['action'];
        }

        if ($action != "logout" && $action == "callback") {
            return true;
        }

        return false;
    }


    /**
     * Redirect user to dashboard if logged in
     *
     * @return void
     */
    private function redirectIfUserLoggedIn(string $redirectTo) : void
    {
        if (is_user_logged_in()) {
            //$redirect_to = user_admin_url();
            wp_safe_redirect($redirectTo);
            $this->refreshClient($redirectTo);
            exit;
        }
    }

    /**
     * Refresh client side
     *
     * @param $url
     * @return void
     */
    private function refreshClient($url) : void
    {
        echo("<script>location.href = '".$url."';</script>");
    }

    /**
     * Handle logout actions
     *
     * @return void
     */
    public function performLogout() : void
    {
        if ($this->isLoggedWithSso()) {
            $this->service->logout();
            $this->service->revokeCurrentAccessToken();
            $this->service->destroyUserSession();
        }
    }

    /**
     * Determine if user is logged via sso
     *
     * @return bool
     */
    private function isLoggedWithSso() : bool
    {
        if (Helper::getCurrentAccessToken() != "") {
            return true;
        }

        return false;
    }

    /**
     * Create SSO Button on login page
     *
     * @param $input
     * @return void
     */
    public function createSsoButton($input = '')
    {

        if (Helper::getSsoButtonToggle()) {

            ?>
            <div>
                <style>
                    .wpsindria-ssosignin-wrapper {
                        box-sizing: border-box;
                        display: block;
                        width: 100%;
                        padding: 12px 12px 24px 12px;
                        text-align: center;
                    }
                    .wpsindria-ssosignin-spacearound {
                        display: inline-block;
                    }
                    .wpsindria-ssosignin-wrapper form {
                        display: none;
                    }
                    .wpsindria-ssosignin-button {
                        border: 1px solid #8c8c8c;
                        background: #ffffff;
                        display: flex;
                        display: -webkit-box;
                        display: -moz-box;
                        display: -webkit-flex;
                        display: -ms-flexbox;
                        -webkit-box-align: center;
                        -moz-box-align: center;
                        -ms-flex-align: center;
                        -webkit-align-items: center;
                        align-items: center;
                        -webkit-box-pack: center;
                        -moz-box-pack: center;
                        -ms-flex-pack: center;
                        -webkit-justify-content: center;
                        justify-content: center;
                        cursor: pointer;
                        max-height: 41px;
                        min-height: 41px;
                        height: 41px;
                    }
                    .wpsindria-ssosignin-logo {
                        padding-left: 12px;
                        padding-right: 6px;
                        -webkit-flex-shrink: 1;
                        -moz-flex-shrink: 1;
                        flex-shrink: 1;
                        width: 21px;
                        height: 21px;
                        box-sizing: content-box;
                        display: flex;
                        display: -webkit-box;
                        display: -moz-box;
                        display: -webkit-flex;
                        display: -ms-flexbox;
                        -webkit-box-pack: center;
                        -moz-box-pack: center;
                        -ms-flex-pack: center;
                        -webkit-justify-content: center;
                        justify-content: center;
                    }
                    .wpsindria-ssosignin-label {
                        padding-left: 6px;
                        padding-right: 12px;
                        font-weight: 600;
                        color: #5e5e5e;
                        font-family: "Segoe UI", Frutiger, "Frutiger Linotype", "Dejavu Sans", "Helvetica Neue", Arial, sans-serif;
                        font-size: 15px;
                        -webkit-flex-shrink: 1;
                        -moz-flex-shrink: 1;
                        flex-shrink: 1;
                        height: 21px;
                        line-height: 21px;
                    }

                </style>



                <div id="wpsindriaOpenIdRedirect" class="wpsindria-ssosignin-wrapper">
                    <p>OR</p><br />
                    <div class="wpsindria-ssosignin-spacearound">
                        <div class="wpsindria-ssosignin-button" onclick="window.location.href = '<?= $this->passport->getAuthorizationUrl() ?>'">
                            <div class="wpsindria-ssosignin-logo">
                                <img style="height: 25px; width: 100px;" src="<?= Helper::getSsoButtonIcon() ?>" alt="SSO">
                            </div>
                            <div class="wpsindria-ssosignin-label">Sign in with SSO</div>
                        </div>
                    </div>
                </div>
            </div>
            <?php

        }


    }



}
