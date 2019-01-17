package free.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import free.android.activity.NoteMainActivity;
import free.android.common.ActivityCommon;
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
        LogUtil.i(Constants.LOG_MES_TRANSITION_PAGE, "便签功能");
        ToastUtil.longShow(MainActivity.this, "正在进入便签功能,请稍等~");
        Intent noteIntent = new Intent(MainActivity.this, NoteMainActivity.class);
        // 传送数据 --> 迁移画面
        noteIntent.putExtra(Constants.LOG_MES_TRANSITION_FLAG, "note");
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
