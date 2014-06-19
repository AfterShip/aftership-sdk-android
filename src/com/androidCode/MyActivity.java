package com.androidCode;
import Classes.ConnectionAPI;
import Enums.ConnectionAPIMethods;
import android.app.Activity;
import android.os.Bundle;
import Classes.*;
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

        try {
            new ConnectionAPI(API_KEY, ConnectionAPIMethods.getCouriers, this).execute();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void onTaskComplete(ConnectionAPI result) {
        try {
            switch (result.getMethod().getNumberMethod()) {
                case 0://getLastCheckpoint
                    break;
                case 1: //reactivate
                    break;
                case 2://getTrackingByNumber
                    break;
                case 3://getTracking
                    break;
                case 4://deleteTracking
                    break;
                case 5://postTracking
                    break;
                case 6://putTracking
                    break;
                case 7://getCouriers(7)
                    List<Courier> couriers = result.getCouriersReturn();
                    for(int i=0;i<couriers.size();i++)
                        tv_main.setText(tv_main.getText() + couriers.get(i).getName() +"\t"+couriers.get(i).getSlug()+"\n");
                    break;
                case 8://detectCouriers(8)
                    break;
            }
        }catch (Exception e){
        // do something with the exception
       }


    }
}