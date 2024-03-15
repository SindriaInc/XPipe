<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use App\Jobs\LoadRoutesJob;
use App\Services\RouteService;
use Illuminate\Foundation\Bus\DispatchesJobs;

class LoadRoutes extends Command
{
    use DispatchesJobs;

    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'load:routes';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Load all XPipe Gateway routes';

    /**
     * @var RouteService
     */
    protected $routeService;

    /**
     * Create a new command instance.
     *
     * @return void
     */
    public function __construct(RouteService $routeService)
    {
        parent::__construct();
        $this->routeService = $routeService;
    }

    /**
     * Execute the console command.
     *
     * @return int
     */
    public function handle()
    {

        try {
            $this->info('Loading api...');
            $this->dispatch(new LoadRoutesJob($this->routeService));
            $this->info('Done.');
            return 0;
        } catch (\Exception $e) {
            echo $e;
            return 1;
        }
    }
}
