package com.androidCode.test;

import Classes.*;
import Enums.*;
import Enums.ConnectionAPIMethods;
import android.test.InstrumentationTestCase;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.util.Log;

/**
 * Created by User on 19/6/14.
 */
public class LaunchActivityTest extends InstrumentationTestCase {

    static final String API_KEY = "a61d6204-6477-4f6d-93ec-86c4f872fb6b";
    AsyncTaskCompleteListener<ConnectionAPI> listener;
    CountDownLatch latch = new CountDownLatch(0);


    //Answers callbacks
    Exception exception;
    List<Courier> returnCoriers = null;
    Tracking returnTracking = null;
    List<Tracking> returnTrackings = null;
    Checkpoint returnCheckpoint;
    boolean returnActionConfirmation = false;

    static boolean firstTime = true;


    //post tracking number
    String trackingNumberPost = "05167019264110";
    String slugPost = "dpd";
    String orderIDPathPost = "www.whatever.com";
    String orderIDPost = "ID 1234";
    String customerNamePost = "Mr Smith";
    String titlePost = "this title";
    ISO3Country countryDestinationPost = ISO3Country.USA;
    String email1Post = "email@yourdomain.com";
    String email2Post = "another_email@yourdomain.com";
    String sms1Post = "+85292345678";
    String sms2Post = "+85292345679";
    String customProductNamePost = "iPhone Case";
    String customProductPricePost = "USD19.99";

    String trackingNumberToDetect = "09445246482536";
    String trackingNumberToDetectError = "asdq";

    //Tracking to Delete
    String trackingNumberDelete = "596454081704";
    String slugDelete = "fedex";

    //tracking to DeleteBad
    String trackingNumberDelete2 = "798865638020";


    protected void setUp() throws Exception {
        super.setUp();
        this.listener = new AsyncTaskCompleteListener<ConnectionAPI>() {
            //when the asynch finished, it will call this method of the listener
            public void onTaskComplete(ConnectionAPI result) {
                if (result.getException() != null) {
                    LaunchActivityTest.this.exception = result.getException();
                    Log.d("","*****"+LaunchActivityTest.this.exception.getMessage());
                }
                switch (result.getMethod().getNumberMethod()) {
                    case 0://getLastCheckpoint
                        LaunchActivityTest.this.returnCheckpoint = (Checkpoint)result.getReturn();
                        break;
                    case 1: //reactivate
                        LaunchActivityTest.this.returnActionConfirmation = (Boolean)result.getReturn();
                        break;
                    case 2://getTrackingByNumber
                        LaunchActivityTest.this.returnTracking = (Tracking)result.getReturn();
                        break;
                    case 3://getTracking
                        LaunchActivityTest.this.returnTrackings = (List<Tracking>)result.getReturn();
                        break;
                    case 4://deleteTracking
                        LaunchActivityTest.this.returnActionConfirmation = (Boolean)result.getReturn();
                        break;
                    case 5://postTracking
                        LaunchActivityTest.this.returnTracking = (Tracking)result.getReturn();
                        break;
                    case 6://putTracking
                        LaunchActivityTest.this.returnTracking = (Tracking)result.getReturn();
                        break;
                    case 7://getCouriers(7)
                        LaunchActivityTest.this.returnCoriers = (List<Courier>)result.getReturn();
                        break;
                    case 8://detectCouriers(8)
                        LaunchActivityTest.this.returnCoriers = (List<Courier>)result.getReturn();
                        break;
                    case 9://getTrackingsNext(9)
                        LaunchActivityTest.this.returnTrackings = (List<Tracking>)result.getReturn();
                        break;
                }
            }
        };
        if (firstTime) {
            this.addToCount(3);
            firstTime = false;
            //delete the tracking we are going to post (in case it exist)
            try {
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //create an AsyncTask and execute it (we add the latch countDown).
                        new ConnectionAPI(API_KEY, ConnectionAPIMethods.deleteTracking, listener, "05167019264110", "dpd") {
                            protected void onPostExecute(ConnectionAPI connection) {
                                super.onPostExecute(connection);
                                LaunchActivityTest.this.latch.countDown();
                            }
                        }.execute();
                    }
                });
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //create an AsyncTask and execute it (we add the latch countDown).
                        new ConnectionAPI(API_KEY, ConnectionAPIMethods.deleteTracking, listener, trackingNumberToDetect, "dpd") {
                            protected void onPostExecute(ConnectionAPI connection) {
                                super.onPostExecute(connection);
                                LaunchActivityTest.this.latch.countDown();
                            }
                        }.execute();
                    }
                });
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //create an AsyncTask and execute it (we add the latch countDown).
                        Tracking newTracking = new Tracking(trackingNumberDelete);
                        newTracking.setSlug(slugDelete);
                        new ConnectionAPI(API_KEY, ConnectionAPIMethods.postTracking, listener, newTracking) {
                            protected void onPostExecute(ConnectionAPI connection) {
                                super.onPostExecute(connection);
                                LaunchActivityTest.this.latch.countDown();
                            }
                        }.execute();
                    }
                });

            } catch (Throwable e) {
                System.out.println("**" + e.getMessage());
            }
            boolean await = this.latch.await(30, TimeUnit.SECONDS);
            assertTrue(await);

            this.exception = null;
            this.returnCoriers = null;
            this.returnTracking = null;
            this.returnTrackings = null;
            this.returnCheckpoint = null;
            this.returnActionConfirmation = false;
        }
    }

    public void addToCount(int i) {
        this.latch = new CountDownLatch((int) this.latch.getCount() + i);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetCouriers() throws Throwable {
        // create CountDownLatch for which the test can wait.
        this.addToCount(1);
        // Execute the async task on the UI thread! THIS IS KEY!
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                //calll to getCouriers
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.getCouriers, listener) {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

        boolean await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        //total Couriers returned
        assertEquals("It should return total couriers", 190, this.returnCoriers.size());
        //check first courier
        assertEquals("First courier slug", "india-post-int", this.returnCoriers.get(0).getSlug());
        assertEquals("First courier name", "India Post International", this.returnCoriers.get(0).getName());
        assertEquals("First courier phone", "+91 1800 11 2011", this.returnCoriers.get(0).getPhone());
        assertEquals("First courier other_name", "भारतीय डाक, Speed Post & eMO, EMS, IPS Web", this.returnCoriers.get(0).getOther_name());
        assertEquals("First courier web_url", "http://www.indiapost.gov.in/", this.returnCoriers.get(0).getWeb_url());
    }

    public void testDetectCouriers() throws Throwable {
        // Execute the async task on the UI thread! THIS IS KEY!
        this.addToCount(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                //calll to getCouriers
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.detectCouriers, listener, "09445246482536") {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

        boolean await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        //get trackings of this number.
        assertEquals("It should return 2 couriers", 2, this.returnCoriers.size());
        //the couriers should be dpd or fedex
        assertTrue("It should  have slug equals", this.returnCoriers.get(0).getSlug().equals("dpd")
                || this.returnCoriers.get(1).getSlug().equals("dpd"));
        assertTrue("It should  have slug equals", this.returnCoriers.get(0).getSlug().equals("fedex")
                || this.returnCoriers.get(1).getSlug().equals("fedex"));

        //if the trackingNumber doesn't match any courier defined, should give an error.
        this.addToCount(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                //calll to getCouriers
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.detectCouriers, listener, trackingNumberToDetectError) {

                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });
        await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertEquals("latch should be 0", 0, this.latch.getCount());
        assertEquals("It should return a exception if the tracking number doesn't matching any courier you have defined"
                , "Invalid JSON data. Cannot detect courier. Activate courier at https://www.aftership.com/settings/courier.", this.exception.getMessage());

    }

    public void testPostTracking() throws Throwable {
        this.addToCount(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                //calll to post Courier
                Tracking tracking1 = new Tracking(trackingNumberPost);
                tracking1.setSlug(slugPost);
                tracking1.setOrderIDPath(orderIDPathPost);
                tracking1.setCustomerName(customerNamePost);
                tracking1.setOrderID(orderIDPost);
                tracking1.setTitle(titlePost);
                tracking1.setDestinationCountryISO3(countryDestinationPost);
                tracking1.addEmails(email1Post);
                tracking1.addEmails(email2Post);
                tracking1.addCustomFields("product_name", customProductNamePost);
                tracking1.addCustomFields("product_price", customProductPricePost);
                tracking1.addSmses(sms1Post);
                tracking1.addSmses(sms2Post);

                new ConnectionAPI(API_KEY, ConnectionAPIMethods.postTracking, listener, tracking1) {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });
        boolean await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertEquals("Should be equals TrackingNumber", trackingNumberPost, this.returnTracking.getTrackingNumber());
        assertEquals("Should be equals slug", slugPost, this.returnTracking.getSlug());
        assertEquals("Should be equals orderIDPath", orderIDPathPost, this.returnTracking.getOrderIDPath());
        assertEquals("Should be equals orderID", orderIDPost, this.returnTracking.getOrderID());
        assertEquals("Should be equals countryOrigin", countryDestinationPost,
                this.returnTracking.getDestinationCountryISO3());

        assertTrue("Should contains email", this.returnTracking.getEmails().contains(email1Post));
        assertTrue("Should contains email", this.returnTracking.getEmails().contains(email2Post));
        assertEquals("Should be equals size emails", 2, this.returnTracking.getEmails().size());

        assertTrue("Should contains smses", this.returnTracking.getSmses().contains(sms1Post));
        assertTrue("Should contains smses", this.returnTracking.getSmses().contains(sms2Post));
        assertEquals("Should be equals size smses", 2, this.returnTracking.getSmses().size());

        assertEquals("Should be equals custom field product_name", customProductNamePost,
                this.returnTracking.getCustomFields().get("product_name"));
        assertEquals("Should be equals custom field product_price", customProductPricePost,
                this.returnTracking.getCustomFields().get("product_price"));

        //test post only informing trackingNumber (the slug can be dpd and fedex)
        this.addToCount(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                //calll to getCouriers
                Tracking tracking2 = new Tracking(trackingNumberToDetect);

                new ConnectionAPI(API_KEY, ConnectionAPIMethods.postTracking, listener, tracking2) {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });
        await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue("this is not 0", await);
        assertEquals("latch should be 0", 0, this.latch.getCount());
        assertEquals("Should be equals TrackingNumber", trackingNumberToDetect, this.returnTracking.getTrackingNumber());
        assertEquals("Should be equals slug", "dpd", this.returnTracking.getSlug());//the system assign dpd (it exist)


        //test post tracking number doesn't exist
        this.addToCount(1);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                //calll to getCouriers
                Tracking tracking3 = new Tracking(trackingNumberToDetectError);

                new ConnectionAPI(API_KEY, ConnectionAPIMethods.postTracking, listener, tracking3) {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });
        await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue("this is not 0", await);
        assertEquals("latch should be 0", 0, this.latch.getCount());
        assertEquals("It should return a exception if the tracking number doesn't matching any courier you have defined"
                , "Invalid JSON data. Cannot detect courier. Activate courier at https://www.aftership.com/settings/courier.", this.exception.getMessage());

    }

    public void testDeleteTracking() throws Throwable {
        this.addToCount(1);
        //delete the tracking we are going to post (in case it exist)
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.deleteTracking, listener, trackingNumberDelete, slugDelete) {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

        boolean await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertTrue(this.returnActionConfirmation);
        this.returnActionConfirmation = false;

        //if the slug is bad informed
        this.addToCount(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.deleteTracking, listener, trackingNumberDelete, trackingNumberDelete2) {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });
        await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertEquals("It should return a exception if the slug is not informed properly"
                , "ResourceNotFound. Tracking does not exist.", this.exception.getMessage());
        this.exception = null;

        //if the trackingNumber is bad informed
        this.addToCount(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.deleteTracking, listener, "adfa", "fedex") {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });
        await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertEquals("It should return a exception if the slug is not informed properly"
                , "ResourceNotFound. Tracking does not exist.", this.exception.getMessage());
        this.exception = null;
    }

    public void testGetTracking() throws Throwable {
        this.addToCount(1);
        //delete the tracking we are going to post (in case it exist)
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackings, listener, 1) {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

        boolean await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertEquals("Should receive 100", 100, this.returnTrackings.size());
        assertTrue("TrackingNumber should be informed", !this.returnTrackings.get(0).equals(""));
        assertTrue("TrackingNumber should be informed", !this.returnTrackings.get(98).equals(""));
        Tracking n100o0 = this.returnTrackings.get(0);
        Tracking n100o98 = this.returnTrackings.get(0);

        this.addToCount(1);
        //delete the tracking we are going to post (in case it exist)
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackings, listener, 2) {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

        await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertEquals("Should receive 100", 100, this.returnTrackings.size());
        assertTrue("TrackingNumber should be informed", !this.returnTrackings.get(0).equals(""));
        assertTrue("TrackingNumber should be informed", !this.returnTrackings.get(98).equals(""));

        assertTrue(!n100o0.getTrackingNumber().equals(this.returnTrackings.get(0).getTrackingNumber()));
        assertTrue(!n100o98.getTrackingNumber().equals(this.returnTrackings.get(98).getTrackingNumber()));
    }

    public void testGetTracking2() throws Throwable {

        this.addToCount(1);
        //delete the tracking we are going to post (in case it exist)
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                ParametersTracking param = new ParametersTracking();
                param.addSlug("dhl");
                Date date = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.MONTH,-1);
                date = c.getTime();
                param.setCreatedAtMin(date);

                new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackings, listener, param) {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });
        boolean await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
//        assertTrue("Exception "+this.exception.getMessage(),this.exception==null);
        assertEquals("Should be 35 trackings", 35, this.returnTrackings.size());


        this.addToCount(1);
        //delete the tracking we are going to post (in case it exist)
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                ParametersTracking param1 = new ParametersTracking();
                param1.addDestination(ISO3Country.ESP);

                new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackings, listener, param1) {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });
        await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
//        assertTrue("Exception "+this.exception.getMessage(),this.exception==null);
        assertEquals("Should be 23 trackings", 23, this.returnTrackings.size());

        this.addToCount(1);
        //delete the tracking we are going to post (in case it exist)
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                ParametersTracking param2 = new ParametersTracking();
                param2.addTag(StatusTag.OutForDelivery);
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackings, listener, param2) {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });
        await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertEquals("Should be 3 trackings", 3, this.returnTrackings.size());
        this.returnTrackings = null;

        this.addToCount(1);
        //delete the tracking we are going to post (in case it exist)
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                ParametersTracking param4 = new ParametersTracking();
                param4.setLimit(50);
                param4.addTag(StatusTag.InTransit);
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackingsNext, listener, param4) {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

        await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertNull(this.exception);
        //because should be 83 in total, and we are making the page limit as 50
        assertEquals("Should be 32 trackings",32 , this.returnTrackings.size());

    }

    public void testGetTrackingByNumber()throws Throwable {

        this.addToCount(1);
        //delete the tracking we are going to post (in case it exist)
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackingByNumber, listener, "RC328021065CN", "canada-post") {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

        boolean await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertNull(this.exception);

        assertEquals("Should be equals TrackingNumber", "RC328021065CN", this.returnTracking.getTrackingNumber());
        assertEquals("Should be equals Slug", "canada-post", this.returnTracking.getSlug());
        assertEquals("Should be equals type", "Lettermail", this.returnTracking.getShipmentType());

        //slug is bad informed
        this.addToCount(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackingByNumber, listener, "RC328021065CN", "") {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

         await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertEquals("It should return a exception if the slug is not informed properly",
                "ResourceNotFound. The requested resource does not exist.", this.exception.getMessage());

        this.addToCount(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackingByNumber, listener, "adf", "fedex") {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

        await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertEquals("It should return a exception if the slug is not informed properly"
                , "ResourceNotFound. Tracking does not exist.", this.exception.getMessage());

    }

    public void testPutTracking()throws Throwable{

        this.addToCount(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                Tracking tracking = new Tracking("RC328021065CN");
                tracking.setSlug("canada-post");
                tracking.setTitle("another title");
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.putTracking, listener,tracking) {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

        boolean await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertNull(this.exception);
        assertEquals("Should be equals title", "another title", this.returnTracking.getTitle());

        //test post tracking number doesn't exist
        this.addToCount(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                Tracking tracking3 = new Tracking(trackingNumberToDetectError);
                tracking3.setTitle("another title");
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.putTracking, listener,tracking3) {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

         await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
            assertEquals("It should return a exception if the tracking number doesn't matching any courier you have defined"
                    , "ResourceNotFound. Tracking does not exist.", this.exception.getMessage());

    }

    public void testReactivate()throws Throwable{
//        this.addToCount(1);
//        runTestOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                //create an AsyncTask and execute it (we add the latch countDown).
//                new ConnectionAPI(API_KEY, ConnectionAPIMethods.reactivate, listener,"92748999985541553009345515","usps") {
//                    protected void onPostExecute(ConnectionAPI connection) {
//                        super.onPostExecute(connection);
//                        LaunchActivityTest.this.latch.countDown();
//                    }
//                }.execute();
//            }
//        });
//
//        boolean await = this.latch.await(30, TimeUnit.SECONDS);
//        assertTrue(await);
//        assertTrue(this.returnActionConfirmation);

        //try reactivate one already active
        this.addToCount(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.reactivate, listener,"RT224265042HK","hong-kong-post") {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

        boolean await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertEquals("Should be equals message",
                "InvalidArgument. Reactivate is not allowed. You can only reactivate an expired tracking.",
                this.exception.getMessage());

//        //slug is bad informed
        this.addToCount(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.reactivate, listener,"RT224265042HK","") {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

        await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertEquals("It should return a exception if the slug is not informed properly"
                    , "ResourceNotFound. The requested resource does not exist.", this.exception.getMessage());

//        //if the trackingNumber is bad informed
        this.addToCount(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.reactivate, listener,"adf", "fedex") {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

        await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
            assertEquals("It should return a exception if the slug is not informed properly"
                    , "ResourceNotFound. Tracking does not exist.", this.exception.getMessage());

    }

    public void testGetLastCheckpoint()throws Throwable{

        this.addToCount(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.getLastCheckpoint, listener,"GM605112270084510370", "dhl-global-mail") {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

        boolean await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertNull(exception);
        assertEquals("Should be equals message", "Delivered", this.returnCheckpoint.getMessage());
        assertEquals("Should be equals city name", "BUDERIM QLD, AU", this.returnCheckpoint.getCountryName());
        assertEquals("Should be equals tag", "Delivered", this.returnCheckpoint.getTag());

        //slug is bad informed
        this.addToCount(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.getLastCheckpoint, listener,"GM605112270084510370", "dhl--mail") {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

        await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertEquals("It should return a exception if the slug is not informed properly"
                    , "ResourceNotFound. Tracking does not exist.", this.exception.getMessage());

        //if the trackingNumber is bad informed
        this.addToCount(1);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                //create an AsyncTask and execute it (we add the latch countDown).
                new ConnectionAPI(API_KEY, ConnectionAPIMethods.getLastCheckpoint, listener,"ads", "dhl--mail") {
                    protected void onPostExecute(ConnectionAPI connection) {
                        super.onPostExecute(connection);
                        LaunchActivityTest.this.latch.countDown();
                    }
                }.execute();
            }
        });

        await = this.latch.await(30, TimeUnit.SECONDS);
        assertTrue(await);
        assertEquals("It should return a exception if the slug is not informed properly"
                    , "ResourceNotFound. Tracking does not exist.", this.exception.getMessage());

    }


}
