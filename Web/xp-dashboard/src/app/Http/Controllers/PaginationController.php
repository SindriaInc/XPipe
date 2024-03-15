<?php

namespace App\Http\Controllers;

use App\Http\Requests\PaginationQuantityRequest;

class PaginationController extends Controller
{

    public function changeQuantity(PaginationQuantityRequest $request) {

        $validated = $request->validated();

        $quantity = $validated['quantity'];

        session()->put('quantity', $quantity);

        return redirect()->back();
    }
}
