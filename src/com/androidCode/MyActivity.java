package com.androidCode;
import Classes.ConnectionAPI;
import Enums.ConnectionAPIMethods;
import android.app.Activity;
import android.os.Bundle;
import Classes.*;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MyActivity extends Activity implements AsyncTaskCompleteListener<ConnectionAPI> {

    static final String API_KEY ="a61d6204-6477-4f6d-93ec-86c4f872fb6b";

    TextView tv_main;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv_main = (TextView)findViewById(R.id.tv_main);

        new ConnectionAPI(API_KEY, ConnectionAPIMethods.getCouriers, this).execute();

    }

    public void onTaskComplete(ConnectionAPI result) {
        if (result.getException()!=null)
            System.out.println(result.getException().getMessage());//do something with the exception

        switch (result.getMethod().getNumberMethod()) {
            case 0://getLastCheckpoint
                break;
            case 1: //reactivate
                break;
            case 2://getTrackingByNumber
                break;
            case 3://getTracking
                List<Tracking> trackings =(List<Tracking>) result.getReturn();
                for (int i = 0; i < trackings.size(); i++)
                    tv_main.setText(tv_main.getText() + trackings.get(i).getTrackingNumber() + "\t" + trackings.get(i).getSlug() + "\n");
                break;
            case 4://deleteTracking
                break;
            case 5://postTracking
                break;
            case 6://putTracking
                break;
            case 7://getCouriers(7)
                List<Courier> couriers = (List<Courier>) result.getReturn();
                for (int i = 0; i < couriers.size(); i++)
                    tv_main.setText(tv_main.getText() + couriers.get(i).getName() + "\t" + couriers.get(i).getSlug() + "\n");
                break;
            case 8://detectCouriers(8)
                break;
        }
    }
    public void  onBtnClickedNumber1(View view){
            tv_main.setText("");

    }
    public void  onBtnClickedNumber2(View view){
            new ConnectionAPI(API_KEY, ConnectionAPIMethods.getCouriers, this).execute();
    }
    public void  onBtnClickedNumber3(View view){
            new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackings, this,1).execute();

    }



}
