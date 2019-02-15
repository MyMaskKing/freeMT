package free.android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.SuperscriptSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import free.android.activity.NoteMainActivity;
import free.android.common.ActivityCommon;
import free.android.enums.PageInfoEnum;
import free.android.utils.Constants;
import free.android.utils.LogUtil;
import free.android.utils.ToastUtil;

public class MainActivity extends ActivityCommon {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 无标题栏(系统自带不删除)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 设置欢迎致辞
        TextView welcomeTV = (TextView)findViewById(R.id.id_activity_main_welcom_tx);
        SpannableStringBuilder msp = new SpannableStringBuilder (Constants.WELCOME_WORD);
        //设置字体样式正常，粗体，斜体，粗斜体
        msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 4, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
        msp.setSpan(new ForegroundColorSpan(Color.RED),4, 32, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new SuperscriptSpan(), 10,32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);     //下标
        welcomeTV.setText(msp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 迁移到便签功能界面
     *
     * @param view
     */
    public void transitionNote(View view) {
        LogUtil.i(Constants.LOG_MES_TRANSITION_PAGE_TO, PageInfoEnum.NOTE_PAGE.getVal());
        ToastUtil.longShow(MainActivity.this, "正在进入便签功能,请稍等~");
        Intent noteIntent = new Intent(MainActivity.this, NoteMainActivity.class);
        // 传送数据 --> 迁移画面
        noteIntent.putExtra(Constants.ACTION_FALG, PageInfoEnum.INDEX_PAGE.getKey());
        // 返回结果 Yes No(startActivity(intent))
        startActivityForResult(noteIntent, 1);
        // setContentView(R.layout.note_main);
    }

    /**
     * 其他画面返回结果接受
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
