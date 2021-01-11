package com.skymanlab.weighttraining.management.project.fragment;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.skymanlab.weighttraining.R;
import com.skymanlab.weighttraining.management.developer.Display;
import com.skymanlab.weighttraining.management.user.data.User;

public class FragmentTopBarManager extends FragmentSectionManager implements FragmentSectionInitializable {

    // constant
    private static final String CLASS_NAME = "[PF] FragmentTopBarManager";
    private static final Display CLASS_LOG_DISPLAY_POWER = Display.OFF;

    // instance variable
    private TextView title;

    // instance variable
    private String titleContent;

    // constructor
    public FragmentTopBarManager(Activity activity, View view, String titleContent) {
        super(activity, view);
        this.titleContent = titleContent;
    }

    @Override
    public void connectWidget() {

        // [iv/C]TextView : title connect
        this.title = (TextView) getView().findViewById(R.id.include_top_bar_title);

    }

    @Override
    public void initWidget() {

        // [method] : title 표시하기 위한 과정진행
        initWidgetOfTitle();

    }

    /**
     * [method] title widget 의 초기 내용을 설정한다.
     */
    private void initWidgetOfTitle() {

        // [check 1] : title 표시할 건지 titleContent 객체의 유무로 파악
        if (this.titleContent != null) {

            // [iv/C]TextView : title 표시하기 / 색상 BLACK 으로 변경
            this.title.setText(titleContent);
            this.title.setTextColor(getActivity().getColor(R.color.colorTextSecond));

        } // [check 1]

    } // End of method [initWidgetOfTitle]


}
