<?php

namespace App\Traits;

trait Csv
{
    /**
     * Get all fields from csv file
     *
     * @param string $file
     * @param string $delimiter
     * @return array
     */
    public function getFieldsFromCsv(string $file, string $delimiter) : array
    {
        $handle = fopen(storage_path("app/import/$file"), "r");
        $header = true;
        $RowCount = 0;

        while ($csvLine = fgetcsv($handle, 2048, "$delimiter")) {
            if ($header) {
                $header = false;
            } else {
                $RowCount++;
                $fields[] = $csvLine;
            }
        }
        return $fields;
    }

}
