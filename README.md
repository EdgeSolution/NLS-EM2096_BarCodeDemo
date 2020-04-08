#  This app is a sample app that teaches you how to receive the scan result of the NewLand EM2096 module on the AIM-68CT device and how to trigger the scan.

##  If you want to use the app, first make sure that your device has NewLand EM2096 module and EM2096BarCode app is installed. If your system does not have EM2096BarCode app installed, please install EM2096BarCode.apk in the current directory first.

##  If you want to develop your own app to receive the scan results of the NewLand EM2096 module, you can refer to the following method.

### Make sure your system has EM2096BarCode app installed. If your system does not have EM2096BarCode app installed, please install EM2096BarCode.apk in the current directory first.

### The method to start the Service App is as follows:

    private static final String SERVICE_PACKAGE_NAME = "com.advantech.em2096barcode";
    private static final String SERVICE_CLASS_NAME = "com.advantech.em2096barcode.RunEm2096BarcodeService";
    private static final String ACTION_START_SERVICE = "com.advantech.em2096barcode.START_SERVICE";

    Intent intent = new Intent(ACTION_START_SERVICE);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setComponent(new ComponentName(SERVICE_PACKAGE_NAME,SERVICE_CLASS_NAME));
        }
        sendBroadcast(intent);
    
### The method of triggering the scan is as follows:

    private static final String ACTION_TRIGGER_SCAN = "com.advantech.em2096barcode.TRIGGER_SCAN";
    Intent intent = new Intent(ACTION_TRIGGER_SCAN);
    intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
    sendBroadcast(intent);
    
### The method of receiving scan results is as follows:

    private static final String ACTION_TRANSFER_DATA = "com.advantech.em2096barcode.TRANSFER_DATA";
    BarCodeDataBroadcastReceiver barCodeDataBroadcastReceiver;

    IntentFilter filter = new IntentFilter();
    filter.addAction(ACTION_TRANSFER_DATA);
    barCodeDataBroadcastReceiver = new BarCodeDataBroadcastReceiver();
    registerReceiver(barCodeDataBroadcastReceiver,filter);

    private  class BarCodeDataBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String barcodeData = intent.getStringExtra("barcodeData");
            if(barcodeData != null){
                textView.append(barcodeData + "\n");
            }
        }
    }
