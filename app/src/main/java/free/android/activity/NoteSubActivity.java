package free.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import free.android.R;
import free.android.common.ActivityCommon;
import free.android.entity.NoteMainEntity;
import free.android.utils.ComponentUtil;
import free.android.utils.Constants;
import free.android.utils.FileUtil;
import free.android.utils.LogUtil;
import free.android.utils.StringUtil;
import free.android.utils.ToastUtil;

/**
 *  便签子画面
 * @author dapao
 *
 */
public class NoteSubActivity extends ActivityCommon{
	private final String FILE_ACTIVITY_NAME = "便签子画面(NoteSubActivity)";
	/**
	 * 初始化方法
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 无标题栏(系统自带不删除)
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏效果
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//隐藏状态栏
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//显示状态栏
		// getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_sub);
		Intent intent = getIntent();
		// 不同功能判断标识
		String actionFlag = intent.getStringExtra(Constants.ACTION_FALG);
		// 便签主画面的每个项目点击事件
		if (StringUtil.equaleReturnBoolean("clickItem", actionFlag)) {
			NoteMainEntity noteMainEntity = (NoteMainEntity)intent.getSerializableExtra("noteEntity");
			TextView idHidden = (TextView)findViewById(R.id.v_id_note_sub_id);
			idHidden.setText(noteMainEntity.getNoteMasterId());
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

		Button menuBtn= (Button) findViewById(R.id.v_id__note_sub_menu);
		registerForContextMenu(menuBtn);
		menuBtn.setOnCreateContextMenuListener(this);//给组件注册Context菜单
		menuBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.showContextMenu();//单击直接显示Context菜单
			}
		});

	}

	/**
	 * 便签子画面Menu部
	 * @param menu
	 * @return
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
		// 选择菜单 一样 进行打气使用
		getMenuInflater().inflate(R.menu.note_sub_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/**
	 * 菜单部的按钮监听
	 * @param item
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			// 便签子画面的删除按钮
			case R.id.menu_note_sub_del:
				LogUtil.i(FILE_ACTIVITY_NAME, "执行删除动作开始");
				showDialogV1("删除", "确认要删除吗?", "是", "否");
				LogUtil.i(FILE_ACTIVITY_NAME, "执行删除动作结束");
				// 返回结果(Dialog使用)
				return false;
			// 便签子画面的修改按钮
			case R.id.menu_note_sub_modify:
				boolean copyFlag = FileUtil.copy(getFilePathByApp(), Constants.NOTE_FILE_NAME, getFilePathBySDCard(), Constants.NOTE_FILE_NAME);
				if(copyFlag) {
					ToastUtil.longShow(this, "下载成功(" + "文件路径:" + getFilePathBySDCard() + "文件名" + Constants.NOTE_FILE_NAME);
				} else {
					ToastUtil.shortShow(this, "下载失败");
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * 对话框Template 1.0的确认按钮执行内容
	 * @return
	 */
	protected void clickConfirmBthByDialog(){
        Map<String, Object> addContent = commonSetWriteContent();
		// 获取删除Id
        TextView idHidden = (TextView)findViewById(R.id.v_id_note_sub_id);
		String id = idHidden.getText().toString();
		addContent.put(Constants.NOTE_MASTER_ID, id);
        // 删除标记ON
        addContent.put(Constants.NOTE_SUB_DELETE_FLAG, Constants.DELETE_ON);
        commonTransitionPage(addContent);
	}

	/**
	 * 执行添加便签内容操作
	 */
	public void executeAddBtn(View view) {
        Map<String, Object> addContent = commonSetWriteContent();
        commonTransitionPage(addContent);

	}

    /**
     * 共通:获取页面内容并放入Map中
     * @return
     */
	private Map<String, Object> commonSetWriteContent(){
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
        // 删除标记
        addContent.put(Constants.NOTE_SUB_DELETE_FLAG, Constants.DELETE_OFF);
        // 更新标记
        addContent.put(Constants.NOTE_SUB_UPDATE_COUNT, Constants.UPDATE_DEFAULT_COUNT);
        return addContent;
    }

    /**
     * 共通:写入新的数据并跳转画面
     * @param addContent
     *          向Text中放入的数据
     */
    private void commonTransitionPage(Map<String, Object> addContent) {
        String externalFilesPath = getFilePathByApp();
        FileUtil.write(externalFilesPath, Constants.NOTE_FILE_NAME, null, addContent);
        Intent noteSubIntent = new Intent(NoteSubActivity.this, NoteMainActivity.class);
        startActivity(noteSubIntent);
    }

}
