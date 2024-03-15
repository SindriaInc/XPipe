<?php

namespace App\Services;

use App\Helpers\FilesystemHelper;
use App\Models\FileMeta;
use App\Traits\Purge;
use Illuminate\Support\Facades\DB;

class FileMetaService extends BaseService {

    use Purge;

    /**
     * FileMetaService constructor.
     */
    public function __construct(FileMeta $model) {
        parent::__construct($model);
    }

    /**
     * Get last updated FileMeta
     *
     * @return mixed
     */
    public function last()
    {
        return $this->model->orderBy('updated_at', 'DESC')->first();
    }

    /**
     * Delete a FileMeta
     *
     * @param $id
     */
    public function delete($id)
    {
        $fileMeta = $this->find($id);
        $post_id = $fileMeta->post_id;

        $this->model->destroy($id);
        $this->deletePost($post_id);

        //TODO gestire il return
    }

    //TODO da spostare in un altra route
    /**
     * Delete an attachment
     *
     * @param $post_id
     * @return void
     */
    private function deletePost($post_id)
    {
        $uploadedFile = DB::table('wp_postmeta')->where('post_id', $post_id)->where('meta_key', '_wp_attached_file')->value('meta_value');
        FilesystemHelper::delete(public_path('cms/uploads/').$uploadedFile);
        DB::table('wp_postmeta')->where('post_id', $post_id)->delete();
        DB::table('wp_posts')->where('id', $post_id)->delete();

        //TODO gestire il return
    }

}
