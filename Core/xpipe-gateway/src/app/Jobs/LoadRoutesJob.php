<?php

namespace App\Jobs;

use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Foundation\Bus\Dispatchable;
use Illuminate\Queue\InteractsWithQueue;
use Illuminate\Queue\SerializesModels;
use App\Services\RouteService;
use App\Helpers\LoadRoutes;

class LoadRoutesJob implements ShouldQueue
{
    use Dispatchable, InteractsWithQueue, Queueable, SerializesModels;

    /**
     * @var RouteService
     */
    protected $routeService;

    /**
     * Create a new job instance.
     *
     * @return void
     */
    public function __construct(RouteService $routeService)
    {
        $this->routeService = $routeService;
    }

    /**
     * Execute the job.
     *
     * @return void
     */
    public function handle()
    {
        $loadedRoutes = $this->processRoutes();
        $stringPublicRoutes = implode("\n", $loadedRoutes['routes']['public']);
        $stringPrivateRoutes = implode("\n", $loadedRoutes['routes']['private']);

        $fileContent = "<?php" . "\n\n" . $loadedRoutes['groups']['public']['start'] . "\n" . $stringPublicRoutes . "\n" . $loadedRoutes['groups']['public']['end'] . "\n\n" . $loadedRoutes['groups']['private']['start'] . "\n" . $stringPrivateRoutes . "\n" . $loadedRoutes['groups']['private']['end'];

        $generatedRoutes = $this->routeService->storeGeneratedRoutes($fileContent);

        if (!$generatedRoutes) {
            // Exception
        }

        //echo "Handle Done";

        // Upload swagger -> Linter -> Cache swagger -> Event Trigger
        // Loader stateful multi swagger -> Observer -> Load Routes -> Check duplicates -> Process Routes -> Implode -> Generated Routes


    }


    /**
     * Process all routes
     *
     * @return array
     */
    private function processRoutes() : array
    {
        $generatedRoutes = array();
        $privateGeneratedRoutes = array();
        $publicGeneratedRoutes = array();

        $privateRoutes = $this->routeService->fetchPrivateRoutes();
        $publicRoutes = $this->routeService->fetchPublicRoutes();

        // Public group
        $template_public_group = "Route::group(['prefix' => ''], function () {";
        $template_public_end_group = "});";

        // Private group
        $template_private_group = "Route::group(['middleware' => 'keycloak', 'prefix' => ''], function () {";
        $template_private_end_group = "});";

        foreach ($publicRoutes as $entry) {
            $routeMethod = strtolower($entry->method);
            $templateRoute = "Route::$routeMethod('/$entry->version/$entry->context$entry->uri', 'Api\ProxyController@handle')->name('$entry->name');";
            $publicGeneratedRoutes[] = $templateRoute;
        }

        foreach ($privateRoutes as $entry) {
            $routeMethod = strtolower($entry->method);
            $templateRoute = "Route::$routeMethod('/$entry->version/$entry->context$entry->uri', 'Api\ProxyController@handle')->name('$entry->name');";
            $privateGeneratedRoutes[] = $templateRoute;
        }

        $generatedRoutes['groups']['public']['start'] = $template_public_group;
        $generatedRoutes['groups']['public']['end'] = $template_public_end_group;
        $generatedRoutes['groups']['private']['start'] = $template_private_group;
        $generatedRoutes['groups']['private']['end'] = $template_private_end_group;

        $generatedRoutes['routes']['public'] = $publicGeneratedRoutes;
        $generatedRoutes['routes']['private'] = $privateGeneratedRoutes;

        return $generatedRoutes;
    }
}
