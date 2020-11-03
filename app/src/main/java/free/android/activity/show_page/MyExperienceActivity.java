package free.android.activity.show_page;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import free.android.R;
import free.android.utils.DBUtils;

public class MyExperienceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_experience_main);
    }

    /**
     * 検索をクリックする
     *
     * @param view
     */
    public void selectBtn(View view) {
        DBUtils db = new DBUtils();
        TextView selectContent = findViewById(R.id.my_experience_select_content);
    }
}
