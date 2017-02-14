package cn.com.fooltech.smartparking.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.fragment.MessageFragment;
import cn.com.fooltech.smartparking.view.ListViewPlus;


public class MessageActivity extends BaseActivity {
    @Bind(R.id.back_message)
    ImageView ivBack;
    @Bind(R.id.user_message)
    RadioButton rbUserMessage;
    @Bind(R.id.sys_message)
    RadioButton rbSysMessage;
    @Bind(R.id.rg_message)
    RadioGroup radioGroup;
    @Bind(R.id.lay_message)
    RelativeLayout layMessage;
    @Bind(R.id.lay_content_message)
    FrameLayout layContentMessage;
    @BindColor( R.color.green )
    int green;
    @BindColor( R.color.white )
    int white;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private MessageFragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        fragmentManager = getFragmentManager();
        fragments = new MessageFragment[2];

        setSelection(rbUserMessage, 0, 1);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if (id == R.id.user_message) { //用户消息
                    setSelection(rbUserMessage, 0, 1);
                } else if (id == R.id.sys_message) { //系统通知
                    setSelection(rbSysMessage, 1, 2);
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setSelection(RadioButton rb, int index, int param) {
        transaction = fragmentManager.beginTransaction();
        hideFragment();
        clearState();
        rb.setTextColor(white);
        if (fragments[index] == null) {
            fragments[index] = MessageFragment.newInstance(param);
            transaction.add(R.id.lay_content_message, fragments[index]);
        } else {
            transaction.show(fragments[index]);
            fragments[index].setIndex();
            fragments[index].getMessageList(ListViewPlus.REFRESH);
        }
        transaction.commit();
    }

    private void hideFragment() {
        if (fragments[0] != null) {
            transaction.hide(fragments[0]);
        }
        if (fragments[1] != null) {
            transaction.hide(fragments[1]);
        }
    }

    private void clearState() {
        rbUserMessage.setTextColor(green);
        rbSysMessage.setTextColor(green);
    }

}
