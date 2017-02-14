package cn.com.fooltech.smartparking.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.AccountRelatingAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.BindCarInfo;
import cn.com.fooltech.smartparking.bean.BindUserInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetBindUsers;
import cn.com.fooltech.smartparking.slidelistview.SwipeMenu;
import cn.com.fooltech.smartparking.slidelistview.SwipeMenuCreator;
import cn.com.fooltech.smartparking.slidelistview.SwipeMenuItem;
import cn.com.fooltech.smartparking.slidelistview.SwipeMenuListView;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;

/**
 * 关联账号
 */
public class AccountRelatingActivity extends BaseActivity {
    @Bind(R.id.back_account)
    ImageView ivBack;
    @Bind(R.id.plate_num)
    TextView tvPlateNum;
    @Bind(R.id.lv_account)
    SwipeMenuListView mListView;
    @Bind(R.id.relieve_bind)
    Button btnRelieveBind;
    private Context context = AccountRelatingActivity.this;
    private List<BindUserInfo> resultList = new ArrayList<BindUserInfo>();
    private String plateNumber;
    private int count; //绑定的车辆数量
    private AccountRelatingAdapter mAdapter;
    private int posi;
    private int balance;
    private String balanceStr;
    private double balanceDou;
    private AlertDialog dialog;
    private int isOwer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_relating)  ;
        ButterKnife.bind(this);

        initView();
        getBindCarList();

    }

    private void initView() {
        initAdapter();
        mListView.setRefreshEnable(false);
        mListView.setLoadEnable(false);

        initDeleteBtn();
        plateNumber = this.getIntent().getStringExtra("plateNumber");
        balance = this.getIntent().getIntExtra("balance", 0) / 100;
        balanceStr = Utils.decimalFormat((double) balance);
        balanceDou = Utils.strToDouble(balanceStr);
        count = this.getIntent().getIntExtra("count", 0);
        tvPlateNum.setText(plateNumber);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //解除绑定
        btnRelieveBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOwer = 1;
                if (balance > 0) {
                    showForceUnBindDialog();
                } else {
                    showUnBindDialog((long) SPUtils.get(context, "userId", new Long(0l)));
                }
            }
        });
    }

    private void initAdapter() {
        mAdapter = new AccountRelatingAdapter(this, resultList);
        mListView.setAdapter(mAdapter);
    }

    /**
     * 设置删除按钮,事件
     */
    private void initDeleteBtn() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                createMenu2(menu);
            }

            private void createMenu2(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F,
                        0x25)));
                // set item width
                openItem.setWidth(Common.dp2px(70, context));
                // set item title
                openItem.setTitle("删除");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
            }
        };
        mListView.setMenuCreator(creator);
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                BindUserInfo info = mAdapter.list.get(position);
                isOwer = info.getIsOwner();
                if (isOwer == 1) { //是车主
                    if (balance > 0) {
                        showForceUnBindDialog();
                    } else {
                        showUnBindDialog(info.getUserId());
                    }
                } else if (isOwer == 0) { //不是车主
                    showUnBindDialog(info.getUserId());
                }
                posi = position;
                return false;
            }
        });
    }

    /**
     * 获取当前所有关联用户
     */
    private void getBindCarList() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("plateNumber", plateNumber);
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_BIND_USERS, handlerBindCar, paramMap, this);
    }

    Handler handlerBindCar = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetBindUsers bindUsers = (GetBindUsers) JsonUtils.jsonToObject(msg.obj.toString(), GetBindUsers.class);
                if (bindUsers != null) {
                    int code = bindUsers.getCode();
                    if (code == 0) {
                        List<BindUserInfo> result = bindUsers.getContent();
                        if(result != null) {
                            resultList.addAll(result);
                        }
                        mAdapter.notifyDataSetChanged();
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
     * 解除绑定
     */
    private void unBindCar(long bindUser) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("plateNumber", plateNumber);
        paramMap.put("bindUser", bindUser);
        HttpUtils.sendHttpPostRequest(Urls.URL_UNBIND, handlerUnBindCar, paramMap, this);
    }

    /**
     * 强行解除绑定
     */
    private void forceUunBindCar() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("plateNumber", plateNumber);
        HttpUtils.sendHttpPostRequest(Urls.URL_FORCE_UNBIND, handlerForceUnBindCar, paramMap, this);
    }

    Handler handlerUnBindCar = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    ToastUtils.showShort(context, "解绑成功");
                    mAdapter.removeData(posi);
                    MyApplication.isUpdateCar = true;
//                    remove();
                    if (isOwer == 1) {
                        finish();
                    }
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };
    Handler handlerForceUnBindCar = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    ToastUtils.showShort(context, "解绑成功");
                    mAdapter.removeData(posi);
                    MyApplication.isUpdateCar = true;
//                    remove();
                    if (isOwer == 1) {
                        finish();
                    }
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 显示提示解绑对话框
     */
    private void showUnBindDialog(final long userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setMessage("您确定要解绑吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        unBindCar(userId);
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
    }
    /**
     * 显示提示强制解绑对话框
     */
    private void showForceUnBindDialog() {
        String content = "您的账户还有余额" + balanceStr + "元,您确定要解绑吗?";
        int index = content.indexOf("元");
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(content);
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(getResources().getColor(R.color.green));
        stringBuilder.setSpan(greenSpan, 8, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setMessage(stringBuilder)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        forceUunBindCar();
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
    }
}
