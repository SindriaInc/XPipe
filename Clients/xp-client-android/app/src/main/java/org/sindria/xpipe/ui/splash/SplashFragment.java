package org.sindria.xpipe.ui.splash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import org.sindria.xpipe.ui.BaseFragment;
import org.sindria.xpipe.R;
import org.sindria.xpipe.databinding.SplashFragmentBinding;
import androidx.fragment.app.Fragment;

public class SplashFragment extends Fragment {

//    private SplashFragmentBinding binding;
//
//    public SplashFragment() {
//        super(R.layout.splash_fragment,SplashFragmentBinding.class);
//    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.splash_fragment, container, false);
        return rootView;
        //binding = SplashFragmentBinding.inflate(inflater, container, false);
        //return binding.getRoot();
    }

}