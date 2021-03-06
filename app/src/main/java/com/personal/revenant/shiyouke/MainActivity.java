package com.personal.revenant.shiyouke;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.apkfuns.logutils.LogUtils;
import com.google.gson.Gson;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.nanchen.compresshelper.CompressHelper;
import com.personal.revenant.shiyouke.bean.PhoneInfo;
import com.personal.revenant.shiyouke.bean.PhotoOne;
import com.personal.revenant.shiyouke.bean.PhotoOneGet;
import com.personal.revenant.shiyouke.bean.TestGoodsBean;
import com.personal.revenant.shiyouke.bean.WeiXinBean;
import com.personal.revenant.shiyouke.event.JsApi;
import com.personal.revenant.shiyouke.utils.GlideImageLoader;
import com.personal.revenant.shiyouke.utils.GsonUtil;
import com.personal.revenant.shiyouke.utils.LoadDialog;
import com.personal.revenant.shiyouke.utils.PayResult;
import com.personal.revenant.shiyouke.utils.PhoneUtil;
import com.personal.revenant.shiyouke.utils.Utils;
import com.personal.revenant.shiyouke.wxapi.WXPayUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.utils.SocializeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import wendu.dsbridge.DWebView;

import static com.personal.revenant.shiyouke.utils.Constant.BASE;

public class MainActivity extends Activity implements JsApi.JsCallback, UMShareListener {

    private RelativeLayout loadView;
    private SmartRefreshLayout smartRefreshLayout;
    private boolean firstLoad = true;
    protected LoadDialog loadDialog;
    private AgentWeb mAgentWeb;
    private ProgressDialog dialog;
    private DWebView dWebView;
    private String url;
    private Context context;
    private static final int IMAGE_PICKER = 300;
    private Button button;
    private List<PhoneInfo> list;
    private ImagePicker imagePicker;
    private int type_photo;
    private String userid;
    private String token;
    private static final int SDK_PAY_FLAG = 1;
//    private ImagePicker imagePicker;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private String goodinfo;

//初始化定位


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWight();

        context = this;
//        initLocation();
        Utils.setImageStatus(this);
        setContentView(R.layout.activity_main);
        setStatusBg(R.color.colorPrimary);
        url = BASE;

        initView();

        LogUtils.d("111111111111111");
    }


    private void initView() {
        button = findViewById(R.id.test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                aipay();
//                imagePicker.setSelectLimit(5);

//                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 201);
//                } else {
//
//                    PhoneUtil phoneUtil = new PhoneUtil(context);
////                    for (int i = 0; i < phoneUtil.getPhone().size(); i++) {
////                        Gson gson=new Gson();
////                        String json = gson.toJson(phoneUtil.getPhone());
////                        LogUtils.d("数据是:"+json);
////                    }
//
//                    Gson gson = new Gson();
//                    String json = gson.toJson(phoneUtil.getPhone());
//                    LogUtils.d("数据是:" + json);
//                }


//                startLocation();

//                Intent intenta = new Intent(context, ImageGridActivity.class);
//                startActivityForResult(intenta, IMAGE_PICKER);
//                List<PhoneInfo> mobile = getPhoneNumberFromMobile(context);
//                LogUtils.d("数据是" + mobile.toString());
            }
        });
        dialog = new ProgressDialog(context);
        loadView = findViewById(R.id.loadView);
        smartRefreshLayout = findViewById(R.id.refreshlayout);
        smartRefreshLayout.setRefreshHeader(new ClassicsHeader(this));
        smartRefreshLayout.setEnableOverScrollDrag(false);
        dWebView = new DWebView(this);
        dWebView.addJavascriptObject(new JsApi(this), null);
        //开启浏览器调试
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
            dWebView.setWebContentsDebuggingEnabled(true);
        }

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mAgentWeb.getUrlLoader().reload();
            }
        });
        //下拉内容不偏移
        smartRefreshLayout.setEnableHeaderTranslationContent(false);

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(smartRefreshLayout, new FrameLayout.LayoutParams(-1, -1))
                .closeIndicator()
                .setWebViewClient(webViewClient)
                .setWebView(dWebView)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)//不跳转其他应用
                .createAgentWeb()
                .ready()
                .go(url);

    }


    private void initLocation() {


//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            //申请WRITE_EXTERNAL_STORAGE权限
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                    301);//自定义的code
//        }
//        locationClient = new AMapLocationClient(this.getApplicationContext());
//        locationOption = getDefaultOption();
//        //设置定位参数
//        locationClient.setLocationOption(locationOption);
//        // 设置定位监听
//        locationClient.setLocationListener(locationListener);
        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION).build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        locationClient = new AMapLocationClient(context);
                        locationOption = getDefaultOption();
                        //设置定位参数
                        locationClient.setLocationOption(locationOption);
                        // 设置定位监听
                        locationClient.setLocationListener(locationListener);
                    }

                    @Override
                    public void onDenied(List<String> permissions) {

                    }
                });

    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        LogUtils.d("支付成功");
//                        finish();
//                        Intent jump_gouwuche = new Intent(GoToBuyActivity.this, MainActivity.class);
////                finish();
//                        jump_gouwuche.putExtra("jump_gouwuche", 0);
//                        startActivity(jump_gouwuche);
//                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        Toast.makeText(GoToBuyActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        Toast.makeText(GoToBuyActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        LogUtils.d("支付失败");
                    }
                    break;
                }
//                case SDK_AUTH_FLAG: {
//                    @SuppressWarnings("unchecked")
//                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
//                    String resultStatus = authResult.getResultStatus();
//
//                    // 判断resultStatus 为“9000”且result_code
//                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
//                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
//                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
//                        // 传入，则支付账户为该授权账户
//                        Toast.makeText(PayDemoActivity.this,
//                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
//                                .show();
//                    } else {
//                        // 其他状态值则为授权失败
//                        Toast.makeText(PayDemoActivity.this,
//                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
//
//                    }
//                    break;
//                }
                default:
                    break;
            }
        }

        ;
    };

    private void start() {

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                Log.d("AAAAAAAAAAAA", goodinfo);
                PayTask alipay = new PayTask(MainActivity.this);
                Map<String, String> result = alipay.payV2(goodinfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
    private void aipay() {
 OkGo.<String>post("http://mogu.vipgz1.idcfengye.com/ECanReach/payAlis.do")
         .tag(this)
         .params("money","0.01")
         .execute(new StringCallback() {
             @Override
             public void onSuccess(Response<String> response) {
                 goodinfo=response.body();
                 start();


//                 WeiXinBean s=GsonUtil.parseJsonWithGson(response.body(),WeiXinBean.class);
//
//                 WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
//                 builder.setAppId(s.getAppid())
//                         .setPrepayId(s.getPrepayid())
//                         .setPartnerId(s.getPartnerid())
//                         .setTimeStamp(s.getTimestamp())
//                         .setNonceStr(s.getNoncestr())
//                         .setPackageValue(s.getPackageX())
//                         .setSign(s.getSign())
//                         .build()
//                         .toWXPayNotSign(context,s.getAppid());
             }
         });
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + location.getProvider() + "\n");

                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
                    //定位完成的时间
                    sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
                }
                sb.append("***定位质量报告***").append("\n");
                sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启" : "关闭").append("\n");
                sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
                sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
                sb.append("* 网络类型：" + location.getLocationQualityReport().getNetworkType()).append("\n");
                sb.append("* 网络耗时：" + location.getLocationQualityReport().getNetUseTime()).append("\n");
                sb.append("****************").append("\n");
                //定位之后的回调时间
                sb.append("回调时间: " + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");

                //解析定位结果，
                String result = sb.toString();
//                tvResult.setText(result);
                LogUtils.d("定位数据是" + result);
            } else {
                LogUtils.d("定位失败，loc is null");
            }
        }
    };

    private void initWight() {
        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(false); //是否按矩形区域保存
        imagePicker.setSelectLimit(3);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
        imagePicker.setMultiMode(true);
    }

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (firstLoad) {
                loadView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            smartRefreshLayout.finishRefresh();
            if (firstLoad) {
                firstLoad = false;
                goneAnim(loadView);
            }
        }
    };

    private void goneAnim(final View view) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(700);

        view.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private long exitTime = 0;

    public void doubleExit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            toast("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    protected void showDig(String msg, boolean canCancel) {
        if (loadDialog == null) {
            loadDialog = new LoadDialog.Builder(this).loadText(msg).canCancel(canCancel).build();
        } else {
            loadDialog.setText(msg);
        }
        if (!loadDialog.isShowing())
            loadDialog.show();
    }


    protected void dismissDig() {
        if (loadDialog != null && loadDialog.isShowing()) {
            loadDialog.dismiss();
        }
    }

    private void setStatusBg(int resId) {
        ViewGroup contentView = findViewById(android.R.id.content);
        View statusBarView = new View(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.getStatusBarHeight(this));
        statusBarView.setBackgroundResource(resId);
        contentView.addView(statusBarView, lp);
    }

    @Override
    public void jsGetAlipayParams(Object params) {

    }

    @Override
    public void addHeadImg(Object params) {
        LogUtils.d("图片数据是" + params.toString());
        PhotoOneGet photoOneGet = GsonUtil.parseJsonWithGson(params.toString(), PhotoOneGet.class);
        userid = photoOneGet.getUserId();
        token = photoOneGet.getToken();
        type_photo = 1;
        imagePicker.setSelectLimit(1);
        Intent intenta = new Intent(context, ImageGridActivity.class);
        startActivityForResult(intenta, IMAGE_PICKER);

    }

    @Override
    public void jsTakeUploads(Object params) {

    }


    private String getGPSStatusString(int statusCode) {
        String str = "";
        switch (statusCode) {
            case AMapLocationQualityReport.GPS_STATUS_OK:
                str = "GPS状态正常";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                str = "手机中没有GPS Provider，无法进行GPS定位";
                break;
            case AMapLocationQualityReport.GPS_STATUS_OFF:
                str = "GPS关闭，建议开启GPS，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
                str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                str = "没有GPS定位权限，建议开启gps定位权限";
                break;
        }
        return str;
    }

    @Override
    public void third_party_login(Object params) {
        UMShareAPI.get(context).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                SocializeUtils.safeShowDialog(dialog);
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                SocializeUtils.safeCloseDialog(dialog);
                Toast.makeText(context, "成功了", Toast.LENGTH_LONG).show();

                StringBuilder sb = new StringBuilder();
                for (String key : map.keySet()) {
                    sb.append(key).append(" : ").append(map.get(key)).append("\n");
                }
                LogUtils.d("信息是:" + sb.toString());

            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                SocializeUtils.safeCloseDialog(dialog);
                Toast.makeText(context, "失败：" + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                SocializeUtils.safeCloseDialog(dialog);
                Toast.makeText(context, "取消了", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void compress(List<String> paths) {
        showDig("压缩中...", false);
        final int size = paths.size();
//        final Map<String, File> map = new HashMap<>();
        final List<File> map = new ArrayList<>();

        Luban.with(context).load(paths).ignoreBy(100)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        LogUtils.d("数据是:" + file.getPath() + "---------------->>>>>>");
//                        map.put(file.getName(), file);
                        map.add(file);
                        if (map.size() == size) {
                            //压缩完毕,上传图片
                            showDig("上传中...", false);
                            uploadImages(map);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissDig();
                        toast("图片压缩失败");

                    }
                }).launch();
    }

    private void uploadImages(
//            Map<String, File> map)
            List<File> map) {
        OkGo.<String>post("http://5ziwcz.natappfree.cc/mall/addPhotos")
                .tag(this)
                .params("user_id", "1000000000")
                .params("token", "VVON8nUAEzg7fSsh4p194TBUQtgW-rVfU7xKzpyTWe8=")
                .addFileParams("file", map)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtils.d("数据是" + response.body());
                        PhotoOne photoOne = GsonUtil.parseJsonWithGson(response.body(), PhotoOne.class);
                        if (photoOne.getMeta().getCode() == 200) {
                            dismissDig();
                            LogUtils.d("数据是图片" + photoOne.getData());
                        }

                    }
                });
    }

    @Override
    public void takeShare(Object params) {

        new ShareAction(MainActivity.this).withText("你好吗")
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        LogUtils.d("分享的是:" + share_media.toString());
                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        LogUtils.d("分享的是:" + share_media.toString());
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        LogUtils.d("分享的是:" + share_media.toString());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        LogUtils.d("分享的是:" + share_media.toString());
                    }
                }).share();

    }

    @Override
    public void takeCopy(Object params) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        //创建ClipData对象
        ClipData clipData = ClipData.newPlainText("tahome text copy", params.toString());
        //添加ClipData对象到剪切板中
        clipboardManager.setPrimaryClip(clipData);

        toast("已复制内容到剪切板");

    }

    @Override
    public void takeExit(Object params) {
        int flag = (int) params;
        if (flag == 0) {
            //不能退出,返回上一页
            if (dWebView != null && dWebView.canGoBack()) {
                dWebView.goBack();
            }
        } else {
            doubleExit();
        }
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {
    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {
        toast("分享成功");
    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        toast("分享失败");
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
        destroyLocation();
    }


    private void resetOption() {
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        locationOption.setGpsFirst(false);
        // 设置是否开启缓存
        locationOption.setLocationCacheEnable(false);
        // 设置是否单次定位
        locationOption.setOnceLocation(false);
        //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
        locationOption.setOnceLocationLatest(false);
        //设置是否使用传感器
        locationOption.setSensorEnable(false);
        //设置是否开启wifi扫描，如果设置为false时同时会停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        String strInterval = "1000";
        if (!TextUtils.isEmpty(strInterval)) {
            try {
                // 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
                locationOption.setInterval(Long.valueOf(strInterval));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        String strTimeout = "2000";
        if (!TextUtils.isEmpty(strTimeout)) {
            try {
                // 设置网络请求超时时间
                locationOption.setHttpTimeOut(Long.valueOf(strTimeout));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void startLocation() {
        //根据控件的选择，重新设置定位参数
//        resetOption();
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                List<String> paths = new ArrayList<>();
                LogUtils.d("数据是" + images.size());
                for (ImageItem datae : images) {
                    String headImage = datae.path;
                    paths.add(headImage);
//                    String imagename = headImage.substring(headImage.lastIndexOf("/") + 1);
                    LogUtils.d("数据是:" + headImage);
//                    testokGO();
                }
                if (type_photo == 1) {
                    uploadAvatar(paths.get(0));
                } else if (type_photo == 2) {
                    compress(paths);
                }

//                if (paths.size() == 1) {
//                    LogUtils.d("数据是" + paths);
//                    uploadAvatar(paths.get(0));
//                } else if (paths.size() > 1) {
//                    LogUtils.d("数据是多选" + paths);
//                    compress(paths);
//                }


            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void testokGO() {
        OkGo.<String>post("http://39.105.148.182/qingniaozhongchou/wdt_showgoodsdetail.do")
                .params("goodsid", 13)
                .params("userid", 0)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
//                        TestGoodsBean s = GsonUtil.parseJsonWithGson(response.body(), GoodsDetails.class);
//                        LogUtils.d("数据是:" + s.getMsg() + s.getGoods());
                    }
                });
    }

    private void uploadAvatar(final String path) {
        LogUtils.d("数据是" + path);
//        File file = new File(path);
        File file = new File(path);
        File newfile = CompressHelper.getDefault(context).compressToFile(file);
        OkGo.<String>post("http://5fv9zd.natappfree.cc/mall/addPhotos")
                .tag(this)
                .isMultipart(true)
                .params("user_id", userid)
                .params("token", token)
                .params("file", newfile)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtils.d("成功是" + response.message());
//                        TestGoodsBean goodsBean = GsonUtil.parseJsonWithGson(response.body(), TestGoodsBean.class);
//                        LogUtils.d("数据是:" + goodsBean.getMsg());


                        PhotoOne photoOne = GsonUtil.parseJsonWithGson(response.body(), PhotoOne.class);
                        LogUtils.d("返回的数据是" + photoOne.getData());
                        dWebView.callHandler("addHeadImgCallback", new Object[]{photoOne.getData()});

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        LogUtils.d("失败是是" + response.message());
                    }
                });
    }

    public List<PhoneInfo> getPhoneNumberFromMobile(Context context) {

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    1);
        }
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            list = new ArrayList<PhoneInfo>();
            Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{"display_name", "sort_key", "contact_id",
                            "data1"}, null, null, null);
//        moveToNext方法返回的是一个boolean类型的数据
            while (cursor.moveToNext()) {
                //读取通讯录的姓名
                String name = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                //读取通讯录的号码
                String number = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int Id = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String Sortkey = getSortkey(cursor.getString(1));
                PhoneInfo phoneInfo = new PhoneInfo(name, number, Sortkey, Id);
                list.add(phoneInfo);
            }
            cursor.close();
        }
        // TODO Auto-generated constructor stub

        return list;
    }

    private static String getSortkey(String sortKeyString) {
        String key = sortKeyString.substring(0, 1).toUpperCase();
        if (key.matches("[A-Z]")) {
            return key;
        } else
            return "#";
    }

}
