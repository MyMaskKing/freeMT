package free.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.HashMap;
import java.util.Map;

import free.android.R;
import free.android.common.ActivityCommon;
import free.android.entity.NoteMainEntity;
import free.android.utils.ComponentUtil;
import free.android.utils.Constants;
import free.android.utils.FileUtil;
import free.android.utils.StringUtil;
/**
 *  便签子画面
 * @author dapao
 *
 */
public class NoteSubActivity extends ActivityCommon{

	/**
	 * 初始化方法
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_sub);
		Intent intent = getIntent();
		// 不同功能判断标识
		String actionFlag = intent.getStringExtra(Constants.ACTION_FALG);
		// 便签主画面的每个项目点击事件
		if (StringUtil.equaleReturnBoolean("clickItem", actionFlag)) {
			NoteMainEntity noteMainEntity = (NoteMainEntity)intent.getSerializableExtra("noteEntity");
			EditText titleEditext = (EditText)findViewById(R.id.v_id_note_sub_title_editext);
			titleEditext.setText(noteMainEntity.getNoteMasterTitle());
			ComponentUtil.setEditextDisable(titleEditext);
			EditText itemEditext = (EditText)findViewById(R.id.v_id_note_sub_item_editext);
			itemEditext.setText(noteMainEntity.getNoteSubEntity().getNoteSubItem());
			ComponentUtil.setEditextDisable(itemEditext);
			EditText addressEditext = (EditText)findViewById(R.id.v_id_note_sub_address_editext);
			addressEditext.setText(noteMainEntity.getNoteMasterAddress());
			ComponentUtil.setEditextDisable(addressEditext);
			Button addBtn = (Button)findViewById(R.id.v_id__note_sub_add_button);
			ComponentUtil.setButtonDisable(addBtn);
			RadioGroup typeGroupRadio = (RadioGroup)findViewById(R.id.v_id_note_type_group);
			ComponentUtil.setRadioSelected(typeGroupRadio, noteMainEntity.getNoteSubEntity().getNoteSubCity());
			ComponentUtil.setRadioDisable(typeGroupRadio);
			EditText cityEditext = (EditText)findViewById(R.id.v_id_note_sub_city_editext);
			cityEditext.setText(noteMainEntity.getNoteSubEntity().getNoteSubCity());
			ComponentUtil.setEditextDisable(cityEditext);
			EditText spendTimeHEditext = (EditText)findViewById(R.id.v_id_note_master_spend_time_hour_editext);
			spendTimeHEditext.setText(StringUtil.split(1, noteMainEntity.getNoteMasterSpendTime(), Constants.HOUR_CN));
			ComponentUtil.setEditextDisable(spendTimeHEditext);
			EditText spendTimeMEditext = (EditText)findViewById(R.id.v_id_note_master_spend_time_minute_editext);
			spendTimeMEditext.setText(StringUtil.split(3, noteMainEntity.getNoteMasterSpendTime(), Constants.HOUR_CN, Constants.MINUTE_CN));
			ComponentUtil.setEditextDisable(spendTimeMEditext);
            EditText remarkEditext = (EditText)findViewById(R.id.v_id_note_sub_remark_editext);
            remarkEditext.setText(noteMainEntity.getNoteSubEntity().getNoteSubRemark());
            ComponentUtil.setEditextDisable(remarkEditext);
		}
	}

	/**
	 * 便签子画面Menu部
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.note_sub_menu, menu);
		return true;
	}

	/**
	 * 执行添加便签内容操作
	 */
	public void executeAddBtn(View view) {
		// 放置添加的内容
		Map<String, Object> addContent = new HashMap<String, Object>();
		EditText titleEditext = (EditText)findViewById(R.id.v_id_note_sub_title_editext);
		// 打卡
		String title = titleEditext.getText().toString();
		addContent.put(Constants.NOTE_MASTER_TITLE, title);
		// 打卡项目
		EditText itemEditext = (EditText)findViewById(R.id.v_id_note_sub_item_editext);
		String item = itemEditext.getText().toString();
		addContent.put(Constants.NOTE_SUB_ITEM, item);
		// 打卡目的地
		EditText addressEditext = (EditText)findViewById(R.id.v_id_note_sub_address_editext);
		String address = addressEditext.getText().toString();
		addContent.put(Constants.NOTE_MASTER_ADDRESS, address);
		String externalFilesPath = getFilePathByApp();
		// 项目类型
		RadioGroup typeGroupRadio = (RadioGroup)findViewById(R.id.v_id_note_type_group);
		String selectedTypeRadio = ComponentUtil.getSelectedRadio(typeGroupRadio);
		addContent.put(Constants.NOTE_SUB_TYPE, selectedTypeRadio);
		// 打卡城市
		EditText cityEditext = (EditText)findViewById(R.id.v_id_note_sub_city_editext);
		String city = cityEditext.getText().toString();
		addContent.put(Constants.NOTE_SUB_CITY, city);
		// 花费时长
		EditText spendTimeHEditext = (EditText)findViewById(R.id.v_id_note_master_spend_time_hour_editext);
		String spendTimeH = StringUtil.isEmptyReturnZero(spendTimeHEditext.getText().toString());
		EditText spendTimeMEditext = (EditText)findViewById(R.id.v_id_note_master_spend_time_minute_editext);
		String spendTimeM = StringUtil.isEmptyReturnZero(spendTimeMEditext.getText().toString());
		addContent.put(Constants.NOTE_MASTER_SPEND_TIME, spendTimeH + Constants.HOUR_CN + spendTimeM + Constants.MINUTE_CN);
		// 备注
		EditText remarkEditext = (EditText)findViewById(R.id.v_id_note_sub_remark_editext);
		String remark = remarkEditext.getText().toString();
        addContent.put(Constants.NOTE_SUB_REMARK, remark);
		FileUtil.write(externalFilesPath, Constants.NOTE_FILE_NAME, null, addContent);
		Intent noteAddIntent = new Intent(NoteSubActivity.this, NoteMainActivity.class);
		startActivity(noteAddIntent);

	}

}
