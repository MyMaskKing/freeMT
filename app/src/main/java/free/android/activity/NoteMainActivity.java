package free.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import free.android.R;
import free.android.common.ActivityCommon;
import free.android.entity.NoteMainEntity;
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

	private List<NoteMainEntity> noteMainBodyData = new ArrayList<NoteMainEntity>();
    private List<String> noteMainHeaderData = new ArrayList<String>();
    private List<String> noteMainHeaderHiddenData = new ArrayList<String>();
    // 便签画面Body部
    private ListView noteBodyListView;
    private Set<String> cityList = new HashSet<String>();
    private Set<String> typeList = new HashSet<String>();
    private Set<String> idOfDeleteData = new HashSet<String>();
    private Set<String> idOfUpdateData = new HashSet<String>();
    private List<CheckBox> checkBoxList = new ArrayList<CheckBox>();
    private List<String> checkBoxHidden = new ArrayList<String>();

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
		againSetNoteBodyData(noteMainBodyData);
        // 便签Body部List
		printNoteMainBodyPage();

		// 通过便签文件内容获取便签Header部数据
		setNoteMainHeaderData();

	}

    /**
     * 检索结果筛选
     * @param datas
     */
	private void againSetNoteBodyData(List<NoteMainEntity> datas) {
	    // 编号
        int noCount = 1;
	    // 新的便签Body部数据集合
        List<NoteMainEntity> againNoteBodyData = new ArrayList<NoteMainEntity>();
        // 获取更新次数最大的更新数据集合
        List<NoteMainEntity> mxaUpdateData = getLastNewUpdateData();
        Iterator<NoteMainEntity> valIterator = noteMainBodyData.iterator();
        while (valIterator.hasNext()) {
            NoteMainEntity val = valIterator.next();
            Iterator<String> delDataIterator = idOfDeleteData.iterator();
            String currentDataDelFlag = Constants.DELETE_OFF;
            while (delDataIterator.hasNext()) {
                String delDataId = delDataIterator.next();
                if (StringUtil.equaleReturnBoolean(delDataId, val.getNoteMasterId())) {
                    currentDataDelFlag = Constants.DELETE_ON;
                    break;
                }
            }
            // 已删除数据排除
            if (StringUtil.equaleReturnBoolean(Constants.DELETE_ON, currentDataDelFlag)) {
                continue;
            }
            // 非最大更新次数数据排除
            Iterator<NoteMainEntity> updDataIterator = mxaUpdateData.iterator();
            String lastNewUpdateFlag = StringUtil.EMPTY;
            while (updDataIterator.hasNext()) {
                NoteMainEntity updateData = updDataIterator.next();
                if (StringUtil.equaleReturnBoolean(val.getNoteMasterId(), updateData.getNoteMasterId())
                        && !StringUtil.equaleReturnBoolean(val.getNoteSubEntity().getNoteSubUpdateCount(), updateData.getNoteSubEntity().getNoteSubUpdateCount())) {
                    lastNewUpdateFlag = Constants.CANCEL_MARK;
                    break;
                }
            }
            // 非最大更新次数数据排除
            if (StringUtil.equaleReturnBoolean(Constants.CANCEL_MARK, lastNewUpdateFlag)) {
                continue;
            }
            val.setNoteMasterNo(String.valueOf(noCount));
            againNoteBodyData.add(val);
            // 统计便签城市使用
            cityList.add(val.getNoteSubEntity().getNoteSubCity());
            // 统计便签类型使用
            typeList.add(val.getNoteSubEntity().getNoteSubType());
            noCount++;
        }
        noteMainBodyData = againNoteBodyData;
    }

    /**
     * 获取最新的更新数据集
     */
    private List<NoteMainEntity> getLastNewUpdateData() {
        // 最终更新数据集合
        List<NoteMainEntity> updateEndData = new ArrayList<NoteMainEntity>();
        // 更新数据集合临时保存
        List<NoteMainEntity> updateData = new ArrayList<NoteMainEntity>();
        Iterator<String> updateValIterator = idOfUpdateData.iterator();
        // 更新数据的Id遍历
        while(updateValIterator.hasNext()) {
            updateData.clear();
            String updateVal = updateValIterator.next();
            Iterator<NoteMainEntity> noteMainEntityIterator = noteMainBodyData.iterator();
            while (noteMainEntityIterator.hasNext()) {
                if(StringUtil.equaleReturnBoolean(updateVal, noteMainEntityIterator.next().getNoteMasterId())) {
                    updateData.add(noteMainEntityIterator.next());
                };
            }
            NoteMainEntity maxUpdateCountData = getMaxUpdateCountData(updateData);
            updateEndData.add(maxUpdateCountData);
        }
        return updateEndData;
    }

    /**
     * 获取更新数最大的List
     * @param updateData
     */
    private NoteMainEntity getMaxUpdateCountData(List<NoteMainEntity> updateData) {
        NoteMainEntity entity = new NoteMainEntity();
        int updateCountCache = 0;
        Iterator<NoteMainEntity> entityIterator = updateData.iterator();
        while (entityIterator.hasNext()){
            if (updateCountCache < StringUtil.isEmptyReturnInteger(entityIterator.next().getNoteSubEntity().getNoteSubUpdateCount())) {
                updateCountCache = StringUtil.isEmptyReturnInteger(entityIterator.next().getNoteSubEntity().getNoteSubUpdateCount());
                entity = entityIterator.next();
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
			List<NoteMainEntity> noteList = new ArrayList<NoteMainEntity>();
			NoteMainEntity noteMainEntity = new NoteMainEntity();
			int repaetMark = 1;
			int count = 1;
			while((readLine = fr.readLine()) != null) {
			    if(count <= 12) {
                    setEntity(noteMainEntity, readLine, repeatSet, idRecod);
                    if(count == 12) {
                        count =  1;
                        noteList.add(noteMainEntity);
                        noteMainEntity = new NoteMainEntity();
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
	private void setEntity(NoteMainEntity entity, String readLine, Set<String> repeatSet, List<String> idRecod) {
        Set<String> citySet = new HashSet<String>();
        Set<String> typeSet = new HashSet<String>();
		if (readLine != null && !readLine.isEmpty()) {
			String[] split = readLine.split(Constants.EQUAL_SYMBOL);
			String entityAttribueName;
			String entityAttribueData;
			if (split.length > 0) {
				entityAttribueName = split[0];
				if (split.length > 1) {
					entityAttribueData = split[1];
					// 手动设置Entity的数据
					if(Constants.NOTE_MASTER_ID.equals(entityAttribueName.trim())) {
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
                        citySet.add(entityAttribueData);
						entity.getNoteSubEntity().setNoteSubCity(entityAttribueData);
					}else if (Constants.NOTE_SUB_TYPE.equals(entityAttribueName)) {
                        typeSet.add(entityAttribueData);
						entity.getNoteSubEntity().setNoteSubType(entityAttribueData);
					}else if (Constants.NOTE_SUB_REMARK.equals(entityAttribueName)) {
						entity.getNoteSubEntity().setNoteSubRemark(entityAttribueData);
					}else if (Constants.NOTE_SUB_DELETE_FLAG.equals(entityAttribueName)) {
                        entity.getNoteSubEntity().setNoteSubDeleteFlag(entityAttribueData);
                        if (Constants.DELETE_ON.equals(entityAttribueData)) {
                            idOfDeleteData.add(entity.getNoteMasterId());
                        }
                    }else if (Constants.NOTE_SUB_UPDATE_COUNT.equals(entityAttribueName)) {
						entity.getNoteSubEntity().setNoteSubUpdateCount(entityAttribueData);
                        if (!Constants.UPDATE_DEFAULT_COUNT.equals(entityAttribueData)) {
                            idOfUpdateData.add(entity.getNoteMasterId());
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_note_master_add:
			LogUtil.i(FILE_ACTIVITY_NAME, "迁移至便签按钮的添加便签画面");
			Intent noteSubIntent = new Intent(NoteMainActivity.this, NoteSubActivity.class);
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
                holder.idTv = (TextView) convertView.findViewById(R.id.v_id_note_main_id);
                holder.titleTv = (TextView) convertView.findViewById(R.id.v_id_note_main_title);
                holder.addressTv = (TextView) convertView.findViewById(R.id.v_id_note_main_address);
                holder.spendTimeTv = (TextView) convertView.findViewById(R.id.v_id_note_main_spend_time);
                holder.noBtn = (Button) convertView.findViewById(R.id.v_id_note_main_no);
                convertView.setTag(holder);
			} else {
                holder = (NoteMainBodyHolder) convertView.getTag();
			}
            holder.idTv.setText(StringUtil.isEmptyReturnString(noteMainBodyData.get(position).getNoteMasterId()));
            holder.titleTv.setText(StringUtil.isEmptyReturnString(noteMainBodyData.get(position).getNoteMasterTitle()));
			ComponentUtil.setMarquee(holder.titleTv);
            holder.addressTv.setText(StringUtil.isEmptyReturnString(noteMainBodyData.get(position).getNoteMasterAddress()));
			ComponentUtil.setMarquee(holder.addressTv);
            holder.spendTimeTv.setText(StringUtil.isEmptyReturnString(noteMainBodyData.get(position).getNoteMasterSpendTime()));
            holder.noBtn.setText(StringUtil.isEmptyReturnString(noteMainBodyData.get(position).getNoteMasterNo()));
            holder.noBtn.setClickable(false);
            holder.noBtn.setBackground(getResources().getDrawable(R.drawable.text_all_rounded));
			LogUtil.i(Constants.LOG_MES_TRANSITION_PAGE, "便签主画面");
			return convertView;
		}
	}

	/**
	 * List中每个项目的点击事件
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		NoteMainEntity entity = (NoteMainEntity)noteBodyListView.getItemAtPosition(position);
		Intent intent = new Intent(NoteMainActivity.this, NoteSubActivity.class);
		intent.putExtra(Constants.ACTION_FALG, "clickItem");
		intent.putExtra("noteEntity", entity);
		startActivity(intent);
		return true;
	}

    /**
     * 获取数据By便签Header部
     */
    private void setNoteMainHeaderData(){
        // 便签Header集合
        Iterator<String> cityIterator = cityList.iterator();
        while (cityIterator.hasNext()) {
            String cityVal = cityIterator.next();
            noteMainHeaderData.add(cityVal);
            noteMainHeaderHiddenData.add("0");
        }
        Iterator<String> typeIterator = typeList.iterator();
        while (typeIterator.hasNext()) {
            String typeVal = typeIterator.next();
            noteMainHeaderData.add(typeVal);
            noteMainHeaderHiddenData.add("1");
        }
        // 获取页面Header线性布局
		LinearLayout noteHeaderLinearLayout = (LinearLayout)findViewById(R.id.v_id_note_header_list);
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
                        List<NoteMainEntity> matchingData = new ArrayList<NoteMainEntity>();
                        boolean matchingFlg = false;
                        int checkBoxPosition = 0;
                        for(CheckBox val : checkBoxList) {
                            if(val.isChecked()) {
                                matchingFlg = true;
                                matchingResult(noteMainHeaderHiddenData.get(checkBoxPosition), val.getText().toString(), matchingData);
                            }
                            checkBoxPosition++;
                        }
                        if (matchingFlg) {
                            noteMainBodyData = matchingData;
                            printNoteMainBodyPage();
                        } else {
                            int noCheckedBoxPosition = 0;
                            boolean noCheckedFlag = true;
                            for(CheckBox val : checkBoxList) {
                                if (!StringUtil.isEmptyReturnBoolean(val.getText().toString())) {
                                    noCheckedFlag = false;
                                    matchingResult(noteMainHeaderHiddenData.get(noCheckedBoxPosition), val.getText().toString(), matchingData);
                                }
                                noCheckedBoxPosition++;
                            }
                            if (!noCheckedFlag) {
                                matchingResult(Constants.STR_ALL, StringUtil.EMPTY, matchingData);
                            } else {
                                noteMainBodyData = matchingData;
                            }
                            printNoteMainBodyPage();
                        }
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
    }

    /**
     * 筛选数据
     * @param mark
     * @param followVal
     */
    private void matchingResult(String mark, String followVal, List<NoteMainEntity> matchingData) {
        // 获取最新数据
        getNoteFileContent();
        againSetNoteBodyData(noteMainBodyData);
        if (!StringUtil.equaleReturnBoolean(Constants.STR_ALL, mark)) {
            Iterator<NoteMainEntity> valIterator = noteMainBodyData.iterator();
            while (valIterator.hasNext()) {
                NoteMainEntity val = valIterator.next();
                if (StringUtil.equaleReturnBoolean(mark, "0")
                        && StringUtil.equaleReturnBoolean(val.getNoteSubEntity().getNoteSubCity(), followVal)) {
                    val.setNoteMasterNo(String.valueOf(matchingData.size() + 1));
                    matchingData.add(val);
                } else if (StringUtil.equaleReturnBoolean(mark, "1")
                        && StringUtil.equaleReturnBoolean(val.getNoteSubEntity().getNoteSubType(), followVal)) {
                    val.setNoteMasterNo(String.valueOf(matchingData.size() + 1));
                    matchingData.add(val);
                }
            }
        }
    }

}
