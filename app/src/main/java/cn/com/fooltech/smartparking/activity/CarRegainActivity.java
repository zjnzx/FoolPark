package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.utils.BaseCallback;
import cn.com.fooltech.smartparking.utils.BitmapUtil;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.OkHttpUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.view.PopWindowPhoto;
import okhttp3.Response;

/**
 * 找回车辆
 */
public class CarRegainActivity extends BaseActivity {
    @Bind(R.id.plate_num_show3)
    TextView tvPlateNum;
    @Bind(R.id.iv_lic)
    ImageView ivLic;
    @Bind(R.id.iv_car)
    ImageView ivCar;
    private Context context = CarRegainActivity.this;
    private Button btnSubmit;
    private String plateNumber;
    private WindowManager.LayoutParams lp;
    private PopWindowPhoto popWindowLic;
    private String licImgName = "drivingLicense";
    private String carImgName = "myCar";
    private int IMG_TYPE = 1;//1:行驶证  2:车辆照片
    private String licImgPath = "";
    private String carImgPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_regain);
        ButterKnife.bind(this);

        plateNumber = getIntent().getStringExtra("plateNumber");
        initView();
    }

    private void initView() {
        tvPlateNum.setText("车牌号:  " + plateNumber);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_car_regain:
                finish();
                break;
            //添加行驶证
            case R.id.iv_lic:
                showPopWindow();
                IMG_TYPE = 1;
                break;
            //添加车辆照片
            case R.id.iv_car:
                showPopWindow();
                IMG_TYPE = 2;
                break;
            //提交认证
            case R.id.submit_approve:
                if (licImgPath.equals("") || carImgPath.equals("")) {
                    ToastUtils.showShort(this, "图片信息不完整");
                    return;
                }
                submitApprove();
                break;
        }
    }

    /**
     * 提交认证
     */
    private void submitApprove() {
        Map<String, String> paramMap = new HashMap<String, String>();
        long userId = (long) SPUtils.get(context, "userId", new Long(0l));
        paramMap.put("userId", String.valueOf(userId));
        paramMap.put("token", (String) SPUtils.get(context, "token", ""));
        paramMap.put("plateNumber", plateNumber);
        Map<String, String> fileMap = new HashMap<String, String>();
        fileMap.put("licPic", licImgPath);
        fileMap.put("carPic", carImgPath);
        OkHttpUtils.getmInstance().uploadFile(Urls.URL_REGAIN_CAR, paramMap, fileMap, new BaseCallback() {
            @Override
            public void onRequestBefore() {
            }

            @Override
            public void onFailure(Exception e) {
            }

            @Override
            public void onSuccess(Response response) {
                try {
                    String result = response.body().string();
                    int code = JsonUtils.getCodeResult(result);
                    if (code == 0) {
                        ToastUtils.showShort(context, "提交成功");
                        finish();
                    } else {
                        ErrorUtils.errorCode(context, code);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },this);
    }

    private void showPopWindow() {
        //设置背景透明
        lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        //实例化SelectPicPopupWindow
        popWindowLic = new PopWindowPhoto(this, itemsOnClickLic);
        popWindowLic.showAtLocation(this.findViewById(R.id.lay_car_regain), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        popWindowLic.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Common.setShadow(CarRegainActivity.this, 1f);
            }
        });
    }

    //为证件照片弹出窗口实现监听类
    private View.OnClickListener itemsOnClickLic = new View.OnClickListener() {

        public void onClick(View v) {
            popWindowLic.dismiss();
            lp.alpha = 1f;
            getWindow().setAttributes(lp);
            switch (v.getId()) {
                case R.id.take_photo:  //拍照
                    File file = new File(MyApplication.imagePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    if (IMG_TYPE == 1) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(MyApplication.imagePath, licImgName)));
                    } else if (IMG_TYPE == 2) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(MyApplication.imagePath, carImgName)));
                    }
                    startActivityForResult(intent, MyApplication.REQUE_CODE_CAMERA);
                    break;
                case R.id.photo_choose:  //图库
                    Intent i = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//调用android的图库
                    startActivityForResult(i, MyApplication.REQUE_CODE_PHOTO);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case MyApplication.REQUE_CODE_CAMERA:  //拍照
                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                    return;
                }
                String filePath = "";
                if (IMG_TYPE == 1) {
                    filePath = MyApplication.imagePath + licImgName;
                } else if (IMG_TYPE == 2) {
                    filePath = MyApplication.imagePath + carImgName;
                }
                Bitmap bitmap = BitmapUtil.getBitmapFromFile(filePath, 240, 240);
                BitmapUtil.saveImg(bitmap, filePath);
                if (IMG_TYPE == 1) {
                    ivLic.setImageBitmap(bitmap);
                    licImgPath = filePath;
                } else if (IMG_TYPE == 2) {
                    ivCar.setImageBitmap(bitmap);
                    carImgPath = filePath;
                }
                break;
            case MyApplication.REQUE_CODE_PHOTO:  //相册选择
                Uri uri = data.getData();
                String path = BitmapUtil.getPhotoPath(uri, context);
                Bitmap bitmap2 = BitmapUtil.getBitmapFromFile(path, 240, 240);
                String filePath2 = "";
                if (IMG_TYPE == 1) {
                    filePath2 = MyApplication.imagePath + licImgName;
                } else if (IMG_TYPE == 2) {
                    filePath2 = MyApplication.imagePath + carImgName;
                }
                BitmapUtil.saveImg(bitmap2, filePath2);
                if (IMG_TYPE == 1) {
                    ivLic.setImageBitmap(bitmap2);
                    licImgPath = filePath2;
                } else if (IMG_TYPE == 2) {
                    ivCar.setImageBitmap(bitmap2);
                    carImgPath = filePath2;
                }
                break;
        }
    }
}
