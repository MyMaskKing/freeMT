package free.android.activity.show_page;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import free.android.R;
import free.android.common.ActivityCommon;

public class ShowPageMainActivity extends ActivityCommon {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 无标题栏(系统自带不删除)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_page_main);

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



}
