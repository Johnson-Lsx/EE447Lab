package com.example.iiot;

/*****************************************************************************************************************
 * Created by HelloShine on 2019-3-24.
 * ***************************************************************************************************************/
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log; //Log can be utilized for debug.

public class SuperWiFi extends MainActivity{

    /*****************************************************************************************************************
     * When you run the APP in your mobile phone, you can utilize the following code for debug:
     * Log.d("TEST_INFO","Your Own String Type Content Here");
     * You can also generate the String via ("String" + int/double value). for example, "CurTime " + 20 = "CurTime 20"
     * ***************************************************************************************************************/
    private String FileLabelName = "IIOT_test";// Define the file Name
    /*****************************************************************************************************************
     * You can define the Wi-Fi SSID to be measured in FileNameGroup, more than 2 SSIDs are OK.
     * It is noting that multiple Wi-Fi APs might share the same SSID such as SJTU.
     * ***************************************************************************************************************/
    private String FileNameGroup[] = {"zxm888888", "360WiFi-01E6FC", "TP-LINK_6DCDEAAA"};

    private int TestTime = 10;//Number of measurement
    private int ScanningTime = 500;//Wait for (?) ms for next scan

    private int NumberOfWiFi = FileNameGroup.length;

    // RSS_Value_Record and RSS_Measurement_Number_Record are used to record RSSI values
    private int[] RSS_Value_Record = new int[NumberOfWiFi];
    private int[] RSS_Measurement_Number_Record = new int[NumberOfWiFi];


    private WifiManager mWiFiManager = null;
    private Vector<String> scanned = null;
    boolean isScanning = false;
    float pos_x, pos_y;
    boolean flag = true;

    public SuperWiFi(Context context)
    {
        this.mWiFiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        this.scanned = new Vector<String>();
    }

    private void startScan()//The start of scanning
    {
        this.isScanning = true;
        Thread scanThread = new Thread(new Runnable()
        {
            public void run() {
                scanned.clear();//Clear last result
                for(int index = 1;index <= NumberOfWiFi; index++){
                    RSS_Value_Record[index - 1] = 0;
                    RSS_Measurement_Number_Record[index - 1] = 1;
                }
                int CurTestTime = 1; //Record the test time and write into the SD card
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis()); //Get the current time
                String CurTimeString = formatter.format(curDate);
                for(int index = 1;index <= NumberOfWiFi; index++){
                    write2file(FileLabelName + "-" + FileNameGroup[index - 1] + ".txt","Test_ID: " + testID + " TestTime: " + CurTimeString + " BEGIN\r\n");
                }
                //Scan for a certain times
                while(CurTestTime++ <= TestTime) performScan();

                for(int index = 1;index <= NumberOfWiFi; index++){//Record the average of the result
                    scanned.add(FileLabelName + "-" + FileNameGroup[index - 1] + " = " + RSS_Value_Record[index - 1]/ RSS_Measurement_Number_Record[index - 1] + "\r\n");
                }
                //*****************************************************************************************************************

                 //You can insert your own code here for localization.
                flag = true;
                if (flag) {
                    for(int index = 1;index <= NumberOfWiFi; index++){ RSS_Value_Record[index - 1] /= RSS_Measurement_Number_Record[index - 1]; }
                    float A = 60;
                    float n = (float) 3.25;
                    double d_1 = Math.pow(10, ((-1 * RSS_Value_Record[0]) - A)
                                / (RSS_Measurement_Number_Record[0] * n));
                    double d_2 = Math.pow(10, ((-1 * RSS_Value_Record[1]) - A)
                                / (RSS_Measurement_Number_Record[1] * n));
                    double d_3 = Math.pow(10, ((-1 * RSS_Value_Record[2]) - A)
                                / (RSS_Measurement_Number_Record[2] * n));
                    // positions of 3 routers
                    double x_1=0, y_1=0, x_2=3, y_2=0, x_3=1.5, y_3=6;
                     // compute user position
                    double x = (Math.pow(d_1, 2) - Math.pow(d_2, 2)
                                - (Math.pow(x_1, 2) - Math.pow(x_2, 2))
                                - (Math.pow(y_1, 2) - Math.pow(y_2, 2)) ) / (2*x_1 - 2*x_2);
                    double y1_ = (d_1*d_1 - x*x - 2*x_1*x - x_1*x_1);
                    if (y1_<0) {y1_ = 0;} else {y1_= Math.pow(y1_, 0.5);}
                    double y2_ = (d_2 * d_2 - x*x - 2*x_2*x - x_2*x_2);
                    if (y2_<0) {y2_ = 0;} else {y2_ = - Math.pow(y2_, 0.5);}
                         Log.e("test pos",Double.toString(d_1)+" "+Double.toString(d_2)+" "+Double.toString(x)+" "+Double.toString(y1_)+" "+Double.toString(y2_));
                    if (flag) {
                         pos_x = (float)x;
                         //计算和第三个点之间的距离
                         float diff2 = (float) Math.abs(Math.pow(x-x_3, 2) + Math.pow(y2_-y_3, 2)- d_3);
                         float diff1 = (float) Math.abs(Math.pow(x-x_3, 2) + Math.pow(y1_-y_3, 2)- d_3);
                         pos_y = (float) ((diff2<diff1)? y2_ : y1_);
                    }
                 }


                 //* ***************************************************************************************************************/
                for(int index = 1;index <= NumberOfWiFi; index++){//Mark the end of the test in the file
                    write2file(FileLabelName + "-" + FileNameGroup[index - 1] + ".txt","testID:"+testID+"END\r\n");
                }
                isScanning=false;
            }
        });
        scanThread.start();
    }

    private void performScan()//The realization of the test
    {
        if(mWiFiManager == null)
            return;
        try
        {
            if(!mWiFiManager.isWifiEnabled())
            {
                mWiFiManager.setWifiEnabled(true);
            }
            mWiFiManager.startScan();//Start to scan
            Log.e("SCAN","tot 10 scanning time, NORMAL scanning!!!!!!!!!!XXXXXXXXXXX");
            try {
                Thread.sleep(ScanningTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.scanned.clear();
            List<ScanResult> sr = mWiFiManager.getScanResults();
            Iterator<ScanResult> test = sr.iterator();
//            while(test.hasNext()){ScanResult ap = test.next(); Log.e("FOREACH", ap.SSID);}
            Iterator<ScanResult> it = sr.iterator();
            while(it.hasNext())
            {
                ScanResult ap = it.next();
//                Log.e("SSID NOW", ap.SSID);
                for(int index = 1;index <= FileNameGroup.length; index++){

                    if (ap.SSID.equals(FileNameGroup[index - 1])){
//                        Log.e("LOG_INFO","RECORDINGXXXXXXXXXXX");//Write the result to the file
                        RSS_Value_Record[index-1] = RSS_Value_Record[index-1] + ap.level;
                        RSS_Measurement_Number_Record[index - 1]++;
                        write2file(FileLabelName + "-" + FileNameGroup[index - 1] + ".txt",ap.level+"\r\n");
                    }
                }
            }
        }
        catch (Exception e)
        {
            this.isScanning = false;
            this.scanned.clear();
        }
    }

    public float getPos_x(){
        return pos_x;
    }
    public float getPos_y(){
        return pos_y;
    }
    public boolean isValid(){
        return flag;
    }
    public void ScanRss(){
        startScan();
    }
    public boolean isscan(){
        return isScanning;
    }
    public Vector<String> getRSSlist(){
        return scanned;
    }

    private void write2file(String filename, String a){//Write to the SD card
        try {
            File file = new File("/sdcard/"+filename);
            if (!file.exists()){
                file.createNewFile();} // Open a random filestream by Read&Write
            RandomAccessFile randomFile = new
                    RandomAccessFile("/sdcard/"+filename, "rw"); // The length of the file(byte)
            long fileLength = randomFile.length(); // Put the writebyte to the end of the file
            randomFile.seek(fileLength);
            randomFile.writeBytes(a);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}