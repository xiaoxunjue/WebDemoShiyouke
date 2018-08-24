package com.personal.revenant.shiyouke;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
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

import com.apkfuns.logutils.LogUtils;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.personal.revenant.shiyouke.bean.TestGoodsBean;
import com.personal.revenant.shiyouke.event.JsApi;
import com.personal.revenant.shiyouke.utils.GlideImageLoader;
import com.personal.revenant.shiyouke.utils.GsonUtil;
import com.personal.revenant.shiyouke.utils.LoadDialog;
import com.personal.revenant.shiyouke.utils.Utils;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWight();
        context = this;
        Utils.setImageStatus(this);
        setContentView(R.layout.activity_main);
        setStatusBg(R.color.colorPrimary);
        url = BASE;
        initView();
    }

    private void initView() {
        button = findViewById(R.id.test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenta = new Intent(context, ImageGridActivity.class);
                startActivityForResult(intenta, IMAGE_PICKER);
            }
        });
        dialog = new ProgressDialog(context);
        loadView = findViewById(R.id.loadView);
        smartRefreshLayout = findViewById(R.id.refreshlayout);
        smartRefreshLayout.setRefreshHeader(new ClassicsHeader(this));
        smartRefreshLayout.setEnableOverScrollDrag(false);;
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

    private void initWight() {
        ImagePicker imagePicker = ImagePicker.getInstance();
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
        imagePicker.setMultiMode(true);   //允许剪切
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
    public void jsTakeUpload(Object params) {

    }

    @Override
    public void jsTakeUploads(Object params) {

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
        OkGo.<String>post("")
                .tag(this)
                .params("", "")
                .addFileParams("", map)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

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
                if (paths.size() == 1) {
                    LogUtils.d("数据是" + paths);
                    uploadAvatar(paths.get(0));
                } else if (paths.size() > 1) {
                    LogUtils.d("数据是多选" + paths);
                    compress(paths);
                }


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

    private void uploadAvatar(String path) {
        LogUtils.d("数据是" + path);
//        File file = new File(path);
        File file = new File(path);
        OkGo.<String>post("http://39.105.148.182/qingniaozhongchou/wdt_imageUpload.do")
                .tag(this)
                .isMultipart(true)
                .params("userid", "8")
                .params("headimg", file)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtils.d("成功是" + response.message());
                        TestGoodsBean goodsBean = GsonUtil.parseJsonWithGson(response.body(), TestGoodsBean.class);
                        LogUtils.d("数据是:" + goodsBean.getMsg());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        LogUtils.d("失败是是" + response.message());
                    }
                });
    }
}
