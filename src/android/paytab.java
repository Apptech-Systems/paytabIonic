package cordova.plugin.paytab;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;
import java.util.Locale;
import android.util.DisplayMetrics;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.text.DecimalFormat;


import com.paytabs.paytabs_sdk.payment.ui.activities.PayTabActivity;
import com.paytabs.paytabs_sdk.utils.PaymentParams;

public class paytab extends CordovaPlugin {

  CallbackContext callback;

  @Override
  public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
    this.callback = callbackContext;
    if (action.equals("add")) {

      JSONObject args = data.getJSONObject(0);

      Log.i("Card Data: ", args.toString());

      Log.d("Language: ",args.getString("language"));

      String lang = "ar";

      if (args.getString("language").equalsIgnoreCase("English")) {
        lang = "en";
      } else {
        lang = "ar";
      }

      Locale myLocale = new Locale(lang);
      Resources res = cordova.getActivity().getResources();
      DisplayMetrics dm = res.getDisplayMetrics();
      Configuration conf = res.getConfiguration();
      conf.locale = myLocale;
      res.updateConfiguration(conf, dm);

      double aDouble = Double.parseDouble(args.getString("amount"));

      Intent in = new Intent(this.cordova.getActivity(), PayTabActivity.class);
      in.putExtra(PaymentParams.MERCHANT_EMAIL, args.getString("merchantEmail"));
      in.putExtra(PaymentParams.SECRET_KEY,args.getString("secretKey"));
      in.putExtra(PaymentParams.LANGUAGE,lang);
      in.putExtra(PaymentParams.TRANSACTION_TITLE, args.getString("transactionTitle"));
      in.putExtra(PaymentParams.AMOUNT, aDouble);

      in.putExtra(PaymentParams.CURRENCY_CODE, args.getString("currency"));
      in.putExtra(PaymentParams.CUSTOMER_PHONE_NUMBER, args.getString("customer_phone_number"));
      in.putExtra(PaymentParams.CUSTOMER_EMAIL, args.getString("customer_email"));
      in.putExtra(PaymentParams.ORDER_ID, args.getString("order_id"));
      in.putExtra(PaymentParams.PRODUCT_NAME, args.getString("product_name"));

      //Billing Address
      in.putExtra(PaymentParams.ADDRESS_BILLING,  args.getString("address_billing"));
      in.putExtra(PaymentParams.CITY_BILLING,  args.getString("city_billing"));
      in.putExtra(PaymentParams.STATE_BILLING,  args.getString("state_billing"));
      in.putExtra(PaymentParams.COUNTRY_BILLING,  args.getString("country_shipping"));
      in.putExtra(PaymentParams.POSTAL_CODE_BILLING,  args.getString("postal_code_billing")); //Put Country Phone code if Postal code not available '00973'

      //Shipping Address
      in.putExtra(PaymentParams.ADDRESS_SHIPPING,  args.getString("address_billing"));
      in.putExtra(PaymentParams.CITY_SHIPPING,  args.getString("city_billing"));
      in.putExtra(PaymentParams.STATE_SHIPPING,  args.getString("state_billing"));
      in.putExtra(PaymentParams.COUNTRY_SHIPPING,  args.getString("country_shipping"));
      in.putExtra(PaymentParams.POSTAL_CODE_SHIPPING,  args.getString("postal_code_billing")); //Put Country Phone code if Postal code not available '00973'

      //Payment Page Style
      in.putExtra(PaymentParams.PAY_BUTTON_COLOR, "#2474bc");

      cordova.startActivityForResult((CordovaPlugin) this, in, 0);
    }

    return true;
  }

  private boolean getResult(CallbackContext callbackContext) throws JSONException {
    SharedPreferences shared_prefs = cordova.getActivity().getApplicationContext().getSharedPreferences("myapp_shared", Context.MODE_PRIVATE);

    String pt_response_code = shared_prefs.getString(PaymentParams.RESPONSE_CODE,"");
    String pt_transaction_id = shared_prefs.getString(PaymentParams.TRANSACTION_ID, "");
    Log.d("Response Code: ",pt_response_code);
    Map<String,String> object = new HashMap<String,String>();
    object.put("response_code", pt_response_code);
    object.put("transaction_id", pt_transaction_id);

    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, new JSONObject(object)));
    return true;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    int RESULT_OK = 0;

    if (resultCode == RESULT_OK && requestCode == PaymentParams.PAYMENT_REQUEST_CODE) {
      if (data.hasExtra(PaymentParams.TOKEN) && !data.getStringExtra(PaymentParams.TOKEN).isEmpty()) {
        Log.e("Tag", data.getStringExtra(PaymentParams.TOKEN));
        Log.e("Tag", data.getStringExtra(PaymentParams.CUSTOMER_EMAIL));
        Log.e("Tag", data.getStringExtra(PaymentParams.CUSTOMER_PASSWORD));
      }
    }

    String pt_response_code ="0";
    String pt_transaction_id = "0";
    try
    {
      pt_response_code = data.getStringExtra(PaymentParams.RESPONSE_CODE);
      pt_transaction_id = data.getStringExtra(PaymentParams.TRANSACTION_ID);
      //hesaplanmak istenen ifade
    }
    catch(Exception ex)
    {
      //Bir hata türü tespit edilince verilmesi gereken mesaj
    }

    Map<String,String> object = new HashMap<String,String>();
    object.put("response_code", pt_response_code);
    object.put("transaction_id", pt_transaction_id);
    this.callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, new JSONObject(object)));
//
//        Log.println(requestCode,"Hello","Hello");
//        Log.println(resultCode,"Hello","Hello");
//        SharedPreferences shared_prefs = cordova.getActivity().getApplicationContext().getSharedPreferences("myapp_shared", Context.MODE_PRIVATE);
//            String pt_response_code = shared_prefs.getString("pt_response_code", "");
//            String pt_transaction_id = shared_prefs.getString("pt_transaction_id", "");
//
//        Log.d("Response Code: ",pt_response_code);
//        Log.d("Response Code: ",pt_transaction_id);
//
//        Map<String,String> object = new HashMap<String,String>();
//        object.put("response_code", pt_response_code);
//        object.put("transaction_id", pt_transaction_id);
//
//        this.callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, new JSONObject(object)));

  }

}
