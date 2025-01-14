package com.skyman.weighttrainingpro.management.event.listview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skyman.weighttrainingpro.R;
import com.skyman.weighttrainingpro.management.developer.Display;
import com.skyman.weighttrainingpro.management.developer.LogManager;
import com.skyman.weighttrainingpro.management.event.data.Event;
import com.skyman.weighttrainingpro.management.event.database.EventDbManager;
import com.skyman.weighttrainingpro.management.event.dialog.EventModificationDialog;
import com.skyman.weighttrainingpro.management.project.data.DataFormatter;
import com.skyman.weighttrainingpro.management.project.data.DataManager;

import java.util.ArrayList;

public class EventItemLvAdapter extends BaseAdapter {

    // constructor
    private static final String CLASS_NAME = "[EL]_EventItemLvAdapter ";
    private static final Display CLASS_LOG_DISPLAY_POWER = Display.ON;

    // instance variable
    private ArrayList<Event> eventArrayList = new ArrayList<>();
    private Activity targetActivity;
    private EventDbManager eventDbManager;
    private boolean isShowing[];

    public EventItemLvAdapter(Activity targetActivity, EventDbManager eventDbManager) {
        this.targetActivity = targetActivity;
        this.eventDbManager = eventDbManager;
    }

    @Override
    public int getCount() {
        return eventArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final String METHOD_NAME = "[getView] ";

        // [lv/C]Context : context 가져오기
        Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_event_item, parent, false);
        }

        // widget mapping
        TextView count = (TextView) convertView.findViewById(R.id.cu_event_item_count);
        TextView eventName = (TextView) convertView.findViewById(R.id.cu_event_item_event_name);
        TextView modify = (TextView) convertView.findViewById(R.id.cu_event_item_modify);
        TextView delete = (TextView) convertView.findViewById(R.id.cu_event_item_delete);
        ImageView moreAndLess = (ImageView) convertView.findViewById(R.id.cu_event_item_im_more);

        LinearLayout contentWrapper = (LinearLayout) convertView.findViewById(R.id.cu_event_item_content_wrapper);
        TextView equipmentType = (TextView) convertView.findViewById(R.id.cu_event_item_equipment_type);
        TextView groupType = (TextView) convertView.findViewById(R.id.cu_event_item_group_type);
        TextView properWeight = (TextView) convertView.findViewById(R.id.cu_event_item_proper_weight);
        TextView maxWeight = (TextView) convertView.findViewById(R.id.cu_event_item_max_weight);

        // [lv/C]Event : 하나의 리스트에 표시할 데이터
        Event event = (Event) getItem(position);

        LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "===>>> Adapter 의 event 내용확인 <<<===");
        LogManager.displayLogOfEvent(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, event);


        // [method] : modify, delete, moreAndLess 의 click listener 이벤트 설정하기
        setClickListenerOfModify(modify, position);
        setClickListenerOfDelete(delete, position);
        setClickListenerOfMoreAndLess(moreAndLess, contentWrapper, position);

        // widget initial data setting
        count.setText((position + 1) + ".");
        eventName.setText(event.getEventName());

        equipmentType.setText(DataManager.convertHanguleOfEquipmentType(event.getEquipmentType()));
        groupType.setText(DataManager.convertHanguleOfGroupType(event.getGroupType()));
        properWeight.setText(DataFormatter.setWeightFormat(event.getProperWeight()));
        maxWeight.setText(DataFormatter.setWeightFormat(event.getMaxWeight()));

        return convertView;

    } // End of method [getView]


    /**
     * [method] ListView 에 보여줄 데이터 설정하기
     *
     * @param eventArrayList 화면에 보여줄 데이터
     */
    public void setDataOfEventArrayList(ArrayList<Event> eventArrayList) {

        final String METHOD_NAME = "[setDataOfEventArrayList] ";

        // [iv/b]isShowing : eventArrayList 의 size 만큼 배열 생성 및 false 로 초기화
        this.isShowing = new boolean[eventArrayList.size()];

        // [cycle 1] :
        for (int index = 0; index < eventArrayList.size(); index++) {

            // [iv/C]ArrayList<Event> : 매개변수 eventArrayList 를 설정하기
            this.eventArrayList.add(eventArrayList.get(index));

            // [iv/b]isShowing : false 로 초기화
            this.isShowing[index] = false;

        } // [cycle 1]


        LogManager.displayLogOfEvent(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, eventArrayList);

    } // End of method [setDataOfEventArrayList]


    /**
     * [method] modify 의 click listener 를 설정하기
     */
    private void setClickListenerOfModify(TextView modify, final int position) {

        final EventItemLvAdapter adapter = this;

        // [lv/C]TextView : modify 의 click listener 를 설정한다.
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // [lv/C]EventModificationDialog : 해당 position 의 event 데이터를 수정하기 위한 dialog 를 띄워준다.
                EventModificationDialog dialog = new EventModificationDialog(targetActivity, eventDbManager, adapter, eventArrayList, position);
                dialog.setDialog();

            }
        });

    } // End of method [setClickListenerOfModify]


    /**
     * [method] delete 의 click listener 을 설정하기
     */
    private void setClickListenerOfDelete(TextView delete, final int position) {

        final String METHOD_NAME = "[setClickListenerOfDelete] ";

        // [lv/C]TextView : delete click listener
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // [lv/C]AlertDialog.Builder : builder 객체 생성 및 초기 설절
                AlertDialog.Builder builder = new AlertDialog.Builder(targetActivity);
                builder.setTitle(R.string.eila_alert_delete_title)
                        .setMessage(R.string.eila_alert_delete_message)
                        .setPositiveButton(R.string.eila_alert_delete_bt_positive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, ">>>>>>>>>>> 삭제할 event 의 내용");
                                LogManager.displayLogOfEvent(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, eventArrayList.get(position));

                                // [iv/C]EventDbManager :
                                int result = eventDbManager.deleteContentBy(eventArrayList.get(position).getCount(), eventArrayList.get(position).getUserCount());

                                LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "result = " + result);

                                // [check 1] : 해당 데이터를 삭제를 성공하였다.
                                if (result == 1) {

                                    LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "=> check_1/true : 데이터베이스에서 삭제 완료하였습니다. <=");
                                    // [lv/C]ArrayList<Event> : eventArrayList 에서 해당 position 의 event 제거하기
                                    eventArrayList.remove(position);

                                    // [method] : 변경된 내용을 adapter 를 통해 반영하기
                                    notifyDataSetChanged();

                                    // [lv/C]Toast : 삭제가 완료되었습니다.
                                    Toast.makeText(targetActivity, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();

                                } else {
                                    LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "=> check_1/false : 데이터베이스에서 삭제하지 못 했어! <=");

                                    // [lv/C]Toast : 실패하였습니다.
                                    Toast.makeText(targetActivity, "실패하였습니다.", Toast.LENGTH_SHORT).show();

                                } // [check 1]

                            }
                        })
                        .setNegativeButton(R.string.eila_alert_delete_bt_negative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        })
                        .show();

            }
        });

    } // End of method [setClickListenerOfDelete]


    /**
     * [method] moreAndLess 의 click listener 을 설정하기
     */
    private void setClickListenerOfMoreAndLess(final ImageView moreAndLess, final LinearLayout contentWrapper, final int position) {

        final String METHOD_NAME = "[setClickListenerOfMoreAndLess] ";

        // [lv/C]LinearLayout : moreAndLess click listener
        moreAndLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // [check 1] : 해당 section 을 보여주고 있나요?
                if (!isShowing[position]) {

                    LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, ">>>> 숨김 -> 보여줌 >>>>");
                    // 해당 section 을 보여준다.
                    // [iv/b]isShowing : true 로 변경
                    isShowing[position] = true;

                    // [lv/C]LinearLayout : 해당 contentWrapper 를 보여준다.(VISIBLE)
                    contentWrapper.setVisibility(LinearLayout.VISIBLE);

                    // [lv/C]ImageView : 해당 이미지를 △ 모양으로 변경
                    moreAndLess.setImageResource(R.drawable.ic_baseline_expand_less_24);

                } else {

                    LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, ">>>> 보여줌 -> 숨김 >>>>");
                    // 해당 section 을 보여주지 않는다.
                    // [iv/b]isShowing : false 로 변경
                    isShowing[position] = false;

                    // [lv/C]LinearLayout : 해당 contentWrapper 를 없앤다.(GONE)
                    contentWrapper.setVisibility(LinearLayout.GONE);

                    // [lv/C]ImageView : 해당 이미지를 ▽ 모양으로 변경
                    moreAndLess.setImageResource(R.drawable.ic_baseline_expand_more_24);

                } // [check 1]

            }
        });

    } // End of method [setClickListenerOfMoreAndLess]
}
