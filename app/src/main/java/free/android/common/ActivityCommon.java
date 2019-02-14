package free.android.common;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import free.android.MainActivity;
import free.android.R;
import free.android.utils.CollectionsUtil;
import free.android.utils.Constants;
import free.android.utils.StringUtil;

;

public class ActivityCommon extends Activity implements OnItemClickListener, OnItemLongClickListener, OnScrollListener {
    /** 双击时间使用 */
    private static final long DOUBLE_TIME = 1000;
    private static long lastClickTime = 0;

    /*** Check项目方式Error时标记 **/
    private boolean checkErrorFlag = false;

    /*** 对话框Template 1.1 **/
    private  Dialog dialogV1_1 = null;

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
     * 获取当前时间
     *
     * @param timeFormat
     *  <BR>
     *          自定义时间格式
     *
     * @return
     */
    protected static String getSystemTime(String timeFormat) {
        String StrCurrentTime = new SimpleDateFormat(timeFormat)
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
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastClickTime < DOUBLE_TIME) {
            onItemDoubleClick(parent, view, position, id);
        }
        lastClickTime = currentTimeMillis;

	}

    /**
     * 双击事件(ListView:item)
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    protected void onItemDoubleClick(AdapterView<?> parent, View view, int position, long id) {
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
        normalDialog.setIcon(R.drawable.img_background_delete_v1);
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
     * <PRE>
     * 对话框Template 1.1
     * 使用页面(common_dialog_v1.xml)
     * <PRE/>
     * @param contentList <BR/>
     *          Dialog显示内容集合<BR/>
     * @param params <BR/>
     *          1.Dialog类型
     *          2.标题(Title)<BR/>
     *          3.Dialog类型(Content)<BR/>
     *          4.按钮(Yes)<BR/>
     *          5.按钮(No)<BR/>
     */
    protected void showDialogV1_1(List<String> contentList,  String... params) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        if (contentList.isEmpty()) {
            return;
        }
        dialogV1_1  = new Dialog(this);
        //去除标题栏
        dialogV1_1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //2.填充布局
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView     = inflater.inflate(R.layout.common_dialog_v1, null);
        //将自定义布局设置进去
        dialogV1_1.setContentView(dialogView);
        //3.设置指定的宽高,如果不设置的话，弹出的对话框可能不会显示全整个布局，当然在布局中写死宽高也可以
        WindowManager.LayoutParams lp     = new WindowManager.LayoutParams();
        Window window = dialogV1_1.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 显示
        dialogV1_1.show();
        window.setAttributes(lp);
        //设置点击其它地方不让消失弹窗
        dialogV1_1.setCancelable(false);
        // 设置Title
        TextView title = dialogView.findViewById(R.id.id_common_dialog_v1_title);
        title.setText(CollectionsUtil.isEmptyByStrArray(params, 2));
        // 设置按钮1
        TextView btn1 = dialogView.findViewById(R.id.id_common_dialog_v1_btn1);
        btn1.setText(CollectionsUtil.isEmptyByStrArray(params, 3));
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBtn1V1_1();
            }
        });
        // 设置按钮2
        TextView btn2 = dialogView.findViewById(R.id.id_common_dialog_v1_btn2);
        btn2.setText(CollectionsUtil.isEmptyByStrArray(params, 4));
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBtn1V1_2();
            }
        });
        // 判断Dialog类型
        initDilogContentV1_1(contentList, dialogView, CollectionsUtil.isEmptyByStrArray(params, 1));
        
    }

    /**
     * <PRE>
     * 对话框Template 2
     * 使用页面(common_dialog_v2.xml)
     * <PRE/>
     */
    protected void showDialogV2() {
        dialogV1_1  = new Dialog(this);
        //去除标题栏
        dialogV1_1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //2.填充布局
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView     = inflater.inflate(R.layout.common_dialog_v2, null);
        //将自定义布局设置进去
        dialogV1_1.setContentView(dialogView);
        //3.设置指定的宽高,如果不设置的话，弹出的对话框可能不会显示全整个布局，当然在布局中写死宽高也可以
        WindowManager.LayoutParams lp     = new WindowManager.LayoutParams();
        Window window = dialogV1_1.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 显示
        dialogV1_1.show();
        window.setAttributes(lp);
        //设置点击其它地方不让消失弹窗
        dialogV1_1.setCancelable(true);

        /** 设置按钮事件:添加 */
        TextView addByCommonDialogV2 = dialogView.findViewById(R.id.id_common_dialog_v2_add);
        addByCommonDialogV2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClickByCommonDialogV2();
            }
        });
        /** 设置按钮事件:删除 */
        TextView delByCommonDialogV2 = dialogView.findViewById(R.id.id_common_dialog_v2_del);
        delByCommonDialogV2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delClickByCommonDialogV2();
            }
        });
        /** 设置按钮事件:修改 */
        TextView modifyByCommonDialogV2 = dialogView.findViewById(R.id.id_common_dialog_v2_modify);
        modifyByCommonDialogV2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyClickByCommonDialogV2();
            }
        });
    }


    /**
     * <PRE>
     * 对话框Template 2
     * <BR/>
     * 使用页面(common_dialog_v2.xml)
     * <PRE/>
     *  <BR/>
     * 监听事件类型:添加
     */
    protected void addClickByCommonDialogV2() {
    }

    /**
     * <PRE>
     * 对话框Template 2
     * <BR/>
     * 使用页面(common_dialog_v2.xml)
     * <PRE/>
     *  <BR/>
     * 监听事件类型:删除
     */
    protected void delClickByCommonDialogV2() {
    }

    /**
     * <PRE>
     * 对话框Template 2
     * <BR/>
     * 使用页面(common_dialog_v2.xml)
     * <PRE/>
     *  <BR/>
     * 监听事件类型:修改
     */
    protected void modifyClickByCommonDialogV2() {
    }

    /**
     * <PRE>
     * 对话框Template 1.1(按钮Btn2的点击事件)
     * 使用页面(common_dialog_v1.xml)
     * <PRE/>
     */
    protected void onClickBtn1V1_2() {
        dialogV1_1.dismiss();
    }

    /**
     * <PRE>
     * 对话框Template 1.1(按钮Btn1的点击事件)
     * 使用页面(common_dialog_v1.xml)
     * <PRE/>
     */
    protected void onClickBtn1V1_1() {
        dialogV1_1.dismiss();
    }

    /**
     * <PRE>
     * 对话框Template 1.1(初始化内容区域)
     * 使用页面(common_dialog_v1.xml)
     * <PRE/>
     */
    private void initDilogContentV1_1(List<String> contentList, View dialogView, String dialogType) {
        LinearLayout contentLayout = (LinearLayout)dialogView.findViewById(R.id.id_common_dialog_v1_content);
        // Line子布局(线性、Java构成)
        LinearLayout subLineLayout = new LinearLayout(this);
        // Header部子线性布局控件属性(线性、Java构成)
        LinearLayout.LayoutParams subLineLayoutAttribute = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f);
        Iterator<String> valIterator = contentList.iterator();
        while (valIterator.hasNext()) {
            subLineLayout.setBackground(getResources().getDrawable(R.drawable.border_line_v3));
            // 设置Header部子线性布局控件横向属性(线性、Java构成)
            subLineLayout.setOrientation(LinearLayout.HORIZONTAL);
            String val = valIterator.next();
            TextView textView = new TextView(this);
            SpannableStringBuilder msp = new SpannableStringBuilder (val);
            if (StringUtil.equaleReturnBoolean(Constants.ERROR_MARK, dialogType)) {
                //设置字体样式正常，粗体，斜体，粗斜体
                msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, val.split(Constants.COLON_SYMBOL)[0].length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
                msp.setSpan(new ForegroundColorSpan(Color.RED),val.split(Constants.COLON_SYMBOL)[0].length()+1,val.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                //设置上下标
                //msp.setSpan(new SubscriptSpan(), val.split(Constants.COLON_SYMBOL)[0].length()+1,val.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);     //下标
                //设置字体大小（相对值,单位：像素） 参数表示为默认字体宽度的多少倍
                //msp.setSpan(new ScaleXSpan(2.0f), 0, val.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //2.0f表示默认字体宽度的两倍，即X轴方向放大为默认字体的两倍，而高度不变
            }else {
                //设置字体样式正常，粗体，斜体，粗斜体
                msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, val.split(Constants.COLON_SYMBOL)[0].length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
                msp.setSpan(new ForegroundColorSpan(Color.BLUE),0,val.split(Constants.COLON_SYMBOL)[0].length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                //设置上下标
                //msp.setSpan(new SuperscriptSpan(), val.split(Constants.COLON_SYMBOL)[0].length()+1,val.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);     //上标
                //设置字体大小（相对值,单位：像素） 参数表示为默认字体宽度的多少倍
                //msp.setSpan(new ScaleXSpan(2.0f), 0, val.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //2.0f表示默认字体宽度的两倍，即X轴方向放大为默认字体的两倍，而高度不变
            }

            textView.setText(msp);
            subLineLayout.addView(textView, subLineLayoutAttribute);
            contentLayout.addView(subLineLayout);
            subLineLayout = new LinearLayout(this);
        }
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


    /**
     *
     * 共通:返回首页
     */
    protected void commonReturnIndex() {
        Intent intent = new Intent(ActivityCommon.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * 设置CheckError标记
     */
    protected void setError() {
        checkErrorFlag = true;
    }
    /**
     * 返回CheckError标记
     */
    protected boolean isError() {
        return checkErrorFlag;
    }

}
