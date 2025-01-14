package com.skyman.weighttrainingpro.management.event.database.mysql;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.skyman.weighttrainingpro.management.developer.Display;
import com.skyman.weighttrainingpro.management.developer.LogManager;
import com.skyman.weighttrainingpro.management.event.data.Event;
import com.skyman.weighttrainingpro.management.event.database.EventDbManager;
import com.skyman.weighttrainingpro.management.project.data.RightDataChecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class EventRegister extends AsyncTask<Event, Void, Event> {

    // constant
    private static final String CLASS_NAME = "[EDM]_EventInsert";
    private static final Display CLASS_LOG_DISPLAY_POWER = Display.OFF;

    // constant
    private static final String INSERT_PHP_FILE = "http://skyman13.cafe24.com/WTP_event_insert.php?";
    private static final String SELECT_COUNT_PHP_FILE = "http://skyman13.cafe24.com/WTP_event_select_my_count.php?";

    // instance variable : intent
    private Context context;

    // instance variable : db
    private EventDbManager eventDbManager;

    // instance variable : section_1 widget
    private Spinner equipmentType;
    private Spinner targetMuscleType;
    private EditText eventName;
    private EditText properWeight;
    private EditText maxWeight;

    // constructor
    public EventRegister(Context context, EventDbManager eventDbManager, Spinner equipmentType, Spinner targetMuscleType, EditText eventName, EditText properWeight, EditText maxWeight) {
        this.context = context;
        this.eventDbManager = eventDbManager;
        this.equipmentType = equipmentType;
        this.targetMuscleType = targetMuscleType;
        this.eventName = eventName;
        this.properWeight = properWeight;
        this.maxWeight = maxWeight;
    }


    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= 결과를 UI 에 적용 =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    @Override
    protected void onPostExecute(Event event) {
        super.onPostExecute(event);

        final String METHOD_NAME = "[onPostExecute] ";

        LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "**** event 데이터 최종 확인 ***");
        LogManager.displayLogOfEvent(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, event);

//        // [method] : 데이터베이스 이용하기 위한 설정
//        connectAndInitOfDatabase();

        // [check 1] : 옳은 event 데이터입니다.
        if (RightDataChecker.checkWhetherRightEvent(event)) {

            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "=> check_1/true : SQLiteDatabase 에 저장할 준비가 되었습니다. <=");
            // [method] : SQLiteDatabase 의 event table 에 event 데이터 저장
            saveEventToSQLiteDatabase(event);
            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "-=================> 저장 완료");


//            // [check 2] : projectDbConnector 가 생성되었다.
//            if (this.projectDbConnector != null) {
//
//                LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "=> check_2/true : 이제 연결을 끊기만 하면 되요. <=");
//                // [iv/C]ProjectDbConnector : close
//                this.projectDbConnector.closeProjectDB();
//
//            } else {
//                LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "=> check_2/false : 생성된 projectDbConnector 가 없어요! <=");
//            } // [check 2]

            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "-=================> 데이터베이스 닫기");

            // [method] : section_1 widget 의 내용 초기화
            initContentOfSectionOne();

            // [lv/C]Toast : 성공 메시지를 Toast 로 보여주기
            Toast.makeText(context, "저장이 완료 되었습니다.", Toast.LENGTH_SHORT).show();

        } else {

        } // [check 1]

    } // End of method [onPostExecute]


    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= HttpURLConnection 통신 =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    @Override
    protected Event doInBackground(Event... events) {

        final String METHOD_NAME = "[doInBackground] ";

        // [lv/C]Event : event table 에 저장하기 위한 데이터
        Event event = events[0];
        LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "**** event 데이터를 확인할께요. ****");
        LogManager.displayLogOfEvent(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, event);

        // [lv/C]String : post 방식으로 보내는 데이터를 url mapping
        String parameters = mappingUrlOfParameters(event);

        // [check 1] : 위에서 받은 user 데이터를 서버에 저장을 성공하였다.
        if (checkWhetherInsert(connectionDatabase(INSERT_PHP_FILE, parameters))) {

            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "=> check_1/true : 성공했습니다.<=");
            // [lv/l]eventCount : 위에서 저장한 데이터가 성공했다면 event 가 몇 번째인지 받아온다
            long eventCount = Long.parseLong(connectionDatabase(SELECT_COUNT_PHP_FILE, parameters));

            // [check 2] : userCount 가 범위에 맞는 값이다.
            if (checkWhetherRightRangeOfCount(eventCount)) {

                LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "=> check_2/true : 성공했습니다.<=");

                // [lv/C]Event : eventCount 를 입력
                event.setCount(eventCount);

            } else {
                LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "=> check_2/false : 실패했어!! <=");
            } // [check 2]

        } else {
            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "=> check_1/false : 실패했어!! <=");
        } // [check 1]

        return event;

    } // End of method [doInBackground]


    /**
     * [method] 데이터베이스 서버와 통신을 하여 결과를 받는다.
     *
     * @param targetPhpFileAddress
     * @param parameters
     * @return 서버의 응답 결과
     */
    private String connectionDatabase(String targetPhpFileAddress, String parameters) {

        final String METHOD_NAME = "[connectionDatabase] ";

        // [lv/C]StringBuilder : 서버의 응답 메시지를 받기 위한
        StringBuilder responseMessage = new StringBuilder();

        LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "**** url 확인! ***");
        LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "1. url = " + targetPhpFileAddress + parameters);

        try {
            // [lv/C]URL : 서버와 연결하기 위한 URL 객체 생성
            URL url = new URL(targetPhpFileAddress);

            // [lv/C]HttpURLConnection : 위의 url 로 http 연결하기 위해 open / 초기 설정 / connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");
            connection.connect();

            // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= 보내기 =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "<step 1> 보내기 시작");
            // [lv/C]OutputStream : 서버에 parameters 를 보내기 위해서 output stream 연결하기 / parameters 전송 / 바로전송 / output stream 닫기
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(parameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= 결과 받기 전 예외처리 =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "<step 2> 보내기 완료 => 결과 받기 전 예외 처리하기 시작");
            // [lv/i]responseStateCode : 서버 전송결과를 나타내는 코드를 받기
            int responseStateCode = connection.getResponseCode();
            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "<step 2-1> 응답 코드 // " + responseStateCode);

            // [lv/C]InputStream : 서버에서 보낸 데이터를 받기 위한 InputStream
            InputStream inputStream = null;

            // [check 1] : 응답코드가 뭐니? /
            if (responseStateCode == HttpURLConnection.HTTP_OK) {

                LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "<step 2> check_1/ 정상 inputStream 연결");
                // [lv/C]InputStream : 정상상태이므로 일반적인 inputStream 을 가져온다.
                inputStream = connection.getInputStream();

            } else {

                LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "<step 2-2> 에러 inputStream 연결");
                // [lv/C]InputStream : 서버에서 오류가 발생하여, error 메시지를 받기 위한 stream 을 가져온다.
                inputStream = connection.getErrorStream();

            } // [check 1]

            // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= 결과 받기 =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "<step 3> 예외처리 완료 => 결과 받기");
            // [lv/C]BufferedReader : 위에서 connection 을 통해 가져온 inputStream 을 UTF-8 방식의 문자를 받기 위해 BufferedReader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            // [lv/C]String : 한 줄씩 읽기위한 임시
            String tempLine = null;

            // [cycle 1] : 한 줄 의 내용이 있을 때
            while ((tempLine = bufferedReader.readLine()) != null) {

                // [lv/C]StringBuilder : 한 줄의 내용을 responseMessage 에 추가하기
                responseMessage.append(tempLine);
                LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, tempLine);

            } // [cycle 1]

            // [lv/C]BufferedReader : 닫기
            bufferedReader.close();

            // [lv/C]InputStream : 닫기
            inputStream.close();
            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "<step 4> 모든 과정 완료");

        } catch (MalformedURLException e) {
            // URL 예외 처리
            e.printStackTrace();
        } catch (IOException e) {
            // url openConnection 예외 처리
            e.printStackTrace();
        }

        return responseMessage.toString();
    } // End of method [connectionDatabase]


    /**
     * [method] event 의 데이터를 post 방식으로 보내기 위한 url mapping
     * <p>
     * <주의>
     * 맨 앞에 "?" 를 추가하면 "?" 다음의 parameter 의 값을 php 파일에서 못 읽는다.
     */
    private String mappingUrlOfParameters(Event event) {

        final String METHOD_NAME = "[mappingUrlOfParameters] ";

        // [lv/C]StringBuilder : url
        StringBuilder url = new StringBuilder();

        // 0. count
        url.append("count=");
        url.append(event.getCount());
        url.append("&");

        // 1. user count
        url.append("userCount=");
        url.append(event.getUserCount());
        url.append("&");

        // 2. eventName
        url.append("eventName=");
        url.append("'");
        url.append(event.getEventName());
        url.append("'");
        url.append("&");

        // 3. muscleArea
        url.append("muscleArea=");
        url.append("'");
        url.append(event.getMuscleArea());
        url.append("'");
        url.append("&");

        // 4. equipmentType
        url.append("equipmentType=");
        url.append("'");
        url.append(event.getEquipmentType());
        url.append("'");
        url.append("&");

        // 5. targetMuscleType
        url.append("groupType=");
        url.append("'");
        url.append(event.getGroupType());
        url.append("'");
        url.append("&");

        // 6. properWeight
        url.append("properWeight=");
        url.append(event.getProperWeight());
        url.append("&");

        // 7. maxWeight
        url.append("maxWeight=");
        url.append(event.getMaxWeight());

        LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "*** mappingUrl = " + url.toString());

        return url.toString();
    } // End of method [mappingUrlOfParameters]


    /**
     * [method] responseMessage 값을 통하여 Insert 문 실행이 성공하였는지 실패 하였는지 판별하여 성공시 true, 실패시 false 를 반환한다.
     */
    private boolean checkWhetherInsert(String responseMessage) {

        final String METHOD_NAME = "[checkWhetherInsert] ";

        // [lv/b]isSuccessful : 성공했냥?
        boolean isSuccessful = false;

        // [check 1] : responseMessage 가 뭐냐?
        if (responseMessage.toString().equals("success")) {

            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "=> check_1/true : 데이터베이스 서버에 성공적으로 입력하였습니다. <=");
            // [lv/b]isSuccessful : 입력 성공이야
            isSuccessful = true;

        } else if (responseMessage.toString().equals("fail")) {

            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "=> check_1/false : 데이터베이스 서버에 입력을 실패했어! <=");
            // [lv/b]isSuccessful : 입력 실패야!
            isSuccessful = false;

        } // [check 1]

        return isSuccessful;
    } // End of method [checkWhetherInsert]


    /**
     * [method] count 값을 알기위해서 수행한 Select 문의 결과가 올바른 법위의 값인지 판별하여 맞으면 true, 아니면 false 로 반환한다.
     */
    private boolean checkWhetherRightRangeOfCount(long count) {

        final String METHOD_NAME = "[checkWhetherRightRangeOfCount] ";

        // [lv/b]isRightRange : 옳은 범위의 count 냐?
        boolean isRightRange = false;

        // [check 1] : 0 보다 큰 값이다.
        if (0 < count) {
            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "=> check_1/true : 0 보다 큰 값이 맞습니다. <=");
            // [lv/b]isRightRange : 넌 X 번째 유저야.
            isRightRange = true;

        } else {
            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "=> check_2/false : 도대체 몇 번째 user 냐? <=");
            // [lv/b]isRightRange : 넌 X 맨이야
            isRightRange = false;

        } // [check 1]

        return isRightRange;
    }


//    /**
//     * [method] project database 에 접근하기위한 초기 설정 및 table 을 사용하기 위한 메니저를 생성하는 초기작업 실행
//     */
//    private void connectAndInitOfDatabase() {
//
//        final String METHOD_NAME = "[connectAndInitOfDatabase] ";
//
//        // [iv/C]ProjectDbConnector : database 에 연결하기 위한 connector 생성
//        this.projectDbConnector = new ProjectDbConnector(this.context);
//
//        // [iv/C]ProjectDbConnector : ProjectDbHelper 생성 및 연결완료 설정하기
//        this.projectDbConnector.connectProjectDB();
//
//        // [iv/C]EventDbManager : 위에서 연결한 database 에서 event table 을 사용하기 위한 manager 생성
//        this.eventDbManager = new EventDbManager(this.projectDbConnector);
//
//    } // End of method [connectAndInitOfDatabase]


    /**
     * [method] user table 에 user 데이터를 저장한다.
     */
    private void saveEventToSQLiteDatabase(Event event) {

        // [iv/C]UserDbManager : user 를 저장한다.
        this.eventDbManager.saveContent(event);

    } // End of method [saveEventToSQLiteDatabase]


    /**
     * [method] section_1 widget 의 내용을 초기화한다.
     */
    private void initContentOfSectionOne() {

        // [iv/C]Spinner : equipmentType 첫 번째 선택
        this.equipmentType.setSelection(0);

        // [iv/C]Spinner : targetMuscleType 첫 번째 선택
        this.targetMuscleType.setSelection(0);

        // [iv/C]EditText : eventName 모든 문자열 제거
        this.eventName.setText("");

        // [iv/C]EditText : properWeight 모든 문자열 제거
        this.properWeight.setText("");

        // [iv/C]EditText : maxWeight 모든 문자열 제거
        this.maxWeight.setText("");

    } // End of method [initSectionOneWidget]

}
