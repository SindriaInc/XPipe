<?php

namespace App\Jobs;

use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldBeUnique;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Foundation\Bus\Dispatchable;
use Illuminate\Queue\InteractsWithQueue;
use Illuminate\Queue\SerializesModels;

use App\Traits\Csv;
use App\Services\PointService;
use Exception;
use Illuminate\Support\Facades\DB;

class ImportPointsJob implements ShouldQueue
{
    use Dispatchable, InteractsWithQueue, Queueable, SerializesModels, Csv;

    /**
     * @var PointService $pointService
     */
    public PointService $pointService;

    /**
     * @var int $batchSize
     */
    private int $batchSize = 100;

    /**
     * ImportPointsJob contructor
     *
     * @param PointService $pointService
     * @return void
     */
    public function __construct(PointService $pointService)
    {
        $this->pointService = $pointService;
    }

    /**
     * Execute the job.
     *
     * @return void
     */
    public function handle()
    {
        DB::transaction(function () {
            DB::statement('SET FOREIGN_KEY_CHECKS=0;');
            //$this->pointService->truncate();
            $this->pointService->purge();
            DB::statement('SET FOREIGN_KEY_CHECKS=1;');

            $file = "points.csv";
            $delimiter = ";";

            $fields = $this->getFieldsFromCsv($file, $delimiter);

            $validatedPoints = array();

            foreach ($fields as $key => $field) {

                try {

                    $validatedPoint = array();

                    $validatedPoint['code'] = $field[0];
                    $validatedPoint['name'] = $field[1];

                    $validatedPoints[] = $validatedPoint;

                    if (count($validatedPoints) >= $this->batchSize) {
                        $this->pointService->insert($validatedPoints);
                        $validatedPoints = array();
                    }

                } catch (\Exception $exception) {
                    $exception->getMessage();
                }

            }

            $this->pointService->insert($validatedPoints);
        });
    }
}
