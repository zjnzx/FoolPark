package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.CommentWholeAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.CommentInfo;
import cn.com.fooltech.smartparking.bean.jsonbean.GetComment;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.utils.DateUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.ListViewPlus;

public class CommentWholeActivity extends BaseActivity implements ListViewPlus.ListViewPlusListener {
    @Bind(R.id.empty_comment)
    TextView tvEmpty;
    private Context context = CommentWholeActivity.this;
    @Bind(R.id.park_name_show6)
    TextView tvParkName;
    @Bind(R.id.lv_comment_whole)
    ListViewPlus mListView;
    private EditText etContent;
    private String commentContent;
    private long parkId;
    private PopupWindow mPopupWindow;
    private EditText etCommentContent;
    private View mContentView;
    private TextView tvCount;
    private RatingBar mRatingBar;
    private int gradeLevel;
    private List<CommentInfo> resultList = new ArrayList<CommentInfo>();
    private List<CommentInfo> result;
    private CommentWholeAdapter mAdapter;
    private boolean isFirstLoad;
    private int index = 0, count = 20;
    private int flag = ListViewPlus.REFRESH;//0:刷新  1:加载

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_whole);
        ButterKnife.bind(this);

        String parkName = getIntent().getStringExtra("parkName");
        parkId = getIntent().getLongExtra("parkId", 0l);

        tvParkName.setText(parkName);

        initView();
    }

    private void initView() {
        isFirstLoad = true;
        mListView.setRefreshEnable(true);
        mListView.setLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setListViewPlusListener(this);

        initAdapter();
    }

    @OnClick({R.id.back_comment_whole, R.id.et_input})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_comment_whole:
                finish();
                break;
            case R.id.et_input:
                gradeLevel = 0;
                showPopWindow();
                //弹出键盘
                openKeyboard(new Handler(), 350);
                break;
        }
    }

    private void showPopWindow() {
        //设置背景透明
        Common.setShadow(this, 0.6f);
        mContentView = LayoutInflater.from(this).inflate(R.layout.pop_comment_layout, null);
        Button btnSubmit = (Button) mContentView.findViewById(R.id.btn_comment_submit);
        etCommentContent = (EditText) mContentView.findViewById(R.id.et_comment_input);
        tvCount = (TextView) mContentView.findViewById(R.id.tv_count);
        mRatingBar = (RatingBar) mContentView.findViewById(R.id.grade_comment2);
        mPopupWindow = new PopupWindow(mContentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //键盘不遮挡popwindow
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //设置SelectPicPopupWindow弹出窗体可点击
        mPopupWindow.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        mPopupWindow.setAnimationStyle(R.style.pop_sex_anim);
        //实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(Color.parseColor("#00000000"));
        //设置SelectPicPopupWindow弹出窗体的背景
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.showAtLocation(this.findViewById(R.id.lay_comment), Gravity.BOTTOM, 0, 0); //设置layout在PopupWindow中显示的位置
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Common.setShadow(CommentWholeActivity.this, 1f);
            }
        });
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mContentView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                int height = mContentView.findViewById(R.id.lay_pop_comment).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        mPopupWindow.dismiss();
                    }
                }
                return true;
            }
        });

        //提交
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Common.isLogin(context)) return;
                commentContent = etCommentContent.getText().toString();
                if (commentContent.isEmpty()) {
                    ToastUtils.showShort(context, "内容不能为空");
                    return;
                }
                sbumitComment();
            }
        });

        etCommentContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String content = editable.toString();
                tvCount.setText(content.length() + "/" + 200);
            }
        });

        //评分等级
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                gradeLevel = (int) ratingBar.getRating();
            }
        });

    }

    /**
     * 打开软键盘
     */
    private void openKeyboard(Handler mHandler, int s) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, s);
    }

    /**
     * 提交评论
     */
    private void sbumitComment() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("parkId", parkId);
        paramMap.put("parkLevel", gradeLevel);
        paramMap.put("parkComment", commentContent);
        HttpUtils.sendHttpPostRequest(Urls.URL_SUBMIT_COMMENT, handlerSubmit, paramMap, this);
    }

    Handler handlerSubmit = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    ToastUtils.showShort(context, "提交成功");
                    mPopupWindow.dismiss();
                    index = 0;
                    getCommentList(ListViewPlus.REFRESH);
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 获取评论列表
     */
    private void getCommentList(int flag) {
        this.flag = flag;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("parkId", parkId);
        paramMap.put("from", index);
        paramMap.put("recnum", count);
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_COMMENT_LIST, handlerComment, paramMap, this);
    }

    Handler handlerComment = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetComment comment = (GetComment) JsonUtils.jsonToObject(msg.obj.toString(), GetComment.class);
                if (comment != null) {
                    int code = comment.getCode();
                    if (code == 0) {
                        result = comment.getContent().getCommentList();
                        notifyData();
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

    private void initAdapter() {
        mAdapter = new CommentWholeAdapter(this, resultList);
        mListView.setAdapter(mAdapter);
    }

    private void notifyData() {
        if (flag == ListViewPlus.REFRESH) {
            resultList.clear();
            if (result != null) {
                resultList.addAll(result);
            }
        } else if (flag == ListViewPlus.LOAD) {
            if (result != null) {
                resultList.addAll(result);
            }
        }
        onLoadComplete();
        mAdapter.notifyDataSetChanged();
        if (resultList.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isFirstLoad) {
            mListView.autoRefresh();
            isFirstLoad = false;
        }
    }

    @Override
    public void onRefresh() {
        index = 0;
        getCommentList(ListViewPlus.REFRESH);
    }

    @Override
    public void onLoadMore() {
        index += count;
        getCommentList(ListViewPlus.LOAD);
    }

    private void onLoadComplete() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(DateUtils.getDate());
    }
}
