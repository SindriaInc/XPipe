package org.sindria.xpipe.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import org.sindria.xpipe.ui.BaseFragment;
import org.sindria.xpipe.R;
import org.sindria.xpipe.databinding.LoginFragmentBinding;

public class LoginFragment extends BaseFragment<LoginFragmentBinding> {

    private LoginFragmentBinding binding;

    public LoginFragment() {
        super(R.layout.login_fragment, LoginFragmentBinding.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = LoginFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

}