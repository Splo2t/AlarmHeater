package com.causoft.heatersetter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {
    Activity thisActivity;

    Calendar setTime;

    TextView setTimeTextView;
    TextView inputDateTextView;

    EditText ssidTextField;
    EditText portTextField;

    long nowIndex;
    String ID;
    String dbTime;
    String repeat;
    String sort = "userid";

    ArrayAdapter<String> arrayAdapter;

    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();
    private TimeDbOpenHelper mTimeDbOpenHelper;
    private SettingDbOpenHelper mSettingDbOpenHelper;

    long mNow;
    Date mDate;

    public String hostIP = "";
    int port = 4824;
    public String date = "abc";
    // String inputData;

    // AlarmManager myAlarmManager;


    // private Intent alarmIntent;


    // int requestCode = 0;

    SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm");




    TimePickerDialog myTimePickerdialog;

    Button pickDateBtn;
    Button setBtn;
    Button resetBtn;
    Button setDeviceBtn;
    Button wifiSettingBtn;
    CheckBox dayCheckBox[];

    private Socket socket;  //소켓생성
    ObjectOutputStream outputStream;
    PrintWriter out;
    BufferedReader in;      //서버로부터 온 데이터를 읽는다.
    String data = "";            //
    String nowTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dayCheckBox = new CheckBox[7];
        thisActivity = this;

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.db_list_view);
        listView.setAdapter(arrayAdapter);

        mTimeDbOpenHelper = new TimeDbOpenHelper(this);
        mTimeDbOpenHelper.open();
        mTimeDbOpenHelper.create();
        mSettingDbOpenHelper = new SettingDbOpenHelper(this);
        mSettingDbOpenHelper.open();
        mSettingDbOpenHelper.create();

        showDatabase(sort);
        ssidTextField.setText(getIP());

        setDayCheckBox();
        setID();

        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        nowTime = mFormat.format(mDate);
        myTimePickerdialog = new TimePickerDialog(this, timePickListener, Integer.parseInt(nowTime.substring(0,2)), Integer.parseInt(nowTime.substring(3,5)), true);
        //   myAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        setTime = Calendar.getInstance();

        if(!ssidTextField.getText().toString().equals("")){
            hostIP = ssidTextField.getText().toString();
            port = Integer.parseInt(portTextField.getText().toString());
            InitSocket myInitSocket = new InitSocket();
            myInitSocket.start();
        }






        //     alarmIntent = new Intent("com.causoft.heatersetter.ALARM_START");

    /*    final PendingIntent myPendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT

        );



*/
        AdapterView.OnItemClickListener onListViewClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nowIndex = Long.parseLong(arrayIndex.get(position));
                String[] nowData = arrayData.get(position).split("\\s+");
                String viewData = nowData[0];
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("데이터 삭제")
                        .setMessage("해당 데이터를 삭제 하시겠습니까?" + "\n" + viewData)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (socket != null && date != null && socket.isConnected() && ! socket.isClosed()) {
                                    outThread outT = new outThread (date + "#" + checkedDay()+"r");
                                    outT.start();
                                    mTimeDbOpenHelper.deleteColumn(nowIndex);
                                    showDatabase(sort);

                                }
                                else {
                                    Toast.makeText(thisActivity, "기기에 먼저 접속해 주세요.", Toast.LENGTH_LONG).show();
                                    Toast.makeText(MainActivity.this, "데이터를 삭제했습니다.", Toast.LENGTH_SHORT).show();

                                }

                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "삭제를 취소했습니다.", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .create()
                        .show();
            }

        };
        listView.setOnItemClickListener(onListViewClickListener);



        Button.OnClickListener onClickListener = new Button.OnClickListener() {


            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.pickTimeButton:
                        if (socket != null && date != null && socket.isConnected() && ! socket.isClosed()) {
                            myTimePickerdialog.show();
                            // myAlarmManager.set(AlarmManager.RTC_WAKEUP, setTime.getTimeInMillis(), myPendingIntent);
                        }

                        else {
                            Toast.makeText(thisActivity, "기기에 먼저 접속해 주세요.", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.setDeviceButton:
                        SetSettingDialog tempSettingDialog = new SetSettingDialog(thisActivity);
                        tempSettingDialog.setDialogListener(new DialogListener() {
                            @Override
                            public void onPositiveClicked(String IP) {
                                mSettingDbOpenHelper.open();
                                mSettingDbOpenHelper.updateColumn(0,"ID"+"0",IP);
                                ssidTextField.setText(getIP());
                            }

                            @Override
                            public void onNegativeClicked() {

                            }
                        });
                        tempSettingDialog.show();

                    case R.id.resetButton:
                        if (socket != null && date != null && socket.isConnected() && ! socket.isClosed()) {
                            mTimeDbOpenHelper.deleteAllColumns();
                            showDatabase(sort);
                            Toast.makeText(thisActivity, "삭제완료.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(thisActivity, "기기에 먼저 접속해 주세요.", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.wifiSettingButton:
                        if (socket!= null && socket.isConnected() && ! socket.isClosed()) {
                            outThread outT = new outThread("setting");
                            outT.start();
                            Toast.makeText(thisActivity, "와이파이 설정이 선택되었습니다.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(thisActivity, "기기에 먼저 접속해 주세요.", Toast.LENGTH_LONG).show();
                        }
                }
            }

        };

        setButtonListener(onClickListener);
    }

    private void setButtonListener(Button.OnClickListener onClickListener) {
        pickDateBtn.setOnClickListener(onClickListener);
        setBtn.setOnClickListener(onClickListener);
        resetBtn.setOnClickListener(onClickListener);
        setDeviceBtn.setOnClickListener(onClickListener);
        wifiSettingBtn.setOnClickListener(onClickListener);
    }

    public void showDatabase(String sort){
        Cursor iCursor = mTimeDbOpenHelper.sortColumn(sort);
        arrayData.clear();
        arrayIndex.clear();
        while(iCursor.moveToNext()){
            String tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            String tempID = iCursor.getString(iCursor.getColumnIndex("userid"));
            tempID = setTextLength(tempID,15);
            String tempTime = iCursor.getString(iCursor.getColumnIndex("time"));
            tempTime = setTextLength(tempTime,15);
            String tempRepeat = iCursor.getString(iCursor.getColumnIndex("repeat"));
            tempRepeat = setTextLength(tempRepeat,15);

            String Result = tempID+ tempTime + tempRepeat;
            arrayData.add(Result);
            arrayIndex.add(tempIndex);
        }
        arrayAdapter.clear();
        arrayAdapter.addAll(arrayData);
        arrayAdapter.notifyDataSetChanged();
    }

    public String setTextLength(String text, int length){
        if(text.length()<length){
            int gap = length - text.length();
            for (int i=0; i<gap; i++){
                text = text + " ";
            }
        }
        return text;
    }
    class outThread extends Thread{
        String output;
        outThread(String input){
            output = input;
        }

        public void run(){
            if(socket.isConnected()){
                    out.write(output);
                    out.flush();
            }

        }
    }

    class InitSocket extends Thread {
        public void run() {
            try {
                    if(socket != null){
                        socket.close(); //소켓을 닫는다.
                    }
                    socket = new Socket(hostIP, port); //소켓생성
                    if(socket.isConnected() && ! socket.isClosed()){
                        thisActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(thisActivity, "서버와 연결되었습니다.", Toast.LENGTH_LONG).show();
                            }
                        });
                        outputStream = new ObjectOutputStream(socket.getOutputStream());
                        out = new PrintWriter(outputStream);//전송한다.
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    }
                    else{
                        thisActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(thisActivity, "연결에 실패하였습니다.", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                 //데이터 수신시 stream을 받아들인다.
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                while (true) {
                    /*
                    if(!(socket!= null && socket.isConnected() && ! socket.isClosed())){
                        thisActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(thisActivity, "연결이 끊어졌습니다.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    */
                    data = in.readLine();
                    inputDateTextView.post(new Runnable() {
                        public void run() {
                            inputDateTextView.setText(data);
                        }
                    });
                }
            } catch (Exception e) {
            }

        }
    }


    @Override
    protected void onStop() {  //앱 종료시
        super.onStop();
        try {
            socket.close(); //소켓을 닫는다.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TimePickerDialog.OnTimeSetListener timePickListener = new TimePickerDialog.OnTimeSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            setTime.set(Calendar.HOUR_OF_DAY, hour);
            setTime.set(Calendar.MINUTE, minute);
            setTime.set(Calendar.SECOND, 0);

            String hourString = hour < 10 ? ("0") + String.valueOf(hour) : String.valueOf(hour);
            String minuteString = minute < 10 ? ("0") + String.valueOf(minute) : String.valueOf(minute);

            date = hourString + ":" + minuteString;
            setTimeTextView.setText(date);
            mTimeDbOpenHelper.open();
            mTimeDbOpenHelper.insertColumn("ID"+setTimeTextView.getText().toString(), setTimeTextView.getText().toString(), checkedDay());
            showDatabase(sort);
            outThread outT = new outThread(setTimeTextView.getText() + "#" + checkedDay()+"a");
            outT.start();
        }
    };

    String checkedDay(){
        String returnString = "";
        for(int i = 0; i < 7; i++){
            if(dayCheckBox[i].isChecked()){
                returnString += String.valueOf(i);
            }
        }
        return returnString;
    }
    public String getIP(){
        String tempIP = "";
        Cursor iCursor = mSettingDbOpenHelper.getSetting();
        while(iCursor.moveToNext()){
            tempIP = iCursor.getString(iCursor.getColumnIndex("ip"));
        }
        return tempIP;

    }

    private void setID() {
        pickDateBtn = (Button) findViewById(R.id.pickTimeButton);
        setBtn = (Button) findViewById(R.id.setButton);
        resetBtn = (Button) findViewById(R.id.resetButton);
        setDeviceBtn = (Button) findViewById(R.id.setDeviceButton);
        wifiSettingBtn = (Button) findViewById(R.id.wifiSettingButton);

        setTimeTextView = (TextView)findViewById(R.id.dateTextView);
        inputDateTextView = (TextView)findViewById(R.id.inputDateTextView);

        ssidTextField = (EditText)findViewById(R.id.ssidEditText);
        portTextField = (EditText)findViewById(R.id.portEditTExt);
    }

    private void setDayCheckBox() {
        dayCheckBox[0] = (CheckBox)findViewById(R.id.mondayCheckBox);
        dayCheckBox[1] = (CheckBox)findViewById(R.id.tuesdayCheckBox);
        dayCheckBox[2] = (CheckBox)findViewById(R.id.wednesdayCheckBox);
        dayCheckBox[3] = (CheckBox)findViewById(R.id.thursdayCheckBox);
        dayCheckBox[4] = (CheckBox)findViewById(R.id.fridayCheckBox);
        dayCheckBox[5] = (CheckBox)findViewById(R.id.saturdayCheckBox);
        dayCheckBox[6] = (CheckBox)findViewById(R.id.sundayCheckBox);
    }


}



