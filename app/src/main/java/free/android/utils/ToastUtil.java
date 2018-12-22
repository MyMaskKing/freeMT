package free.android.utils;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {

	public static void shortShow(Context context, String mes) {
		modifyToastBackground(Toast.makeText(context, mes, Toast.LENGTH_SHORT));
	}

	public static void longShow(Context context, String mes) {
		modifyToastBackground(Toast.makeText(context, mes, Toast.LENGTH_SHORT));
	}

	/**
	 * 修改Toast的样式
	 * 
	 * @param toast
	 */
	private static void modifyToastBackground(Toast toast) {
		// 以横向和纵向的百分比设置显示位置，参数均为 float 类型(水平位移正右负左，竖直位移正上负下)
		toast.setMargin(0, 0);
		// 三个参数分别表示(起点位置,水平向右位移,垂直向下位移)。
		toast.setGravity(0, 0, 0);
		// Toast的背景颜色
		LinearLayout layout = (LinearLayout) toast.getView();
		layout.setBackgroundColor(Color.parseColor("#F5F5F5"));
		// toast显示的文本内容
		TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
		v.setTextColor(Color.parseColor("#3366CC")); // 设置toast的字体颜色
		v.setTextSize(20);
		// 显示弹框
		toast.show();
	}
}
