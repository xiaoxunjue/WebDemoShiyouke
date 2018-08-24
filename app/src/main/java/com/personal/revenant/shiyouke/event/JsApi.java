package com.personal.revenant.shiyouke.event;

import android.app.FragmentManager;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by Administrator on 2018/8/23.
 */

public class JsApi {
    private JsCallback jsCallback;

    public JsApi(JsCallback callback) {
        this.jsCallback = callback;
    }


    @JavascriptInterface
    public void toPay(Object params) {
        Log.d("---->>>>", params.toString());
        jsCallback.jsGetAlipayParams(params);
    }

    @JavascriptInterface
    public void uploadPhoto(Object params) {
        jsCallback.jsTakeUpload(params);
    }

    @JavascriptInterface
    public void uploadPhotos(Object params) {
        Log.d("---->>>>", params.toString());
        jsCallback.jsTakeUploads(params);
    }

    @JavascriptInterface
    public void share(Object params) {
        Log.d("---->>>>", params.toString());
        jsCallback.takeShare(params);
    }

    @JavascriptInterface
    public void third_party_login(Object params) {
        Log.d("---->>>>", params.toString());
        jsCallback.third_party_login(params);
    }

    @JavascriptInterface
    public void copyordercode(Object params) {
        Log.d("---->>>>", params.toString());
        jsCallback.takeShare(params);
    }


    @JavascriptInterface
    public void canExit(Object params) {
        Log.d("jsApi: params ---->>>>", params.toString());
        jsCallback.takeExit(params);
    }


    public interface JsCallback {
        /**
         * 调用支付
         **/
        void jsGetAlipayParams(Object params);

        /**
         * 上传头像
         **/
        void jsTakeUpload(Object params);

        /**
         * 多图上传
         **/
        void jsTakeUploads(Object params);

        /**
         * 分享
         **/
        void takeShare(Object params);

        /**
         * 拷贝到剪切板
         **/
        void takeCopy(Object params);

        /**
         * 是否可以退出
         **/
        void takeExit(Object params);

        void  third_party_login(Object params);
    }
}
