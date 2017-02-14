package cn.com.fooltech.smartparking.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bigkoo.pickerview.TimePickerView;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.UserInfo;
import cn.com.fooltech.smartparking.cache.BitmapCache;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetUserInfo;
import cn.com.fooltech.smartparking.utils.BaseCallback;
import cn.com.fooltech.smartparking.utils.BitmapUtil;
import cn.com.fooltech.smartparking.utils.DateUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.GuideUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.OkHttpUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.PopWindowPhoto;
import cn.com.fooltech.smartparking.view.PopWindowSex;
import cn.com.fooltech.smartparking.view.RoundImageView;
import okhttp3.Response;

public class UserCenterActivity extends BaseActivity {
    @Bind(R.id.complete)
    TextView tvComplete;
    @Bind(R.id.user_head_show2)
    RoundImageView ivUserImg;
    @Bind(R.id.point_show)
    TextView tvPoint;
    @Bind(R.id.park_voucher_show)
    TextView tvVoucher;
    @Bind(R.id.park_card_show)
    TextView tvCard;
    @Bind(R.id.nick_name_edit)
    EditText etNickName;
    @Bind(R.id.sex_show)
    TextView tvSex;
    @Bind(R.id.birthday_show)
    TextView tvBirthday;
    @Bind(R.id.mobile_show)
    TextView tvMobile;
    @Bind(R.id.lay_change_mobile)
    RelativeLayout layChangeMobile;
    @Bind(R.id.layout_user_center)
    RelativeLayout mLayout;
    @Bind(R.id.lay_integral)
    RelativeLayout mLayout2;
    private Context context = UserCenterActivity.this;
    private TimePickerView pvTime;
    private PopWindowSex popWindowSex;
    private PopWindowPhoto popWindowPhoto;
    private View sexView;
    private String imgName = "userHead.png";
    private int sexType = 0;
    private ImageView ivMan, ivWoman;
    private UserInfo userInfo = new UserInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        GuideUtils guideUtil = GuideUtils.getInstance();
        guideUtil.addGuideImage(this,mLayout,R.drawable.ic_guide_2,"guide_user");

        getUserInfo();
        initView();
    }

    private void initView() {
        sexView = LayoutInflater.from(this).inflate(R.layout.pop_sex_layout, null);
        ivMan = (ImageView) sexView.findViewById(R.id.image_man);
        ivWoman = (ImageView) sexView.findViewById(R.id.image_woman);

        etNickName.setCursorVisible(false);//隐藏光标

//        userInfo = (UserInfo) SPUtils.getObject(this,"userInfo");
//        sexType = tvSex.getText().toString().equals("男") ? 1 : tvSex.getText().toString().equals("女") ? 2 : 0;

    }

    public void onClick(View view) {
        switch (view.getId()) {
            //修改用户头像
            case R.id.modify_img:
                showPopWindowPhoto();
                break;
            //我的积分
            case R.id.lay_integral:
                String pointStr = tvPoint.getText().toString();
                int point = Utils.strToInt(pointStr);
                Intent intent = new Intent(this, PointDetailActivity.class);
                intent.putExtra("point", point);
                startActivity(intent);
                break;
            //停车券
            case R.id.lay_park_coupon:
                startActivity(new Intent(this, VoucherActivity.class));
                break;
            //月卡
            case R.id.lay_park_card:
                startActivity(new Intent(this, CardMyActivity.class));
                break;
            //昵称
            case R.id.nick_name_edit:
                etNickName.setCursorVisible(true);//显示光标
                etNickName.setSelection(etNickName.getText().toString().length());
                tvComplete.setVisibility(View.VISIBLE);
                break;
            //性别
            case R.id.lay_sex:
                showPopWindowSex();
                break;
            //出生日期
            case R.id.lay_birthday:
                initDatePicker();
                break;
            //更换手机号
            case R.id.lay_change_mobile:
                startActivity(new Intent(this, ChangeMobileActivity.class));
                break;
            //注销登录
            case R.id.btn_logout:
                showLogoutDialog();
                break;
            //返回
            case R.id.back_user:
                finish();
                break;
            //完成更新用户信息
            case R.id.complete:
                String nickName = etNickName.getText().toString();
                String sex = tvSex.getText().toString();
                String birthday = tvBirthday.getText().toString();
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("userId", userInfo.getUserId());
                paramMap.put("token", SPUtils.get(context, "token", ""));
                if (!("").equals(nickName)) {
                    paramMap.put("nickname", nickName);
                }
                if (!("").equals(sex)) {
                    paramMap.put("sex", sex.equals("男") ? 1 : 2);
                }
                if (!("").equals(birthday)) {
                    paramMap.put("birthday", birthday);
                }

                HttpUtils.sendHttpPostRequest(Urls.URL_UPDATE_USER_INFO, handlerUserInfo, paramMap, this);
                break;
        }
    }

    /**
     * 获取用户信息
     */
    public void getUserInfo() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(context, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(context, "token", ""));

        HttpUtils.sendHttpPostRequest(Urls.URL_GET_USER_INFO, handlerGetUserInfo, paramMap, this);
    }


    /**
     * 时间选择器
     */
    private void initDatePicker() {
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        //控制时间范围
        Date date = null;
        if (!tvBirthday.getText().toString().equals("")) {
            date = DateUtils.strToDate(tvBirthday.getText().toString());
        }
        Calendar calendar = Calendar.getInstance();
        pvTime.setRange(calendar.get(Calendar.YEAR) - 70, calendar.get(Calendar.YEAR));//要在setTime 之前才有效果哦
        pvTime.setTime(date);
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                tvBirthday.setText(DateUtils.getTime(date));
                tvComplete.setVisibility(View.VISIBLE);
            }
        });
        pvTime.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (pvTime != null && pvTime.isShowing()) {
                pvTime.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //注销
    Handler handlerLogout = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
//                if (code == 0) {
                ToastUtils.showShort(context, "注销成功");
                MobclickAgent.onProfileSignOff();//友盟统计
                MyApplication.isLogin = false;
                SPUtils.put(context, "isLogin", false);
                finish();
                startActivity(new Intent(context, LoginActivity.class));
//                } else {
//                ErrorUtils.errorCode(context, code);
//                }
            }
            super.handleMessage(msg);
        }
    };
    //更新用户信息
    Handler handlerUserInfo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    ToastUtils.showShort(context, "更新成功");
                    MyApplication.isUpdateUser = true;
                    etNickName.setCursorVisible(false);//隐藏光标
                    tvComplete.setVisibility(View.GONE);
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };
    //获取用户信息
    Handler handlerGetUserInfo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetUserInfo jsonBean = (GetUserInfo) JsonUtils.jsonToObject(msg.obj.toString(), GetUserInfo.class);
                if (jsonBean != null) {
                    int code = jsonBean.getCode();
                    if (code == 0) {
                        userInfo = jsonBean.getContent();
//                SPUtils.putObject(context,"userInfo",info);

                        etNickName.setText(userInfo.getNickName().equals(" ") ? "" : userInfo.getNickName());
                        tvSex.setText(userInfo.getSex() == 1 ? "男" : userInfo.getSex() == 2 ? "女" : "");
                        tvBirthday.setText(userInfo.getBirthday() == null ? "" : userInfo.getBirthday().toString());
                        tvMobile.setText(userInfo.getMobile());
                        tvPoint.setText(userInfo.getPoints() + "");
                        tvVoucher.setText(userInfo.getVoucherCount() + "张");
                        tvCard.setText(userInfo.getCardCount() + "张");

                        ImageLoader imageLoader = new ImageLoader(MyApplication.getHttpRequestQueue(), new BitmapCache());
                        ImageLoader.ImageListener listener = ImageLoader.getImageListener(ivUserImg, R.drawable.default_user, R.drawable.default_user);
                        if (userInfo.getAvatarUrl().contains("http")) {
                            imageLoader.get(userInfo.getAvatarUrl(), listener);
                        }
                    } else {
                        ErrorUtils.errorCode(context, code);
                    }
                } else {
                    ToastUtils.showShort(context, "服务器请求错误");
                }
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 弹出性别选择框
     */
    private void showPopWindowSex() {
        //实例化SelectPicPopupWindow
        String sex = tvSex.getText().toString();
        popWindowSex = new PopWindowSex(this, itemsOnClickSex, sex);
        popWindowSex.showAtLocation(this.findViewById(R.id.layout_user_center), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        popWindowSex.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Common.setShadow(UserCenterActivity.this, 1f);
            }
        });
    }

    /**
     * 弹出拍照选择框
     */
    private void showPopWindowPhoto() {
        //实例化SelectPicPopupWindow
        popWindowPhoto = new PopWindowPhoto(this, itemsOnClickPhoto);
        popWindowPhoto.showAtLocation(this.findViewById(R.id.layout_user_center), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        popWindowPhoto.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Common.setShadow(UserCenterActivity.this, 1f);
            }
        });
    }

    //为性别弹出窗口实现监听类 1:男  2:女
    private View.OnClickListener itemsOnClickSex = new View.OnClickListener() {

        public void onClick(View v) {
            popWindowSex.dismiss();
            switch (v.getId()) {
                case R.id.sex_man:
                    ivMan.setVisibility(View.VISIBLE);
                    ivWoman.setVisibility(View.GONE);
                    tvSex.setText("男");
                    sexType = 1;
                    tvComplete.setVisibility(View.VISIBLE);
                    break;
                case R.id.sex_woman:
                    ivWoman.setVisibility(View.VISIBLE);
                    ivMan.setVisibility(View.GONE);
                    tvSex.setText("女");
                    sexType = 2;
                    tvComplete.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }

    };

    //为选择照片弹出窗口实现监听类
    private View.OnClickListener itemsOnClickPhoto = new View.OnClickListener() {

        public void onClick(View v) {
            popWindowPhoto.dismiss();
            switch (v.getId()) {
                case R.id.take_photo:  //拍照
                    File file = new File(MyApplication.imagePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(MyApplication.imagePath, imgName)));
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
        if (resultCode != RESULT_OK) {
            Common.setShadow(UserCenterActivity.this, 1f);
            return;
        }
//        Uri uri = data.getData();
//        String path = uri.getPath();
        switch (requestCode) {
            case MyApplication.REQUE_CODE_CAMERA:  //拍照
                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                    return;
                }
                BitmapUtil.startPhotoZoom(Uri.fromFile(new File(MyApplication.imagePath, imgName)), this);
                break;
            case MyApplication.REQUE_CODE_PHOTO:  //相册选择
                BitmapUtil.startPhotoZoom(data.getData(), this);
//                Intent intent = new Intent(this, ClipImageActivity.class);
//                intent.putExtra("path", path);
//                this.startActivityForResult(intent, 3);
                break;
            case MyApplication.REQUE_CODE_CROP:  //裁剪
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
//        Bundle extras = picdata.getExtras();
//        if (extras != null) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(MyApplication.userHeadUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//            Bitmap bitmap = extras.getParcelable("data");
            ivUserImg.setImageBitmap(bitmap);
            Bitmap mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            String filePath = MyApplication.imagePath + imgName;
            BitmapUtil.saveImg(mBitmap, filePath);

//            File file = new File(filePath);
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("userId", String.valueOf(userInfo.getUserId()));
            paramMap.put("token", (String) SPUtils.get(context, "token", ""));
            Map<String, String> fileMap = new HashMap<String, String>();
            fileMap.put("headPic", filePath);
            OkHttpUtils.getmInstance().uploadFile(Urls.URL_CHANGE_USER_HEAD, paramMap, fileMap, new BaseCallback() {
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
                        Map<String,Object> resultMap = JsonUtils.jsonToMap(result);
                        if(resultMap != null && resultMap.size() > 0) {
                            int code = (int) resultMap.get("code");
                            if (code == 0) {
//                                MyApplication.imageUrl = (String) resultMap.get("imageUrl");
                                ToastUtils.showShort(context, "上传成功");
                                MyApplication.isUpdateUser = true;
                            } else {
                                ErrorUtils.errorCode(context, code);
                            }
                            Common.setShadow(UserCenterActivity.this, 1f);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            },this);
//        }
    }

    /**
     * 显示注销对话框
     */
    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setMessage(getResources().getString(R.string.dialog_logout))
                .setPositiveButton("注销", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Map<String, Object> paramMap = new HashMap<String, Object>();
                        paramMap.put("userId", SPUtils.get(context, "userId", new Long(0l)));
                        paramMap.put("token", SPUtils.get(context, "token", ""));
                        HttpUtils.sendHttpPostRequest(Urls.URL_LOGOUT, handlerLogout, paramMap, UserCenterActivity.this);
                    }
                })
                .setNegativeButton("点错了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (MyApplication.pointCount != -1) {
            tvPoint.setText(MyApplication.pointCount + "");
            MyApplication.pointCount = -1;
        }
    }
}
