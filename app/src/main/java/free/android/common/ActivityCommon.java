package free.android.common;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import free.android.utils.CollectionsUtil;
import free.android.utils.Constants;
import free.android.utils.StringUtil;

;

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
	protected String getFilePathByApp() {
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
     * 获取SD卡路径
     * @return
     */
	protected  String getFilePathBySDCard() {
        return Environment.getExternalStorageDirectory().toString() + "/freeMT/Data";
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
    /**
     * 获取储存权限
     * @param activity
     * @return
     */

    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }
        return true;
    }

    /**
     * @return
     */
    protected Map<String, String> getUploadPath(Intent data) {
        Map<String, String> pathMap = new HashMap<String, String>();
        Uri uri = data.getData();
        StringBuffer filePath = new StringBuffer();
        //使用第三方应用打开
        String path = StringUtil.EMPTY;
        if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
            path = uri.getPath();
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) { // 4.4以后
            path = getPath(this, uri);
        } else{ // //4.4以下下系统调用方法
            path = getRealPathFromURI(uri);
        }

        String[] filePathSplit = path.split("/");
        if(filePathSplit.length > 0){
            String fileName = filePathSplit[filePathSplit.length - 1];
            pathMap.put(Constants.UPLOAD_FILE_NAME, fileName);
            for(int i = 0; i < filePathSplit.length-1; i++){
                filePath.append(filePathSplit[i]);
                filePath.append("/");
            }
            pathMap.put(Constants.UPLOAD_FILE_PATH, filePath.toString());
        }

        return pathMap;
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /**
     * 专为Android . 设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 对话框Template 1.0
     * @param params <BR/>
     *          1.标题(Title)<BR/>
     *          2.内容(Content)<BR/>
     *          3.按钮(Yes)<BR/>
     *          3.按钮(No)<BR/>
     */
    protected void showDialogV1(String... params) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ActivityCommon.this);
        // 设置Dialog的图标
        //normalDialog.setIcon(R.drawable.icon_dialog);
        normalDialog.setTitle(CollectionsUtil.isEmptyByStrArray(params,1));
        normalDialog.setMessage(CollectionsUtil.isEmptyByStrArray(params,2));
        normalDialog.setPositiveButton(CollectionsUtil.isEmptyByStrArray(params,3),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       clickConfirmBthByDialog();
                    }
                });
        normalDialog.setNegativeButton(CollectionsUtil.isEmptyByStrArray(params,4),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clickCloseBthByDialog();
                    }
                });
        // 显示
        normalDialog.show();
    }

    /**
     * 对话框Template 1.0的确认按钮执行内容
     * @return
     */
    protected void clickConfirmBthByDialog(){
    }

    /**
     * 对话框Template 1.0的取消按钮执行内容
     * @return
     */
    protected void clickCloseBthByDialog(){
    }
}
