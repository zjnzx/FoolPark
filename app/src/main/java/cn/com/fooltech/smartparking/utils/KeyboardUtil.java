package cn.com.fooltech.smartparking.utils;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import cn.com.fooltech.smartparking.R;


/**
 * Created by xxx on 2015/9/22.
 */
public class KeyboardUtil {
    private Context ctx;
    private KeyboardView keyboardView;
    private Keyboard k1;// 省份简称键盘
    private Keyboard k2;// 字母键盘
    private Keyboard k3;// 数字字母键盘
    private Keyboard k4;// 数字字母键盘,港澳

    private String provinceShort[];
    private String numberAndLetter[];
    private String numberAndLetter2[];
    private String letters[];
    private RelativeLayout keyboardLay;

    private EditText edits[];
    private Button btn;
    private int currentEditText = 0;//默认当前光标在第一个EditText

    public KeyboardUtil(Context ctx, EditText edits[], Button btn) {
        this.ctx = ctx;
        this.edits = edits;
        this.btn = btn;
        k1 = new Keyboard(ctx, R.xml.province_abbreviation);
        k2 = new Keyboard(ctx, R.xml.letters);
        k3 = new Keyboard(ctx, R.xml.number_letters);
        k4 = new Keyboard(ctx, R.xml.number_letters2);
        keyboardView = (KeyboardView) ((Activity)ctx).findViewById(R.id.keyboard_view);
        keyboardLay = (RelativeLayout) ((Activity)ctx).findViewById(R.id.keyboard_lay);
        keyboardView.setKeyboard(k1);
        keyboardView.setEnabled(true);
        //设置为true时,当按下一个按键时会有一个popup来显示<key>元素设置的android:popupCharacters=""
        keyboardView.setPreviewEnabled(true);
        //设置键盘按键监听器
        keyboardView.setOnKeyboardActionListener(listener);
        provinceShort = new String[]{"京", "津", "冀", "鲁", "晋", "蒙", "辽", "吉", "黑"
                , "沪", "苏", "浙", "皖", "闽", "赣", "豫", "鄂", "湘"
                , "粤", "桂", "渝", "川", "贵", "云", "藏", "陕", "甘"
                , "青", "琼", "新", "宁","港", "澳", "台"};

        letters = new String[]{ "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"
                , "A", "S", "D", "F", "G", "H", "J", "K", "L"
                , "Z", "X", "C", "V", "B", "N", "M"};
        numberAndLetter = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9","0"
                , "Q", "W", "E", "R", "T", "Y", "U", "", "", "P"
                , "A", "S", "D", "F", "G", "H", "J", "K", "L"
                , "Z", "X", "C", "V", "B", "N", "M"};
        numberAndLetter2 = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9","0"
                , "Q", "W", "E", "R", "T", "Y", "U", "", "", "P"
                , "A", "S", "D", "F", "G", "H", "J", "K", "L"
                , "Z", "X", "C", "V", "B", "N", "M","港","澳"};
    }

    private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            if(primaryCode == 112){ //xml中定义的删除键值为112
                edits[currentEditText].setText("");//将当前EditText置为""并currentEditText-1
                currentEditText--;
                if(currentEditText < 1){
                    //切换为省份简称键盘
                    showKeyboard(1);
                }
                if(currentEditText < 0){
                    currentEditText = 0;
                }
                if(currentEditText == 1){
                    showKeyboard(2);//切换字母键盘
                }
                if(currentEditText == 5){
                    showKeyboard(3);//切换数字字母键盘,港澳
                }
                edits[currentEditText + 1].setBackgroundResource(R.drawable.bg_gray);
                edits[currentEditText].setBackgroundResource(R.drawable.bg_green);
                btn.setEnabled(false);//设置确定按钮不可用
                btn.setBackgroundResource(R.drawable.btn_add_car_bg);
            }else { //其它字符按键
                if (currentEditText == 0) { //如果currentEditText==0代表当前为省份键盘,
                    edits[0].setText(provinceShort[primaryCode]);
                    currentEditText = 1;
                    edits[0].setBackgroundResource(R.drawable.bg_gray);
                    edits[currentEditText].setBackgroundResource(R.drawable.bg_green);
                    showKeyboard(2);//切换字母键盘
                    setBtnEnable();
                }else if(currentEditText == 1){ //第二个输入框
                    edits[currentEditText].setText(letters[primaryCode]);
                    edits[currentEditText].setBackgroundResource(R.drawable.bg_gray);
                    edits[currentEditText + 1].setBackgroundResource(R.drawable.bg_green);
                    //切换为数字字母键盘
                    showKeyboard(3);

                    currentEditText++;
                    if (currentEditText > 6) {
                        currentEditText = 6;
                    }
                    setBtnEnable();
                }else {
                    if(currentEditText != 6) {
                        edits[currentEditText].setBackgroundResource(R.drawable.bg_gray);
                        edits[currentEditText + 1].setBackgroundResource(R.drawable.bg_green);
                    }
                    //切换为数字字母键盘
                    if(currentEditText == 2) {
                        showKeyboard(3);
                    }else if(currentEditText == 5){//切换为数字字母键盘,港澳
                        showKeyboard(4);
                    }
                    if(currentEditText == 6){
                        edits[currentEditText].setText(numberAndLetter2[primaryCode]);
                    }else {
                        edits[currentEditText].setText(numberAndLetter[primaryCode]);
                    }

                    currentEditText++;
                    if (currentEditText > 6) {
                        currentEditText = 6;
                        hideKeyboard();
                    }
                    setBtnEnable();
                }
            }
        }
    };

    /**
     * 显示键盘
     */
    public void showKeyboard() {
        int visibility = keyboardLay.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardLay.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyboard() {
        int visibility = keyboardLay.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardLay.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * 显示键盘
     */
    public void showKeyboard(int flag) {
        showKeyboard();
        if(flag == 1){
            keyboardView.setKeyboard(k1); //省键盘
        }else if(flag == 2){
            keyboardView.setKeyboard(k2);//字母键盘
        }else if(flag == 3){
            keyboardView.setKeyboard(k3);//数字字母键盘
        }else if(flag == 4){
            keyboardView.setKeyboard(k4);//数字字母键盘.港澳
        }
    }

    public void setBackground(int index){
        currentEditText = index;
        for(int i = 0,len = edits.length;i < len;i++){
            if(index == i){
                edits[i].setBackgroundResource(R.drawable.bg_green);
            }else {
                edits[i].setBackgroundResource(R.drawable.bg_gray);
            }
        }
    }

    /**
     * 设置确定按钮可用
     */
    private void setBtnEnable(){
        StringBuffer buffer = new StringBuffer();
        for(int i = 0, len = edits.length;i < len;i++){
            buffer.append(edits[i].getText().toString());
        }
        String textStr = buffer.toString();

        if(textStr.length() == 7) {
            btn.setEnabled(true);//设置确定按钮可用
            btn.setBackgroundResource(R.drawable.selector_btn_click);
        }
    }

}