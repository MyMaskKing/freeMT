package free.android.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 *  组件帮助类
 * @author Administrator
 *
 */
public class ComponentUtil{

	public static TextView textView;
	public static ImageView imageView;

	public ComponentUtil() {
	}

	public static void setEditextDisable(EditText editext) {
		editext.setEnabled(false);
		editext.setFocusable(true);
		editext.setFocusableInTouchMode(true);
        editext.getBackground().setAlpha(0);
        editext.setTextColor(Color.rgb(0,0,139));
	}

	public static void setEditextEnable(EditText editext) {
		editext.setEnabled(true);
		editext.setFocusable(true);
		editext.setFocusableInTouchMode(true);
        editext.getBackground().setAlpha(255);
		editext.setTextColor(Color.BLACK);
	}

	public static void setButtonDisable(Button button) {
		button.setEnabled(false);
	}

	public static void setButtonEnable(Button button) {
		button.setEnabled(true);
	}

	/**
	 * 走马灯效果(TextView)
	 * @param textView
	 */
	public static void setMarquee(TextView textView) {
		textView.setSingleLine(true);
		textView.setEllipsize(TruncateAt.MARQUEE);
		textView.setSelected(true);
		// 让文字可以水平滑动
		textView.setHorizontallyScrolling(true);
		textView.setMarqueeRepeatLimit(-1);
	}

	/**
	 * 获取选中的Radio的值
	 * @param radioGroup
	 * @return
	 */
	public static String getSelectedRadio(RadioGroup radioGroup) {
		int childCount = radioGroup.getChildCount();
		String val = StringUtil.EMPTY;
		for(int i = 0; i < childCount; i++) {
			RadioButton radioButton = (RadioButton)radioGroup.getChildAt(i);
			if(radioButton.isChecked()) {
				val = StringUtil.isEmptyReturnString(String.valueOf(radioButton.getText()));
				break;
			}
		}
		return val;
	}

	public static void setRadioSelected(RadioGroup radioGroup, String radioVal) {
		int childCount = radioGroup.getChildCount();
		for(int i = 0; i < childCount; i++) {
			RadioButton radioButton = (RadioButton)radioGroup.getChildAt(i);
			if (StringUtil.equaleReturnBoolean(radioVal, radioButton.getText().toString())) {
				radioButton.setChecked(true);
				break;
			}
		}
	}

	public static void setRadioDisable(RadioGroup radioGroup) {
		int childCount = radioGroup.getChildCount();
		for(int i = 0; i < childCount; i++) {
			RadioButton radioButton = (RadioButton)radioGroup.getChildAt(i);
			radioButton.setEnabled(false);
		}
	}

	public static void setRadioEnable(RadioGroup radioGroup) {
		int childCount = radioGroup.getChildCount();
		for(int i = 0; i < childCount; i++) {
			RadioButton radioButton = (RadioButton)radioGroup.getChildAt(i);
			radioButton.setEnabled(true);
		}
	}

	/**
	 * 共通:设置字体样式
	 * @param text
	 * 			文字列
	 * @return SpannableStringBuilder
	 */
	public static SpannableStringBuilder setFontStyle(String text) {
		SpannableStringBuilder msp = new SpannableStringBuilder (text);
		msp.setSpan(new ForegroundColorSpan(Color.rgb(0x00, 0x00, 0xff)),0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new UnderlineSpan(), 0,text.length(), 0);
		return msp;
	}

}
