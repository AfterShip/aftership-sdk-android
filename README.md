aftership-android
=================

The Android SDK of AfterShip API


Quick Start
--------------

Tips:

- Declare your API KEY as a static final variable
- Every time you want to make a request, create and execute it right away.
- Don't reuse request.
- Always control the Exceptions in the result. And be careful with the casts.
- Implement in your activites AsyncTaskCompleteListener<ConnectionAPI>, that will force to create the method 
onTaskComplete(ConnectionAPI result), is where the Asynchronous return will be send.
- All the Constructors of methods has as parameters:
    - Key: To link to that account.
    - ConnectionAPIMethods: To specific with action do in the account.
    - Listener: To tell the method what to execute when it finished, usually is "this", so the method at complexion
      will execute onTaskComplete of the this class.


**Get a list of supported couriers**

    static final String API_KEY ="a61d??04-????-????-????-86c4f8??6b";

    //We can call the method from onCreated
    public void onCreate(Bundle savedInstanceState) {
        //Create and execute a petition
        new ConnectionAPI(API_KEY, ConnectionAPIMethods.getALLCouriers, this).execute();
    }
    
    //define the method onTaskComplete in your Activity
    public void onTaskComplete(ConnectionAPI result) {
    
        //Control the exception of the result
        if (result.getException()!=null){
            System.out.println(result.getException().getMessage());//Do something with the exception
        }

        //Every method has a number associate, getCouriers is 7
        switch (result.getMethod().getNumberMethod()) {
            case 10://getAllCouriers(10)
                List<Courier> couriers = (List<Courier>) result.getReturn();//All the Couriers supported by Aftership
                break;
        }
    }
    
**Get a list of the couriers in your account**

	new ConnectionAPI(API_KEY, ConnectionAPIMethods.getCouriers, this).execute();
	
	public void onTaskComplete(ConnectionAPI result) {
        switch (result.getMethod().getNumberMethod()) {
            case 7://getCouriers(7)
                List<Courier> couriers = (List<Courier>) result.getReturn();//All the Couriers supported by Aftership
                break;
        }
	}

**Detect which couriers defined in your account match a tracking number**

    //Do this call whenever you want
    new ConnectionAPI(API_KEY, ConnectionAPIMethods.detectCouriers, this, "09445246482536").execute();

    public void onTaskComplete(ConnectionAPI result) {
        //Remember to control a possible Exception
    
        switch (result.getMethod().getNumberMethod()) {
            case 8://detectCouriers(8)
                 List<Courier> couriers = (List<Courier>) result.getReturn(); //The detected Couriers
                break;
        }
    }


**Post a tracking to your account**

	//First we have to create a Tracking
	Tracking tracking1 = new Tracking("05167019264110");

	//Then we can add information;
    tracking1.setSlug("dpd");
    tracking1.setTitle("this title");
    tracking1.addEmails("email@yourdomain.com");
    tracking1.addEmails("another_email@yourdomain.com");
    tracking1.addSmses("+85292345678");
    tracking1.addSmses("+85292345679");

	//Even add customer fields
    tracking1.addCustomFields(“product_name”,"iPhone Case");
    tracking1.addCustomFields(“product_price”,"USD19.99");

	//Finally we add the tracking to our account
    new ConnectionAPI(API_KEY, ConnectionAPIMethods.postTracking, this, tracking1).execute();

	    public void onTaskComplete(ConnectionAPI result) {
            //Remember to control a possible Exception
        
            switch (result.getMethod().getNumberMethod()) {
                case 5://postTracking
                	//In the response we will have exactly the information of the server
                    trackingPosted =  (Tracking) result.getReturn(); //The posted tracking
                    trackingPosted.getTrackingNumber();//get information of the tracking
                    trackingPosted.getSlug();//get information of the tracking
                    break;
            }
        }


**Delete a tracking of your account**

	
	Tracking trackingDelete = new Tracking("123456789");//tracking number
	trackingDelete.setSlug("dhl");
    new ConnectionAPI(API_KEY, ConnectionAPIMethods.deleteTracking, this, trackingDelete.setSlug).execute();
    
    public void onTaskComplete(ConnectionAPI result) {
        //Remember to control a possible Exception
    
        switch (result.getMethod().getNumberMethod()) {
            case 4://deleteTracking
                boolean correct =  (Boolean) result.getReturn(); //True if correct, exception or false if not
                break;
        }
    }

**Get trackings of your account, there is two ways**

	//1- Simplest way, with the page you want to get

    new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackings, this, 1).execute()//get first 100
	new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackings, this, 1).execute()// get 100-200
	//If you delete tracings right before, you may get less number.


	//2- Using Parameters tracking

	//Create a new Parameter
	ParametersTracking param = new ParametersTracking();

	//Add the information we want in the parameter
	param.addSlug("dhl");//Add slug to our parameters
	Date date = new Date();//Create a date with value of now
	Calendar c = Calendar.getInstance();
	c.setTime(date);
    c.add(Calendar.MONTH,-1); //Substract a Month to the date
    date = c.getTime();
    param.setCreatedAtMin(date);//SetCreadtedMin to the date of one month ago

	//Get the first page of trackings in your account from dhl and created less than a month ago.
    new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackings, this, param).execute();

	//if the get has several pages, you can either modify the page you want in your get with param.setPage(page), or
	call getTrackingsNext(param1) instead, it automatically increase the page , example:

	//Get trackings with destination Spain, total 23
	ParametersTracking param1 = new ParametersTracking();
    param1.addDestination(ISO3Country.ESP);
    param1.setLimit(20);//set limit of the page to 20
    new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackings, this, param1).execute();//We will receive the 20 first
    new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackingsNext, this, param1).execute(); //We will receive the next 3
    int total = param1.getTotal(); // we will receive the total of trackings, 23;

	//Get trackings that are OutForDelivery
    ParametersTracking param2 = new ParametersTracking();
    param2.addTag(StatusTag.OutForDelivery);
    new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackings, this, param2).execute();

    //Receive the trackings after the petition:
    
    public void onTaskComplete(ConnectionAPI result) {
        //Remember to control a possible Exception
        
        switch (result.getMethod().getNumberMethod()) {
            case 3://getTracking(3)
                List<Tracking> trackings =(List<Tracking>) result.getReturn();
                break;
            case 9://getTrackingsNext(9), when we call getTrackingNext, we will receive them here.
                List<Tracking> trackingsNext =(List<Tracking>) result.getReturn();
                break;    
        }

**Get a tracking from your account**

	Tracking trackingToGet = new Tracking("RC328021065CN");
	trackingToGet.setSlug("canada-post");
    new ConnectionAPI(API_KEY, ConnectionAPIMethods.getTrackingByNumber, this,trackingToGet).execute();

    public void onTaskComplete(ConnectionAPI result) {
        //Remember to control a possible Exception
    
        switch (result.getMethod().getNumberMethod()) 
            case 2://getTrackingByNumber
                Tracking tracking = (Tracking) result.getReturn();
                break;
        }
    }

**Modify a tracking from your account**

	//Create a tracking
	Tracking tracking = new Tracking("RC328021065CN");
    tracking.setSlug("canada-post");
    //Add the fields we want to modify
    tracking.setTitle("another title");

    new ConnectionAPI(API_KEY, ConnectionAPIMethods.putTracking, this, tracking).execute();
    
    public void onTaskComplete(ConnectionAPI result) {
        //Remember to control a possible Exception
  
        switch (result.getMethod().getNumberMethod()) {
            case 6://putTracking
                //Returns a tracking with exactly the information of the server
                Tracking tracking2 = (Tracking) result.getReturn();
                tracking2.getTitle();//Value “another title”
                break;
        }
    }  

**Retrack a tracking of your account**

	Tracking tracking = new Tracking("RT224265042HK");
    tracking.setSlug("hong-kong-post");
    
    new ConnectionAPI(API_KEY, ConnectionAPIMethods.retrack, this,tracking).execute();
    public void onTaskComplete(ConnectionAPI result) {
        //Remember to control a possible Exception
        
        switch (result.getMethod().getNumberMethod()) {
            case 1: //retrack
            	boolean answer = (Boolean) result.getReturn();//True if correct, false or exception otherwise
                break;
        }
    }   


**Get the last checkpoint of a tracking of your account**

	Tracking tracking = new Tracking("GM605112270084510370");
    tracking.setSlug("dhl-global-mail");
    new ConnectionAPI(API_KEY, ConnectionAPIMethods.getLastCheckpoint, this, tracking).execute();

    public void onTaskComplete(ConnectionAPI result) {
        //Remember to control a possible Exception
        
        switch (result.getMethod().getNumberMethod()) {
            case 0://getLastCheckpoint
                Checkpoint newCheckpoint = (Checkpoint) result.getReturn();//True if correct, false or exception otherwise
                newCheckpoint.getMessage()//"Delivered"
                newCheckpoint.getCountryName()//"BUDERIM QLD, AU"
                newCheckpoint.getTag()//"Delivered"
                break;
        }
    }  

**Summary of onTaskComplete with all the cases and possible returns**


    public void onTaskComplete(ConnectionAPI result) {
        if (result.getException()!=null)
            System.out.println(result.getException().getMessage());//do something with the exception

        switch (result.getMethod().getNumberMethod()) {
            case 0://getLastCheckpoint
                Checkpoint returnCheckpoint = (Checkpoint)result.getReturn();
                break;
            case 1: //reactivate
                boolean returnActionConfirmation = (Boolean)result.getReturn();
                break;
            case 2://getTrackingByNumber
                Tracking returnTracking = (Tracking)result.getReturn();
                break;
            case 3://getTracking
                List<Tracking> returnTrackings = (List<Tracking>)result.getReturn();
                break;
            case 4://deleteTracking
                boolean returnActionConfirmation = (Boolean)result.getReturn();
                break;
            case 5://postTracking
                Tracking returnTracking = (Tracking)result.getReturn();
                break;
            case 6://putTracking
                Tracking returnTracking = (Tracking)result.getReturn();
                break;
            case 7://getCouriers(7)
                List<Couriers> returnCouriers = (List<Courier>)result.getReturn();
                break;
            case 8://detectCouriers(8)
                List<Couriers> returnCouriers = (List<Courier>)result.getReturn();
                break;
            case 9://getTrackingsNext(9)
                List<Tracking> returnTrackings = (List<Tracking>)result.getReturn();
                break;
           case 10://getAllCouriers(10)
                List<Couriers> returnCouriers = (List<Courier>)result.getReturn();
                break;
        }
    }