package com.skymanlab.weighttraining.management.project.fragment.Training.program.SectionManager;

import android.app.Activity;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.card.MaterialCardView;
import com.skymanlab.weighttraining.R;
import com.skymanlab.weighttraining.management.developer.Display;
import com.skymanlab.weighttraining.management.project.fragment.FragmentSectionInitializable;
import com.skymanlab.weighttraining.management.project.fragment.FragmentSectionManager;
import com.skymanlab.weighttraining.management.project.fragment.Training.program.MakerStep1Fragment;
import com.skymanlab.weighttraining.management.project.fragment.Training.program.MyProgramFragment;

public class ProgramSectionManager extends FragmentSectionManager implements FragmentSectionInitializable {

    // constant
    private static final String CLASS_NAME = "[PFTPS] ProgramSectionManager";
    private static final Display CLASS_LOG_DISPLAY_POWER = Display.ON;

    // instructor
    private MaterialCardView programMaker;
    private MaterialCardView myProgram;
    private MaterialCardView recommendProgram;

    // constructor
    public ProgramSectionManager(Activity activity, View view, FragmentManager fragmentManager) {
        super(activity, view, fragmentManager);
    }


    @Override
    public void mappingWidget() {

        // [iv/C]MaterialCardView : programMaker mapping
        this.programMaker = (MaterialCardView)getView().findViewById(R.id.f_program_menu_program_maker);

        // [iv/C]MaterialCardView : myProgram mapping
        this.myProgram = (MaterialCardView)getView().findViewById(R.id.f_program_menu_my_program);

        // [iv/C]MaterialCardView : recommendProgram mapping
        this.recommendProgram = (MaterialCardView)getView().findViewById(R.id.f_program_menu_recommend_program);

    }

    @Override
    public void initWidget() {

        // [iv/C]MaterialCardView : programMaker click listener
        this.programMaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // [lv/C]MakerStep1Fragment : maker step 1 fragment 생성
                MakerStep1Fragment step1Fragment = MakerStep1Fragment.newInstance();

                // [lv/C]FragmentTransaction : maker step 1 fragment 로 이동하기 FragmentTransaction 객체 생성
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_home_content_container, step1Fragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });


        // [iv/C]MaterialCardView : myProgram click listener
        this.myProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // [lv/C]MyProgramFragment : my program fragment 생성
                MyProgramFragment myProgramFragment = MyProgramFragment.newInstance();

                // [lv/C]FragmentTransaction : my program fragment 로 이동하기 FragmentTransaction 객체 생성
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_home_content_container, myProgramFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });


        // [iv/C]MaterialCardView : recommendProgram click listener
        this.recommendProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}