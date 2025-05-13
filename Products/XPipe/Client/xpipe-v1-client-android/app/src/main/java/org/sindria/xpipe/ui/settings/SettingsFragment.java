package org.sindria.xpipe.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import org.sindria.xpipe.ui.BaseFragment;
import org.sindria.xpipe.R;
import org.sindria.xpipe.databinding.SettingsFragmentBinding;

public class SettingsFragment extends BaseFragment<SettingsFragmentBinding> {

    private SettingsFragmentBinding binding;

    public SettingsFragment() {
        super(R.layout.settings_fragment, SettingsFragmentBinding.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = SettingsFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

}