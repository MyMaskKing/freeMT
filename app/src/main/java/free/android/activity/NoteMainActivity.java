package free.android.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import free.android.R;
import free.android.common.ActivityCommon;
import free.android.entity.NoteMainEntity;
import free.android.utils.ComponentUtil;
import free.android.utils.Constants;
import free.android.utils.FileUtil;
import free.android.utils.LogUtil;
import free.android.utils.StringUtil;

public class NoteMainActivity extends ActivityCommon {

	private final String FILE_ACTIVITY_NAME = "Location:便签功能(NoteMainActivity.class)";

	private List<NoteMainEntity> noteMainData = new ArrayList<NoteMainEntity>();

	private ListView noteListView;

	/**
	 * 初始化方法
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_main);

		// 获取从其他画面的数据[Start]
		Intent noteIntent = getIntent();
		String extra = noteIntent.getStringExtra(Constants.LOG_MES_TRANSITION_FLAG);
		LogUtil.i(Constants.LOG_MES_TRANSITION_DATA, String.valueOf(extra));
		// 获取从其他画面的数据[End]

		// 获取便签文件内容
		getNoteFileContent();

		noteListView = (ListView) findViewById(R.id.v_id_notel_list);
		/**
		 * 静态打印数据 SimpleAdapter simpleAdapter = new SimpleAdapter(context, data,
		 * resource, from, to) context 上下文 this代替 data 打印数据List resource
		 * 画面Layout的ListView的ID from 打印数据Bean中的数学 ID Name等 to 画面Layout中要放置Bean的数据的组件Id
		 */
		// 自定义打印数据
		noteListView.setAdapter(new NoteMainItem());
		// 自定义点击事件
		noteListView.setOnItemLongClickListener(this);

	}

	/**
	 *  获取便签文件内容
	 */
	private void getNoteFileContent() {
		String externalFilesPath = getExternalFiles();
		try {
			File noteFile = new File(externalFilesPath, Constants.NOTE_FILE_NAME);
			// 文件是否存在Check
			if (!FileUtil.checkFileExist(noteFile)) {
				return;
			}
			BufferedReader fr = new BufferedReader(new FileReader(noteFile));
			String readLine;
			/**
			 * 读取文件时根据ID的不同来获取多个便签实体类
			 */
			Set<String> repeatSet = new HashSet<String>();// By 创建新的实体类标识
			List<String> idRecod = new ArrayList<String>();// By 创建新的实体类之前将上一个实体类的Id记录
			List<NoteMainEntity> noteList = new ArrayList<NoteMainEntity>();
			NoteMainEntity noteMainEntity = new NoteMainEntity();
			int repaetMark = 1;
			while((readLine = fr.readLine()) != null) {
				setEntity(noteMainEntity, readLine, repeatSet, idRecod);
				// 当新的ID获取到时,重新创建实体类
				if (repaetMark != repeatSet.size()) {
					repaetMark = repeatSet.size();
					noteMainEntity.setNoteMasterId(idRecod.get(idRecod.size() - 2));
					noteList.add(noteMainEntity);
					noteMainEntity = new NoteMainEntity();
					noteMainEntity.setNoteMasterId(idRecod.get(idRecod.size() - 1));
				}
			}
			// 将最后一个Bean放入Bean集合
			noteList.add(noteMainEntity);
			noteMainData = noteList;
		} catch (Exception e) {
			Log.e(FILE_ACTIVITY_NAME, Constants.LOG_MES_READY_FILE_ERROR_REASON + e.getMessage());
		}
	}

	/**
	 *  设置实体类中的数据
	 * @param entity
	 * @param readLine
	 * @param repeatSet
	 * 			判断是否要重建实体类
	 */
	private void setEntity(NoteMainEntity entity, String readLine, Set<String> repeatSet, List<String> idRecod) {
		if (readLine != null && !readLine.isEmpty()) {
			String[] split = readLine.split(Constants.EQUAL_SYMBOL);
			String entityAttribueName;
			String entityAttribueData;
			if (split.length > 0) {
				entityAttribueName = split[0];
				if (split.length > 1) {
					entityAttribueData = split[1];
					// 手动设置Entity的数据
					if(Constants.NOTE_MASTER_ID.equals(entityAttribueName)) {
						entity.setNoteMasterId(entityAttribueData);
						/**
						 *  重复标识及Id记录
						 */
						repeatSet.add(entityAttribueData);
						idRecod.add(entityAttribueData);
						/** 主画面信息 */
					}else if (Constants.NOTE_MASTER_TITLE.equals(entityAttribueName)) {
						entity.setNoteMasterTitle(entityAttribueData);
					}else if (Constants.NOTE_MASTER_SPEND_TIME.equals(entityAttribueName)) {
						entity.setNoteMasterSpendTime(entityAttribueData);
					}else if (Constants.NOTE_MASTER_ADDRESS.equals(entityAttribueName)) {
						entity.setNoteMasterAddress(entityAttribueData);
						/** 子画面信息 */
					}else if (Constants.NOTE_SUB_OVER_FLAG.equals(entityAttribueName)) {
						entity.getNoteSubEntity().setNoteSubOverFlag(entityAttribueData);
					}else if (Constants.NOTE_SUB_APPRAISAL.equals(entityAttribueName)) {
						entity.getNoteSubEntity().setNoteSubAppraisal(entityAttribueData);
					}else if (Constants.NOTE_SUB_ITEM.equals(entityAttribueName)) {
						entity.getNoteSubEntity().setNoteSubItem(entityAttribueData);
					}else if (Constants.NOTE_SUB_CITY.equals(entityAttribueName)) {
						entity.getNoteSubEntity().setNoteSubCity(entityAttribueData);
					}else if (Constants.NOTE_SUB_TYPE.equals(entityAttribueName)) {
						entity.getNoteSubEntity().setNoteSubType(entityAttribueData);
					}

				}
			}
		}else {
			Log.i(FILE_ACTIVITY_NAME, "文件未读取到数据");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.note_main_menu, menu);
		return true;
	}

	/**
	 * 菜单部的按钮监听
	 * @param view
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_note_master_add:
			LogUtil.i(FILE_ACTIVITY_NAME, "迁移至便签按钮的添加便签画面");
			Intent noteSubIntent = new Intent(NoteMainActivity.this, NoteSubActivity.class);
			startActivity(noteSubIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * 其他画面返回结果接受
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 打印便签主页的内容 注意点: getCount() 打印数据的Length (必须) getItem() 打印数据每个Bean (必须)
	 * getItemId() 打印数据的索引 (必须) getView() 打印数据的详情 (必须)
	 *
	 * @author Administrator
	 *
	 */
	class NoteMainItem extends BaseAdapter {

		@Override
		public int getCount() {
			return noteMainData.size();
		}

		@Override
		public Object getItem(int position) {
			return noteMainData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView idTv = null;
			TextView titleTv = null;
			TextView addressTv = null;
			TextView spendTimeTv = null;
			TextView overFlagTv = null;
			TextView appraisalTv = null;
			// 实例化组件类
			ComponentUtil componentType = new ComponentUtil();
			// 通过下面的条件判断语句，来循环利用。如果convertView = null ，表示屏幕上没有可以被重复利用的对象。
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.note_main_item, null);
				idTv = (TextView) convertView.findViewById(R.id.v_id_note_main_id);
				titleTv = (TextView) convertView.findViewById(R.id.v_id_note_main_title);
				addressTv = (TextView) convertView.findViewById(R.id.v_id_note_main_address);
				spendTimeTv = (TextView) convertView.findViewById(R.id.v_id_note_main_spend_time);
			} else {
				componentType = (ComponentUtil) convertView.getTag();
			}
			idTv.setText(StringUtil.isEmptyReturnString(noteMainData.get(position).getNoteMasterId()));
			titleTv.setText(StringUtil.isEmptyReturnString(noteMainData.get(position).getNoteMasterTitle()));
			ComponentUtil.setMarquee(titleTv);
			addressTv.setText(StringUtil.isEmptyReturnString(noteMainData.get(position).getNoteMasterAddress()));
			ComponentUtil.setMarquee(addressTv);
			spendTimeTv.setText(StringUtil.isEmptyReturnString(noteMainData.get(position).getNoteMasterSpendTime()));
			LogUtil.i(Constants.LOG_MES_TRANSITION_PAGE, "便签主画面");
			return convertView;
		}
	}

	/**
	 * List中每个项目的点击事件
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		NoteMainEntity entity = (NoteMainEntity)noteListView.getItemAtPosition(position);
		Intent intent = new Intent(NoteMainActivity.this, NoteSubActivity.class);
		intent.putExtra(Constants.ACTION_FALG, "clickItem");
		intent.putExtra("noteEntity", entity);
		startActivity(intent);
		return true;
	}

}
