package __PACKAGE_NAME__;

import org.apache.cordova.PluginResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.qdc.plugins.weixin.WeixinPay;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
  
  private static final String LOG_TAG = WXPayEntryActivity.class.getSimpleName();
  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WeixinPay.wxAPI.handleIntent(getIntent(), this);
    }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    WeixinPay.wxAPI.handleIntent(intent, this);
  }

  @Override
  public void onReq(BaseReq req) {
    finish();
  }

  @Override
  public void onResp(BaseResp resp) {
    Log.d(LOG_TAG, "onPayFinish, errCode = " + resp.errCode);

    if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
      
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "微信支付结果：" + resp.errStr +";code=" + String.valueOf(resp.errCode));
            result.setKeepCallback(true);
            WeixinPay.cbContext.sendPluginResult(result);
    }

    finish();
  }
}
