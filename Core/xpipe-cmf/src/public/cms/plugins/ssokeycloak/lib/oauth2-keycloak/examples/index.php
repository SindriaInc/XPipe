<?php

require 'vendor/autoload.php';

use Stevenmaguire\OAuth2\Client\Passport;

$passport = Passport::init([
    'authServerUrl'         => 'http://xxxxx/auth',
    'realm'                 => 'xxxxxxx',
    'clientId'              => 'backend',
    'clientSecret'          => '9030c395-1ffa-44ad-99ba-e2aba9586822',
    'redirectUri'           => 'http://xxxx/',
]);
//$passport->setModel(Passport::$MODEL_REFRESH_TOKEN);

//if ($passport->getAccessToken()) {
    //$passport->logout();
    //exit;
//}

$user = $passport->checkLogin();


echo implode("<br>\n", [
    $passport->getAccessToken(),
    "--------------",
    $passport->getLogoutUrl(),
    $passport->getAuthorizationUrl(),
    $user->getAttr('username'),
]);
echo "</pre>";
print_r($passport->getToken());
print_r($user->toArray());

