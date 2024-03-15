<?php

namespace App\Services;

use Illuminate\Database\Eloquent\Model;

abstract class BaseService
{


    /**
     * @var
     */
    protected $model;


    /**
     * ServiceAbstract constructor.
     * @param Model $model
     */
    public function __construct(Model $model) {
        $this->model = $model;
    }


    /**
     * Get all records
     *
     * @return \Illuminate\Database\Eloquent\Collection|Model[]
     */
    public function all() {
        return $this->model->all();
    }


    /**
     * Get all records with paginate
     *
     * @param $field
     * @param $order
     * @param $number
     * @return mixed
     */
    public function paginate($field, $order, $number) {
        return $this->model->orderBy($field, $order)->paginate($number);
    }


    /**
     * Find by id
     *
     * @param $id
     * @return mixed
     */
    public function find($id) {
        return $this->model->find($id);
    }


    /**
     * Create a new record
     *
     * @param $validated
     * @return mixed
     */
    public function create($validated) {
        return $this->model->create($validated);
    }


    /**
     * Create new records
     *
     * @param $validated
     * @return mixed
     */
    public function insert($validateds) {
        return $this->model->insert($validateds);
    }


    /**
     * Update a record
     *
     * @param $validated
     * @return boolean
     */
    public function update($validated) {
        return $this->model->update($validated);
    }


    /**
     * Delete a record
     *
     * @param $id
     * @return int
     */
    public function delete($id) {
        return $this->model->destroy($id);
    }


    /**
     * Truncate model table
     *
     * @return mixed
     * @deprecated
     */
    public function truncate() {
        return $this->model->truncate();
    }
}
