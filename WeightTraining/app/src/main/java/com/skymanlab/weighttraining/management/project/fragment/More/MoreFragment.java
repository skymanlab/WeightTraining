package com.skymanlab.weighttraining.management.project.fragment.More;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.telephony.TelephonyScanManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skymanlab.weighttraining.R;
import com.skymanlab.weighttraining.management.developer.Display;
import com.skymanlab.weighttraining.management.developer.LogManager;
import com.skymanlab.weighttraining.management.project.fragment.FragmentTopBarManager;
import com.skymanlab.weighttraining.management.project.fragment.FragmentTopUserManager;
import com.skymanlab.weighttraining.management.project.fragment.More.SectionManager.MoreSectionManager;
import com.skymanlab.weighttraining.management.user.data.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoreFragment extends Fragment {

    // constant
    private static final String CLASS_NAME = "[PFM] MoreFragment";
    private static final Display CLASS_LOG_DISPLAY_POWER = Display.OFF;

    // instance variable
    private FragmentTopBarManager topBarManager;
    private FragmentTopUserManager topUserManager;
    private MoreSectionManager sectionManager;

    // constructor
    public MoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoreFragment newInstance() {
        MoreFragment fragment = new MoreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // top bar
        this.topBarManager = new FragmentTopBarManager(this, view, getString(R.string.f_more_title));
        this.topBarManager.connectWidget();
        this.topBarManager.initWidget();

        // top user
        this.topUserManager = new FragmentTopUserManager(this, view, true);
        this.topUserManager.connectWidget();
        this.topUserManager.initWidget();

        // section
        this.sectionManager = new MoreSectionManager(this, view);
        this.sectionManager.connectWidget();
        this.sectionManager.initWidget();

    }



}
