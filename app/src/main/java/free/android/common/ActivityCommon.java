package free.android.common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import free.android.utils.Constants;

public class ActivityCommon extends Activity implements OnItemClickListener, OnItemLongClickListener, OnScrollListener {

	/**
	 * <pre>
	 * 获取App专属文件路径
	 * </pre>
	 *
	 * 特性:随着app删除而一起删除(不可见)
	 *
	 * @return
	 */
	protected String getExternalFiles() {
		/**
		 * Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) //
		 * 判断是否已装入SD卡
		 *
		 * App独立文件:不依赖自己App的文件，例如图片、视频等(可见) 两种获取路径方式： A: File sdCard =
		 * Environment.getExternalStorageDirectory(); // SD卡根路径 File directory_pictures
		 * = new File(sdCard, "Pictures"); //定访问的文件夹名 B: File directory_pictures =
		 * Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		 * // 访问公共目录的方法
		 */
		// App专属文件:随着app删除而一起删除(不可见)
		// external storage就是通常所说的SD卡
		// internal storage就是手机自带的一块存储区域
		File externalFilesDir = getExternalFilesDir("Caches");
		return externalFilesDir.getParent();
	}

	/**
	 * 获取当前时间 精确到毫秒(字符形式)
	 *
	 * @return
	 */
	protected static String getIdByTime() {
		String StrCurrentTime = new SimpleDateFormat(Constants.TIME_YYYY_MM_DD_HH_MM_SS_SSS_NO_SYMBOL)
				.format(new Date());
		return StrCurrentTime;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		return false;
	}

}
