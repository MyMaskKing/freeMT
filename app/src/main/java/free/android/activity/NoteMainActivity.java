package free.android.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import free.android.R;
import free.android.common.ActivityCommon;
import free.android.entity.NoteEntity;
import free.android.enums.PageInfoEnum;
import free.android.holder.NoteMainBodyHolder;
import free.android.utils.CollectionsUtil;
import free.android.utils.ComponentUtil;
import free.android.utils.Constants;
import free.android.utils.FileUtil;
import free.android.utils.LogUtil;
import free.android.utils.StringUtil;
import free.android.utils.ToastUtil;

public class NoteMainActivity extends ActivityCommon {

	private final String FILE_ACTIVITY_NAME = "Location:便签功能(NoteMainActivity.class)";

	private List<NoteEntity> noteMainBodyData = new ArrayList<NoteEntity>();
    private List<NoteEntity> noteMainBodyDataBackUp = new ArrayList<NoteEntity>();
    private List<String> noteMainHeaderData = new ArrayList<String>();
    private List<String> noteMainHeaderHiddenData = new ArrayList<String>();
    // 便签画面Body部
    private ListView noteBodyListView;
    private Set<String> cityList = new HashSet<String>(); // 未使用
    private Set<String> typeList = new HashSet<String>(); // 未使用
    // 便签:标签集合
    private Set<String> tagDataSet = new HashSet<String>();
    private Set<String> idOfDeleteData = new HashSet<String>();
    private Set<String> idOfUpdateData = new HashSet<String>();
    private List<CheckBox> checkBoxList = new ArrayList<CheckBox>();
    private List<String> checkBoxHidden = new ArrayList<String>(); // 未使用
    private NoteEntity commonNoteEntity;
    /**
     * 初始化方法
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        // 无标题栏(系统自带不删除)
        requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_main);

		// 获取从其他画面的数据[Start]
		Intent noteIntent = getIntent();
        String actionFlag = noteIntent.getStringExtra(Constants.ACTION_FALG);
        /** From 便签子画面[Condition:非(副)便签迁移|当前页面级别不为1|便签ID不为空] */
        if (StringUtil.equaleReturnBoolean(PageInfoEnum.NOTE_SUB_PAGE.getKey(), actionFlag)
                && !StringUtil.equaleReturnBoolean(String.valueOf(Constants.NOTE_CURRENT_PAGE_LEVEL_DEFAULT_VALUE), noteIntent.getStringExtra(Constants.NOTE_MATCH_CONDITION_PAGE_LEVEL))) {
            LogUtil.i(Constants.LOG_MES_TRANSITION_PAGE_FROM, PageInfoEnum.NOTE_SUB_PAGE.getVal());
            Map<String, String> matchingCondition = new HashMap<>();
            matchingCondition.put(Constants.NOTE_MATCH_CONDITION_PAGE_LEVEL, noteIntent.getStringExtra(Constants.NOTE_MATCH_CONDITION_PAGE_LEVEL));
            initNotePage(matchingCondition);
            setCurrentPageLevel(noteIntent.getStringExtra(Constants.NOTE_MATCH_CONDITION_PAGE_LEVEL));
        }else{
            Map<String, String> matchingCondition = new HashMap<>();
            matchingCondition.put(Constants.NOTE_MATCH_CONDITION_PAGE_LEVEL, String.valueOf(Constants.NOTE_CURRENT_PAGE_LEVEL_DEFAULT_VALUE));
            initNotePage(matchingCondition);
            setCurrentPageLevel(String.valueOf(Constants.NOTE_CURRENT_PAGE_LEVEL_DEFAULT_VALUE));
        }
        // 获取从其他画面的数据[End]

        enablePreviousOnClickListener(true);
        // 自定义Menu
        TextView menuTv = (TextView) findViewById(R.id.v_id_note_main_menu_tv);
        registerForContextMenu(menuTv);
        menuTv.setOnCreateContextMenuListener(this);//给组件注册Context菜单
        menuTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.showContextMenu();//单击直接显示Context菜单
            }
        });
    }

    /**
     * 设置导航栏部:当前页面级别
     * @param currentPageLevel
     */
    private void setCurrentPageLevel(String currentPageLevel){
        TextView currentPageLevelTv = (TextView) findViewById(R.id.v_id_note_main_current_page_level_tv);
        currentPageLevelTv.setText(currentPageLevel);
    }

    /**
     * 便签子画面Menu部
     * @param menu
     * @return
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        // 选择菜单 一样 进行打气使用
        getMenuInflater().inflate(R.menu.note_main_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    /**
     * 初始化便签页面
     * @param matchingCondition
     *              筛选条件
     */
    private void initNotePage(Map<String, String> matchingCondition) {
        // 获取便签文件内容
        getNoteFileContent();
        againSetNoteBodyData(noteMainBodyData);
        /** 便签:Body部 */
        // 便签Body部List
        printNoteMainBodyPage();
        matchingResult(matchingCondition);
        /** 便签:Header部 */
        // 通过便签文件内容获取便签Header部数据
        setNoteMainHeaderData();
    }

    /**
     * 检索结果筛选
     * @param datas
     */
	private void againSetNoteBodyData(List<NoteEntity> datas) {
	    // 新的便签Body部数据集合
        List<NoteEntity> againNoteBodyData = new ArrayList<NoteEntity>();
        // 获取更新次数最大的更新数据集合
        List<NoteEntity> mxaUpdateData = getLastNewUpdateData();
        Iterator<NoteEntity> valIterator = noteMainBodyData.iterator();
        while (valIterator.hasNext()) {
            NoteEntity val = valIterator.next();
            Iterator<String> delDataIterator = idOfDeleteData.iterator();
            String currentDataDelFlag = Constants.DELETE_OFF;
            while (delDataIterator.hasNext()) {
                String delDataId = delDataIterator.next();
                if (StringUtil.equaleReturnBoolean(delDataId, val.getNoteId())) {
                    currentDataDelFlag = Constants.DELETE_ON;
                    break;
                }
            }
            // 已删除数据排除
            if (StringUtil.equaleReturnBoolean(Constants.DELETE_ON, currentDataDelFlag)) {
                continue;
            }
            // 非最大更新次数数据排除
            Iterator<NoteEntity> updDataIterator = mxaUpdateData.iterator();
            String lastNewUpdateFlag = StringUtil.EMPTY;
            while (updDataIterator.hasNext()) {
                NoteEntity updateData = updDataIterator.next();
                if (StringUtil.equaleReturnBoolean(val.getNoteId(), updateData.getNoteId())
                        && !StringUtil.equaleReturnBoolean(val.getNoteUpdateCount(), updateData.getNoteUpdateCount())) {
                    lastNewUpdateFlag = Constants.CANCEL_MARK;
                    break;
                }
            }
            // 非最大更新次数数据排除
            if (StringUtil.equaleReturnBoolean(Constants.CANCEL_MARK, lastNewUpdateFlag)) {
                continue;
            }
            againNoteBodyData.add(val);
            // 便签:标记集合
            if (!StringUtil.isEmptyReturnBoolean(val.getNoteTag())) {
                cityList.add(val.getNoteTag());
            }
        }
        noteMainBodyData = againNoteBodyData;
    }

    /**
     * 获取最新的更新数据集
     */
    private List<NoteEntity> getLastNewUpdateData() {
        // 最终更新数据集合
        List<NoteEntity> updateEndData = new ArrayList<NoteEntity>();
        // 更新数据集合临时保存
        List<NoteEntity> updateData = new ArrayList<NoteEntity>();
        Iterator<String> updateValIterator = idOfUpdateData.iterator();
        // 更新数据的Id遍历
        while(updateValIterator.hasNext()) {
            updateData.clear();
            String updateVal = updateValIterator.next();
            Iterator<NoteEntity> noteMainEntityIterator = noteMainBodyData.iterator();
            while (noteMainEntityIterator.hasNext()) {
                NoteEntity val = noteMainEntityIterator.next();
                if(StringUtil.equaleReturnBoolean(updateVal, val.getNoteId())) {
                    updateData.add(val);
                };
            }
            NoteEntity maxUpdateCountData = getMaxUpdateCountData(updateData);
            updateEndData.add(maxUpdateCountData);
        }
        return updateEndData;
    }

    /**
     * 获取更新数最大的List
     * @param updateData
     */
    private NoteEntity getMaxUpdateCountData(List<NoteEntity> updateData) {
        NoteEntity entity = new NoteEntity();
        int updateCountCache = 0;
        Iterator<NoteEntity> entityIterator = updateData.iterator();
        while (entityIterator.hasNext()){
            NoteEntity noteEntity = entityIterator.next();
            if (updateCountCache < StringUtil.isEmptyReturnInteger(noteEntity.getNoteUpdateCount())) {
                updateCountCache = StringUtil.isEmptyReturnInteger(noteEntity.getNoteUpdateCount());
                entity = noteEntity;
            }
        }
        return entity;
    }

    /**
	 *  获取便签文件内容
	 */
	private void getNoteFileContent() {
		String externalFilesPath = getFilePathByApp();
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
			List<NoteEntity> noteList = new ArrayList<NoteEntity>();
			NoteEntity noteEntity = new NoteEntity();
			int repaetMark = 1;
			int count = 1;
			while((readLine = fr.readLine()) != null) {
			    if (readLine.contains("#COMENT#")) {
			        continue;
                }
			    if(count <= Constants.NOTE_FILE_DATA_COUNT) {
                    setEntity(noteEntity, readLine, repeatSet, idRecod);
                    if(count == Constants.NOTE_FILE_DATA_COUNT) {
                        count =  1;
                        noteList.add(noteEntity);
                        noteEntity = new NoteEntity();
                    } else{
                        count++;
                    }
                }

				/*// 当新的ID获取到时,重新创建实体类
				if (repaetMark != repeatSet.size()) {
					repaetMark = repeatSet.size();
					noteMainEntity.setNoteMasterId(idRecod.get(idRecod.size() - 2));
					noteList.add(noteMainEntity);
					noteMainEntity = new NoteMainEntity();
					noteMainEntity.setNoteMasterId(idRecod.get(idRecod.size() - 1));
                }*/
			}
			// 将最后一个Bean放入Bean集合TODO
           // noteList.add(noteMainEntity);
			noteMainBodyData = noteList;
		} catch (Exception e) {
			LogUtil.e(FILE_ACTIVITY_NAME, Constants.LOG_MES_READY_FILE_ERROR_REASON + e.getMessage());
		}
	}

	/**
	 *  设置实体类中的数据
	 * @param entity
	 * @param readLine
	 * @param repeatSet
	 * 			判断是否要重建实体类
	 */
	private void setEntity(NoteEntity entity, String readLine, Set<String> repeatSet, List<String> idRecod) {
		if (readLine != null && !readLine.isEmpty()) {
			String[] split = readLine.split(Constants.EQUAL_SYMBOL);
			String entityAttribueName;
			String entityAttribueData;
			if (split.length > 0) {
				entityAttribueName = split[0];
				if (split.length > 1) {
					entityAttribueData = split[1];
					// 手动设置Entity的数据
					if(Constants.NOTE_ID.equals(entityAttribueName.trim())) {
						entity.setNoteId(entityAttribueData);
						/**
						 *  重复标识及Id记录
						 */
						repeatSet.add(entityAttribueData);
						idRecod.add(entityAttribueData);
                        /** 便签画面信息:副便子ID */
                    }else if (Constants.NOTE_SUB_ID.equals(entityAttribueName)) {
                        entity.setNoteSubId(entityAttribueData);
                        /** 便签画面信息:副父签ID */
                    }else if (Constants.NOTE_PARENT_ID.equals(entityAttribueName)) {
                        entity.setNoteParentId(entityAttribueData);
						/** 便签画面信息:便签内容 */
					}else if (Constants.NOTE_CONTENT.equals(entityAttribueName)) {
                        entity.setNoteContent(entityAttribueData);
                        /** 便签画面信息:标签内容 */
					}else if (Constants.NOTE_TAG.equals(entityAttribueName)) {
						entity.setNoteTag(entityAttribueData);
                        /** 便签画面信息:录入时间 */
					}else if (Constants.NOTE_INSERT_TIME.equals(entityAttribueName)) {
						entity.setNoteInsertTime(entityAttribueData);
                        /** 便签画面信息:更新时间 */
					}else if (Constants.NOTE_UPDATE_TIME.equals(entityAttribueName)) {
                        entity.setNoteUpdateTime(entityAttribueData);
                        /** 便签画面信息:删除时间 */
					}else if (Constants.NOTE_DELETE_TIME.equals(entityAttribueName)) {
                        entity.setNoteDeleteTime(entityAttribueData);
                        /** 便签画面信息:当前页面级别 */
                    }else if (Constants.NOTE_CURRENT_PAGE_LEVEL.equals(entityAttribueName)) {
                        entity.setNoteCurrentPageLevel(entityAttribueData);
                        /** 便签画面信息:自便签录入时间 */
                    }else if (Constants.SUB_NOTE_INSERT_TIME.equals(entityAttribueName)) {
                        entity.setSubNoteInsertTime(entityAttribueData);
                        /** 便签画面信息:删除标记 */
					}else if (Constants.NOTE_DELETE_FLAG.equals(entityAttribueName)) {
                        entity.setNoteDeleteFlag(entityAttribueData);
                        if (Constants.DELETE_ON.equals(entityAttribueData)) {
                            idOfDeleteData.add(entity.getNoteId());
                        }
                        /** 便签画面信息:更新次数 */
                    }else if (Constants.NOTE_UPDATE_COUNT.equals(entityAttribueName)) {
                        entity.setNoteUpdateCount(entityAttribueData);
                        if (!Constants.UPDATE_DEFAULT_COUNT.equals(entityAttribueData)) {
                            idOfUpdateData.add(entity.getNoteId());
                        }
					}
				}
			}
		}else {
			LogUtil.i(FILE_ACTIVITY_NAME, "文件未读取到数据");
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
	 * @param item
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_note_master_add:
			LogUtil.i(FILE_ACTIVITY_NAME, "迁移至便签按钮的添加便签画面");
            TextView currentPageLevelTv = (TextView)findViewById(R.id.v_id_note_main_current_page_level_tv);
            String currentPageLevelStr = currentPageLevelTv.getText().toString();
            Intent noteSubIntent = new Intent(NoteMainActivity.this, NoteSubActivity.class);
            noteSubIntent.putExtra(Constants.NOTE_MATCH_CONDITION_PAGE_LEVEL, currentPageLevelStr);
			startActivity(noteSubIntent);
			return true;
		case R.id.menu_note_master_download:
            isGrantExternalRW(NoteMainActivity.this);
            boolean copyFlag = FileUtil.copy(getFilePathByApp(), Constants.NOTE_FILE_NAME, getFilePathBySDCard(), Constants.NOTE_FILE_NAME);
            if(copyFlag) {
                ToastUtil.longShow(this, "下载成功(" + "文件路径:" + getFilePathBySDCard() + "文件名" + Constants.NOTE_FILE_NAME);
            } else {
                ToastUtil.shortShow(this, "下载失败");
            }
            return true;
        case R.id.menu_note_master_upload:
            // 文件上传开启选择文件
            Intent uploadIntent = new Intent(Intent.ACTION_GET_CONTENT);
            uploadIntent.setType("*/*");//无类型限制
            uploadIntent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(uploadIntent, Constants.REQUEST_CODE_UPLOAD);
            return true;
        case R.id.menu_note_master_download_template:
            String filePath = getFilePathBySDCard();
            List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < 10; i++) {
                // 放置添加的内容
                Map<String, Object> map = new HashMap<String, Object>();
                // 便签:便签ID
                map.put(Constants.NOTE_ID, StringUtil.EMPTY);
                // 便签:便签子ID
                map.put(Constants.NOTE_SUB_ID, StringUtil.EMPTY);
                // 便签:便签内容
                map.put(Constants.NOTE_CONTENT, StringUtil.EMPTY);
                // 便签:便签标签
                map.put(Constants.NOTE_TAG, StringUtil.EMPTY);
                // 便签:录入时间
                map.put(Constants.NOTE_INSERT_TIME, StringUtil.EMPTY);
                // 便签:更新时间
                map.put(Constants.NOTE_UPDATE_TIME, StringUtil.EMPTY);
                // 便签:删除时间
                map.put(Constants.NOTE_DELETE_TIME, StringUtil.EMPTY);
                // 便签:更新次数
                map.put(Constants.NOTE_UPDATE_COUNT, Constants.UPDATE_DEFAULT_COUNT);
                // 便签:删除标记
                map.put(Constants.NOTE_DELETE_FLAG, Constants.DELETE_OFF);
                listMap.add(map);
            }

            if(FileUtil.write(filePath, Constants.NOTE_FILE_NAME_DOWNLOAD_TEMPLATE, listMap, null)) {
                ToastUtil.longShow(this, "下载成功(" + "文件路径:" + getFilePathBySDCard() + "文件名" + Constants.NOTE_FILE_NAME_DOWNLOAD_TEMPLATE);
            } else {
                ToastUtil.shortShow(this, "下载失败");
            }
            return true;
        case R.id.menu_note_master_return:
            Map<String, String> matchingCondition = new HashMap<>();
            matchingCondition.put(Constants.NOTE_MATCH_CONDITION_PAGE_LEVEL, String.valueOf(Constants.NOTE_CURRENT_PAGE_LEVEL_DEFAULT_VALUE));
            initNotePage(matchingCondition);
            setCurrentPageLevel(String.valueOf(Constants.NOTE_CURRENT_PAGE_LEVEL_DEFAULT_VALUE));
            return true;
        case R.id.menu_note_master_index:
            commonReturnIndex();
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
        if(requestCode == Constants.REQUEST_CODE_UPLOAD) {
            Map<String, String> uploadPath = getUploadPath(data);
            boolean copyFlag = FileUtil.copy(uploadPath.get(Constants.UPLOAD_FILE_PATH), uploadPath.get(Constants.UPLOAD_FILE_NAME), getFilePathByApp(), Constants.NOTE_FILE_NAME);
            if(copyFlag){
                ToastUtil.shortShow(this, "上传成功~(文件名:" + uploadPath.get(Constants.UPLOAD_FILE_NAME) + ")");
                Intent transitionIntent = new Intent(NoteMainActivity.this, NoteMainActivity.class);
                startActivity(transitionIntent);
            }else {
                ToastUtil.shortShow(this, "上传失败");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

	/**
	 * 打印便签主页Body部内容 注意点: getCount() 打印数据的Length (必须) getItem() 打印数据每个Bean (必须)
	 * getItemId() 打印数据的索引 (必须) getView() 打印数据的详情 (必须)
	 *
	 * @author Administrator
	 *
	 */
	class NoteMainBodyItem extends BaseAdapter {

		@Override
		public int getCount() {
			return noteMainBodyData.size();
		}

		@Override
		public Object getItem(int position) {
			return noteMainBodyData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
            NoteMainBodyHolder holder = new NoteMainBodyHolder();
			// 通过下面的条件判断语句，来循环利用。如果convertView = null ，表示屏幕上没有可以被重复利用的对象。
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.note_main_body_item, null);
                holder.noTv = (TextView) convertView.findViewById(R.id.v_id_note_main_no);
                holder.idTv = (TextView) convertView.findViewById(R.id.v_id_note_main_id);
                holder.subIdTv = (TextView) convertView.findViewById(R.id.v_id_note_main_sub_id);
                holder.contentTv = (TextView) convertView.findViewById(R.id.v_id_note_main_content);
                holder.tagTv = (TextView) convertView.findViewById(R.id.v_id_note_main_tag);
                holder.tagTitleTv = (TextView) convertView.findViewById(R.id.v_id_note_main_tag_title);
                convertView.setTag(holder);
			} else {
                holder = (NoteMainBodyHolder) convertView.getTag();
			}
            holder.idTv.setText(StringUtil.isEmptyReturnString(noteMainBodyData.get(position).getNoteId()));
            holder.subIdTv.setText(StringUtil.isEmptyReturnString(noteMainBodyData.get(position).getNoteSubId()));
            holder.contentTv.setText(StringUtil.isEmptyReturnString(noteMainBodyData.get(position).getNoteContent()));
            ComponentUtil.setMarquee(holder.contentTv);
            /**
             * 便签:标签无内容时不显示
             */
            if (!StringUtil.isEmptyReturnBoolean(noteMainBodyData.get(position).getNoteTag())) {
                holder.tagTv.setText(StringUtil.isEmptyReturnString(noteMainBodyData.get(position).getNoteTag()));
                ComponentUtil.setMarquee(holder.tagTv);
            }else {
                holder.tagTitleTv.setText(StringUtil.EMPTY);
            }
            holder.noTv.setText(StringUtil.isEmptyReturnString(noteMainBodyData.get(position).getNoteNo()));
            /**
             * 便签:每隔一行变换颜色,当前数据有子数据时更换其他颜色
             */
            if((position + 1) % 2 == 0
                    && StringUtil.isEmptyReturnBoolean(noteMainBodyData.get(position).getNoteSubId())) {
                convertView.setBackground(getResources().getDrawable(R.drawable.background_normal_v1));
            } else if ((position + 1) % 2 == 0 &&
                    !StringUtil.isEmptyReturnBoolean(noteMainBodyData.get(position).getNoteSubId())){
                convertView.setBackground(getResources().getDrawable(R.drawable.background_info_v1));
            } else if ((position + 1) % 2 != 0 &&
                    !StringUtil.isEmptyReturnBoolean(noteMainBodyData.get(position).getNoteSubId())){
                convertView.setBackground(getResources().getDrawable(R.drawable.background_info_v2));
            }else {
                convertView.setBackground(getResources().getDrawable(R.drawable.background_normal_v2));
            }

			return convertView;
		}
	}

	/**
	 * List中每个项目的点击事件
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showDialogV2();
        commonNoteEntity = (NoteEntity)noteBodyListView.getItemAtPosition(position);
		return true;
	}

    /**
     * 点击事件:添加副便签
     */
    protected void addClickByCommonDialogV2() {
		Intent intent = new Intent(NoteMainActivity.this, NoteSubActivity.class);
		intent.putExtra(Constants.ACTION_FALG, "clickItem");
		intent.putExtra("noteEntity", commonNoteEntity);
        intent.putExtra(Constants.ACTION_MODE, Constants.STR_ADD);
		startActivity(intent);
    }

    /**
     * 点击事件:查看当前便签
     */
    protected void lookUpClickByCommonDialogV2() {
        Intent intent = new Intent(NoteMainActivity.this, NoteSubActivity.class);
        intent.putExtra(Constants.ACTION_FALG, "clickItem");
        intent.putExtra("noteEntity", commonNoteEntity);
        intent.putExtra(Constants.ACTION_MODE, Constants.STR_LOOK_UP);
        startActivity(intent);
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
        Intent intent = new Intent(NoteMainActivity.this, NoteSubActivity.class);
        intent.putExtra(Constants.ACTION_FALG, "clickItem");
        intent.putExtra("noteEntity", commonNoteEntity);
        intent.putExtra(Constants.ACTION_MODE, Constants.STR_MODIFY);
        startActivity(intent);
    }

    /**
     * 获取数据By便签Header部
     */
    private void setNoteMainHeaderData(){
        // (使用中集合)数据情况
        noteMainHeaderData.clear();
        noteMainHeaderHiddenData.clear();
        checkBoxList.clear();
        // 便签Header集合
        Iterator<String> tagDataIterator = tagDataSet.iterator();
        while (tagDataIterator.hasNext()) {
            String tagData = tagDataIterator.next();
            noteMainHeaderData.add(tagData);
            noteMainHeaderHiddenData.add(Constants.STR_NONE);
        }
        // 获取页面Header线性布局
		LinearLayout noteHeaderLinearLayout = (LinearLayout)findViewById(R.id.v_id_note_header_list);
        // 清空所有子布局
        noteHeaderLinearLayout.removeAllViews();
		// Header部子布局(线性、Java构成)
		LinearLayout noteHeaderSubLineLayout = new LinearLayout(this);
		// Header部子线性布局控件属性(线性、Java构成)
		LinearLayout.LayoutParams noteHeaderSubLineLayoutAttribute = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f);
        // 设置Header部数据
        if(!CollectionsUtil.isEmptyByCollection(noteMainHeaderData)) {
            int noteMainHeaderDataSize = noteMainHeaderData.size();
            CheckBox[] checkBoxs = new CheckBox[noteMainHeaderDataSize];
            Iterator<String> noteMainHeaderDataIterator = noteMainHeaderData.iterator();
            int position = 0;
            while(noteMainHeaderDataIterator.hasNext()) {
                // 设置Header部子线性布局控件横向属性(线性、Java构成)
                noteHeaderSubLineLayout.setOrientation(LinearLayout.HORIZONTAL);
                checkBoxs[position] = new CheckBox(this);
                checkBoxs[position].setText(noteMainHeaderDataIterator.next());
                checkBoxs[position].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<String> matchingCondition = new ArrayList<>();
                        boolean checkedFlag = false;
                        for(CheckBox val : checkBoxList) {
                            if(val.isChecked()) {
                                checkedFlag = true;
                                matchingCondition.add(StringUtil.isEmptyReturnString(val.getText()));
                            }
                        }

                        if (noteMainBodyDataBackUp.size() == 0) {
                            noteMainBodyDataBackUp = noteMainBodyData;
                        } else if (noteMainBodyDataBackUp.size() > noteMainBodyData.size()) {
                            noteMainBodyData = noteMainBodyDataBackUp;
                        }
                        List<NoteEntity> matchingResult = matchingResult(matchingCondition);
                        noteMainBodyData = matchingResult;
                        // 初始化便签No使用
                        Iterator<NoteEntity> entityIterator = noteMainBodyData.iterator();
                        int noCount = 1;
                        while (entityIterator.hasNext()) {
                            NoteEntity entity = entityIterator.next();
                            entity.setNoteNo(String.valueOf(noCount));
                            noCount++;
                        }
                        printNoteMainBodyPage();
                    }
                });
                checkBoxList.add(checkBoxs[position]);
                // 设置按钮形状
                noteHeaderSubLineLayout.addView(checkBoxs[position], noteHeaderSubLineLayoutAttribute);
                // 页面一行放置N个按钮
                if ((position + 1)%Constants.ROW_NUMBBERS == 0) {
                    noteHeaderLinearLayout.addView(noteHeaderSubLineLayout);
                    noteHeaderSubLineLayout = new LinearLayout(this);
                }else if (position == noteMainHeaderData.size() - 1) {
                    noteHeaderLinearLayout.addView(noteHeaderSubLineLayout);
                }
                position++;
            }
        }
    }

    private void printNoteMainBodyPage() {
        // 便签Body部List
        noteBodyListView = (ListView) findViewById(R.id.v_id_note_body_list);
        // 便签Header部List
        /**
         * 静态打印数据 SimpleAdapter simpleAdapter = new SimpleAdapter(context, data,
         * resource, from, to) context 上下文 this代替 data 打印数据List resource
         * 画面Layout的ListView的ID from 打印数据Bean中的数学 ID Name等 to 画面Layout中要放置Bean的数据的组件Id
         */
        // 自定义打印Body部数据
        noteBodyListView.setAdapter(new NoteMainBodyItem());
        // 自定义点击Body部数据事件
        noteBodyListView.setOnItemLongClickListener(this);
        noteBodyListView.setOnItemClickListener(this);
    }

    protected void onItemDoubleClick(AdapterView<?> parent, View view, int position, long id) {
        commonNoteEntity = (NoteEntity)noteBodyListView.getItemAtPosition(position);
        if (StringUtil.isEmptyReturnBoolean(commonNoteEntity.getNoteSubId())) {
            return;
        }
        Map<String, String> matchingCondition = new HashMap<>();
        /** (主)便签Id为父类ID */
        matchingCondition.put(Constants.NOTE_MATCH_CONDITION_PARENT_ID, commonNoteEntity.getNoteId());
        initNotePage(matchingCondition);
        setCurrentPageLevel(StringUtil.isEmptyReturnBigDecimal(commonNoteEntity.getNoteCurrentPageLevel()).add(new BigDecimal(1)).toString());
        enablePreviousOnClickListener(true);
    }



    /**
     * 筛选数据
     * @param matchingConditionList
     */
    private List<NoteEntity> matchingResult(List<String> matchingConditionList) {
        List<NoteEntity> matchingNoteMainBodyData = new ArrayList<NoteEntity>();
        if (matchingConditionList == null || matchingConditionList.isEmpty()) {
            return noteMainBodyData;
        }
        Iterator<String> matchingConditionIterator = matchingConditionList.iterator();
        while (matchingConditionIterator.hasNext()) {
            String matchingConditionStr = matchingConditionIterator.next();
            Iterator<String> headerDataIterator = noteMainHeaderData.iterator();
            int position = 0;
            while (headerDataIterator.hasNext()) {
                String headerDataStr = headerDataIterator.next();
                if(StringUtil.equaleReturnBoolean(matchingConditionStr, headerDataStr)) {
                    break;
                }
                position++;
            }
            String headerHidden = noteMainHeaderHiddenData.get(position);
            if (StringUtil.equaleReturnBoolean(Constants.STR_NONE, headerHidden)) {
                Iterator<NoteEntity> entityIterator = noteMainBodyData.iterator();
                while (entityIterator.hasNext()) {
                    NoteEntity entity = entityIterator.next();
                    if (StringUtil.equaleReturnBoolean(matchingConditionStr, entity.getNoteTag())) {
                        matchingNoteMainBodyData.add(entity);
                    }
                }
            }
        }
        return matchingNoteMainBodyData;
    }

    /**
     * 返回上一级功能是否启用
     * @param enableFlag
     */
    private void enablePreviousOnClickListener(boolean enableFlag){
        TextView currentPageLevelTv = (TextView) findViewById(R.id.v_id_note_main_current_page_level_tv);
        final String currentPageLevelStr = currentPageLevelTv.getText().toString();
        TextView previousTitleTv = (TextView)findViewById(R.id.v_id_note_main_previous_title_tv);
        TextView previousTv = (TextView)findViewById(R.id.v_id_note_main_previous_tv);
        if(enableFlag
                && !StringUtil.equaleReturnBoolean(String.valueOf(Constants.NOTE_CURRENT_PAGE_LEVEL_DEFAULT_VALUE), currentPageLevelStr)) {
            previousTitleTv.setVisibility(View.VISIBLE);
            previousTv.setVisibility(View.VISIBLE);
            // 添加下划线
            previousTitleTv.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
            // 计算返回的当前页面级别
            BigDecimal oldCurrentPageLevelBigDecimal = StringUtil.isEmptyReturnBigDecimal(currentPageLevelStr);
            BigDecimal newCurrentPageLevelBigDecimal = oldCurrentPageLevelBigDecimal.subtract(new BigDecimal(1));
            final String newCurrentPageLevel= newCurrentPageLevelBigDecimal.toString();
            previousTv.setText(newCurrentPageLevel);
            if (newCurrentPageLevelBigDecimal.compareTo(new BigDecimal(1)) >= 0) {
                previousTitleTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, String> matchingCondition = new HashMap<>();
                        matchingCondition.put(Constants.NOTE_MATCH_CONDITION_PAGE_LEVEL, newCurrentPageLevel);
                        initNotePage(matchingCondition);
                        setCurrentPageLevel(new BigDecimal(currentPageLevelStr).subtract(new BigDecimal(1)).toString());
                        enablePreviousOnClickListener(true);
                    }
                });
            } else {
                previousTitleTv.setVisibility(View.GONE);
                previousTv.setVisibility(View.GONE);
            }

        } else {
            previousTitleTv.setVisibility(View.GONE);
            previousTv.setVisibility(View.GONE);
        }
    }

    /**
     * 匹配数据(页面初始化时使用)
     * @param matchingCondition
     */
    private void matchingResult(Map<String,String> matchingCondition) {
        int noCount = 1;
        List<NoteEntity> newNoteMainBodyData = new ArrayList<NoteEntity>();
        Iterator<NoteEntity> noteMainBodyIterator = noteMainBodyData.iterator();
        while (noteMainBodyIterator.hasNext()){
            NoteEntity entity = noteMainBodyIterator.next();
            entity.setNoteNo(String.valueOf(noCount));
            if (!StringUtil.isEmptyReturnBoolean(matchingCondition.get(Constants.NOTE_MATCH_CONDITION_ID))
                    && StringUtil.equaleReturnBoolean(matchingCondition.get(Constants.NOTE_MATCH_CONDITION_ID), entity.getNoteId())) {
                newNoteMainBodyData.add(entity);
                noCount++;
            }
            if (!StringUtil.isEmptyReturnBoolean(matchingCondition.get(Constants.NOTE_MATCH_CONDITION_PARENT_ID))
                    && StringUtil.equaleReturnBoolean(matchingCondition.get(Constants.NOTE_MATCH_CONDITION_PARENT_ID), entity.getNoteParentId())) {
                newNoteMainBodyData.add(entity);
                noCount++;
            }
            if (!StringUtil.isEmptyReturnBoolean(matchingCondition.get(Constants.NOTE_MATCH_CONDITION_PAGE_LEVEL))
                    && StringUtil.equaleReturnBoolean(matchingCondition.get(Constants.NOTE_MATCH_CONDITION_PAGE_LEVEL), entity.getNoteCurrentPageLevel())) {
                newNoteMainBodyData.add(entity);
                noCount++;
            }

        }
        noteMainBodyData = newNoteMainBodyData;
        Iterator<NoteEntity> iterator = newNoteMainBodyData.iterator();
        tagDataSet.clear();
        while (iterator.hasNext()) {
            NoteEntity val = iterator.next();
            // 便签画面信息:Header检索部生成标签集合使用[Start]
            if (!StringUtil.isEmptyReturnBoolean(val.getNoteTag())) {
                tagDataSet.add(val.getNoteTag());
            }
            // 便签画面信息:Header检索部生成标签集合使用[End]
        }

    }
}
