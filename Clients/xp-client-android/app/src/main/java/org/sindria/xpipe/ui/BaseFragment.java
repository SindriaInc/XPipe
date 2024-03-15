package org.sindria.xpipe.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//import androidx.fragment.app.activityViewModels;
import androidx.navigation.*;
import androidx.navigation.fragment.NavHostFragment;
import org.sindria.xpipe.R;
//import kotlinx.android.synthetic.main.activity_main.*;

public abstract class BaseFragment<T> extends Fragment {

    protected int layoutResId;

    protected Boolean bottomMenuVisible;

    protected Boolean toolbarGoBackVisible;

    protected Class<T> classBinding;

    public BaseFragment(@LayoutRes int layoutResId, Class<T> typeBinding) {
        this.layoutResId = layoutResId;
        this.classBinding = typeBinding;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layoutResId, container, false);
    }

//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Class<T> binding = this.classBinding.inflate(inflater, container, false);
//        return binding.getRoot();
////        binding = LoginFragmentBinding.inflate(inflater, container, false);
////        return binding.getRoot();
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.classBinding = null;
    }
}
