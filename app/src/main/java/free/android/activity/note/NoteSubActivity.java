package free.android.activity.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import free.android.R;
import free.android.common.BasicActivity;
import free.android.entity.NoteEntity;
import free.android.enums.FormatEnum;
import free.android.enums.PageInfoEnum;
import free.android.utils.ComponentUtil;
import free.android.utils.Constants;
import free.android.utils.FileUtil;
import free.android.utils.StringUtil;

/**
 * 便签子画面
 *
 * @author dapao
 */
public class NoteSubActivity extends BasicActivity {
    private final String FILE_ACTIVITY_NAME = "便签子画面(NoteSubActivity)";

    /**
     * 初始化方法
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 无标题栏(系统自带不删除)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏效果
		/*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        //隐藏状态栏
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //显示状态栏
        // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        String currentMode = fromNoteMainActivityMode();
        setContentView(R.layout.note_sub);
        if (fromNoteMainActivityFlag()) {
            NoteEntity noteEntity = (NoteEntity) getIntent().getSerializableExtra("noteEntity");

            /** [1](From主)便签:便签内容 */
            TextView contentByNoteMainTv = findViewById(R.id.v_id_note_sub_content_by_main);
            contentByNoteMainTv.setText(noteEntity.getNoteContent());
            ComponentUtil.setMarquee(contentByNoteMainTv);
            /** [2](From主)便签:当前页面级别(Hidden) */
            TextView currentPageLevelHidden = findViewById(R.id.v_id_note_sub_current_page_level);
            currentPageLevelHidden.setText(noteEntity.getNoteCurrentPageLevel());
            /** [3](From主)便签:数据父类ID(Hidden) */
            TextView idParentHidden = findViewById(R.id.v_id_note_sub_parent_id);
            idParentHidden.setText(noteEntity.getNoteParentId());
            // 便签修改/便签查询
            if (StringUtil.equaleReturnBoolean(Constants.STR_MODIFY, currentMode)
                    || StringUtil.equaleReturnBoolean(Constants.STR_LOOK_UP, currentMode)) {
                /** [4](From主)便签:副便签数据数量(Hidden) */
                TextView childrenCountHidden = findViewById(R.id.v_id_note_sub_children_count);
                childrenCountHidden.setText(noteEntity.getNoteChildrenCount());
                /** [5](From主)便签:便签内容 */
                EditText contentEditext = findViewById(R.id.v_id_note_sub_content_editext);
                contentEditext.setText(noteEntity.getNoteContent());
                ComponentUtil.setEditextDisable(contentEditext);
                /** [6](From主)便签:标签内容 */
                EditText tagEditext = findViewById(R.id.v_id_note_sub_tag_editext);
                tagEditext.setText(noteEntity.getNoteTag());
                ComponentUtil.setEditextDisable(tagEditext);
                /** [7](From主)便签:录入时间 */
                TextView itemEditext = findViewById(R.id.v_id_note_sub_insert_time_editext);
                itemEditext.setText(!StringUtil.isEmptyReturnBoolean(noteEntity.getNoteUpdateTime()) ? noteEntity.getNoteUpdateTime() : noteEntity.getNoteInsertTime());
                /** [8](From主)便签:更新次数(Hidden) */
                TextView updateCountHidden = findViewById(R.id.v_id_note_sub_update_count);
                updateCountHidden.setText(noteEntity.getNoteUpdateCount());
                /** [9](From主)便签:数据ID */
                TextView idHidden = findViewById(R.id.v_id_note_sub_id);
                idHidden.setText(noteEntity.getNoteId());
                /** [10](From主)便签:更新次数(Hidden) */
                TextView subNoteInsertTimeHidden = findViewById(R.id.v_id_note_sub_subnote_insert_time);
                subNoteInsertTimeHidden.setText(noteEntity.getSubNoteInsertTime());
                Button addBtn = findViewById(R.id.v_id__note_sub_add_button);
                addBtn.setVisibility(View.GONE);
                LinearLayout currentNotell = findViewById(R.id.v_id_note_sub_current_note_ll);
                currentNotell.setVisibility(View.GONE);
                if (StringUtil.equaleReturnBoolean(Constants.STR_MODIFY, currentMode)) {
                    modifyMode();
                }
            } else {
                /** 便签标题:便签内容 */
                TextView noteSubContentTv = findViewById(R.id.v_id_note_sub_content);
                noteSubContentTv.setText("(副)" + getResources().getString(R.string.note_content));
                /** 便签标题:标签内容 */
                TextView noteSubTagTv = findViewById(R.id.v_id_note_sub_tag);
                noteSubTagTv.setText("(副)" + getResources().getString(R.string.note_tag));
                /** 便签标题:录入时间(Hidden) */
                TextView insertTimeTv = findViewById(R.id.v_id_note_sub_insert_time);
                insertTimeTv.setVisibility(View.GONE);
            }
        } else {
            Intent intent = getIntent();
            // 不同功能判断标识
            String currentPageLevel = intent.getStringExtra(Constants.NOTE_MATCH_CONDITION_PAGE_LEVEL);
            TextView currentPageLevelHidden = findViewById(R.id.v_id_note_sub_current_page_level);
            currentPageLevelHidden.setText(currentPageLevel);
            LinearLayout idLL = findViewById(R.id.v_id_note_sub_current_note_ll);
            idLL.setVisibility(View.GONE);
            TextView currentModeTitleTv = findViewById(R.id.v_id_note_sub_current_mode_title_tv);
            currentModeTitleTv.setVisibility(View.GONE);
            /** 便签标题:录入时间(Hidden) */
            TextView insertTimeTv = findViewById(R.id.v_id_note_sub_insert_time);
            insertTimeTv.setVisibility(View.GONE);
        }
        /** 便签:当前模式 */
        TextView currentModeTv = findViewById(R.id.v_id_note_sub_current_mode_tv);
        currentModeTv.setText(StringUtil.isEmptyReturnString(currentMode));
        Button menuBtn = findViewById(R.id.v_id__note_sub_menu);
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
     * 判断是否由便签主画面迁移
     *
     * @return
     */
    private boolean fromNoteMainActivityFlag() {
        boolean result = false;
        Intent intent = getIntent();
        // 不同功能判断标识
        String actionFlag = intent.getStringExtra(Constants.ACTION_FALG);
        // 便签主画面的每个项目点击事件
        if (StringUtil.equaleReturnBoolean("clickItem", actionFlag)) {
            result = true;
        }
        return result;
    }

    /**
     * 判断是否由便签主画面迁移模式
     *
     * @return
     */
    private String fromNoteMainActivityMode() {
        Intent intent = getIntent();
        // 不同功能判断标识
        return intent.getStringExtra(Constants.ACTION_MODE);
    }

    /**
     * 便签子画面Menu部
     *
     * @param menu
     * @return
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // 选择菜单 一样 进行打气使用
        getMenuInflater().inflate(R.menu.note_sub_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
        if (!fromNoteMainActivityFlag()) {
            // 新规场合禁止修改删除
            menu.findItem(R.id.menu_note_sub_modify).setVisible(false);
        }
    }

    /**
     * 菜单部的按钮监听
     *
     * @param item
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            // 便签子画面的修改按钮
            case R.id.menu_note_sub_modify:
                modifyMode();
                return true;
            // 便签子画面的返回上一级
            case R.id.menu_note_sub_previous:
                returnNoteMainActivity();
                return true;
            // 便签子画面的返回首页
            case R.id.menu_note_sub_return_index:
                commonReturnIndex();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void modifyMode() {
        /** 便签:便签内容部 */
        EditText contentEditext = findViewById(R.id.v_id_note_sub_content_editext);
        ComponentUtil.setEditextEnable(contentEditext);
        /** 便签:标签部 */
        EditText tagEditext = findViewById(R.id.v_id_note_sub_tag_editext);
        ComponentUtil.setEditextEnable(tagEditext);
        Button addBtn = findViewById(R.id.v_id__note_sub_add_button);
        addBtn.setVisibility(View.VISIBLE);
        addBtn.setText(getResources().getString(R.string.modify_CN_mes));
        TextView currentModeTv = findViewById(R.id.v_id_note_sub_current_mode_tv);
        currentModeTv.setText(Constants.STR_MODIFY);
    }

    /**
     * 返回(主)便签画面
     */
    private void returnNoteMainActivity() {
        Intent intent = new Intent(NoteSubActivity.this, NoteMainActivity.class);
        TextView currentPageLevelHidden = findViewById(R.id.v_id_note_sub_current_page_level);
        TextView parentIdHidden = findViewById(R.id.v_id_note_sub_parent_id);
        String currentPageLevelStr = currentPageLevelHidden.getText().toString();
        String parentIdStr = parentIdHidden.getText().toString();
        intent.putExtra(Constants.ACTION_FALG, PageInfoEnum.NOTE_SUB_PAGE.getKey());
        intent.putExtra(Constants.NOTE_PARENT_ID, parentIdStr);
        intent.putExtra(Constants.NOTE_CURRENT_PAGE_LEVEL, currentPageLevelStr);
        startActivity(intent);
    }

    /**
     * 对话框Template 1.0的确认按钮执行内容
     *
     * @return
     */
    protected void clickConfirmBthByDialog() {
        Map<String, Object> addContent = commonSetWriteContent();
        // 获取删除Id
        TextView idHidden = findViewById(R.id.v_id_note_sub_id);
        String id = idHidden.getText().toString();
        addContent.put(Constants.NOTE_ID, id);
        // 删除标记ON
        addContent.put(Constants.NOTE_SUB_DELETE_FLAG, Constants.DELETE_ON);
        commonTransitionPage(addContent);
    }

    /**
     * 执行添加便签内容操作
     */
    public void executeAddBtn(View view) {
        Map<String, Object> submitContent = commonSetWriteContent();
        List<String> submitContentList = new ArrayList<>();
        List<String> checkErrorList = new ArrayList<>();
        /** 便签确认:便签内容 */
        if (!StringUtil.isEmptyReturnBoolean(submitContent.get(Constants.NOTE_CONTENT).toString())) {
            submitContentList.add(Constants.NM_NOTE_CONTENT + Constants.COLON_SYMBOL + submitContent.get(Constants.NOTE_CONTENT).toString());
        } else {
            checkErrorList.add(Constants.NM_NOTE_CONTENT + Constants.COLON_SYMBOL + "请必须输入此项目");
        }
        /** 便签确认:标签内容 */
        if (!StringUtil.isEmptyReturnBoolean(submitContent.get(Constants.NOTE_TAG).toString())) {
            submitContentList.add(Constants.NM_NOTE_TAG + Constants.COLON_SYMBOL + submitContent.get(Constants.NOTE_TAG).toString());
        }
        if (!checkErrorList.isEmpty()) {
            setError();
            showDialogV1_1(checkErrorList, Constants.ERROR_MARK, "错误", "订正", "取消");
        } else {
            TextView currentModeTv = findViewById(R.id.v_id_note_sub_current_mode_tv);
            String currentModeStr = currentModeTv.getText().toString();
            if (StringUtil.isEmptyReturnBoolean(currentModeStr)) {
                currentModeStr = Constants.STR_ADD;
            }
            showDialogV1_1(submitContentList, Constants.CONFIRM_MARK, currentModeStr + "内容确认", "确认", "取消");
        }
    }

    /**
     * <PRE>
     * 对话框Template 1.1(按钮Btn1的点击事件)
     * 使用页面(common_dialog_v1.xml)
     * <PRE/>
     */
    protected void onClickBtn1V1_1() {
        Map<String, Object> addContent = commonSetWriteContent();
        // 判断页面Check结果
        if (isError()) {
            onClickBtn1V1_2();
            /** 执行(主)便签添加操作 */
        } else if (!isError() && !fromNoteMainActivityFlag() && StringUtil.isEmptyReturnBoolean(fromNoteMainActivityMode())) {
            commonSetWriteContent();
            // 添加默认页面级别
            addContent.put(Constants.NOTE_CURRENT_PAGE_LEVEL, Constants.NOTE_CURRENT_PAGE_LEVEL_DEFAULT_VALUE);
            // 添加默认更新次数
            addContent.put(Constants.NOTE_UPDATE_COUNT, Constants.UPDATE_DEFAULT_COUNT);
            commonTransitionPage(addContent);
        } else {
            /** 执行(副)便签添加操作 */
            commonSetWriteContent();
            // 操作:赋予主便签子ID
            if (fromNoteMainActivityFlag() && StringUtil.equaleReturnBoolean(Constants.STR_ADD, fromNoteMainActivityMode())) {
                List<Map<String, Object>> listWriteData = new ArrayList<>();
                NoteEntity noteEntity = (NoteEntity) getIntent().getSerializableExtra("noteEntity");
                // (副)便签ID
                String subId = "NOTE" + getSystemTime(FormatEnum.TIME_FORMAT_ID.getVal());
                addContent.put(Constants.NOTE_ID, subId);
                // (副)便签:主便签ID
                addContent.put(Constants.NOTE_PARENT_ID, noteEntity.getNoteId());
                // (副)便签页面级别
                BigDecimal subCurrentPageLevel = StringUtil.isEmptyReturnBigDecimal(noteEntity.getNoteCurrentPageLevel()).add(new BigDecimal(1));
                addContent.put(Constants.NOTE_CURRENT_PAGE_LEVEL, subCurrentPageLevel.toString());
                listWriteData.add(addContent);
                /** ---- (主)便签 ---- */
                Map<String, Object> mainNoteData = commonSetWriteContent();
                // ([1]主)便签:便签Id
                mainNoteData.put(Constants.NOTE_ID, noteEntity.getNoteId());
                // ([2]主)便签:(副)便签数量
                mainNoteData.put(Constants.NOTE_CHILDREN_COUNT, StringUtil.isEmptyReturnBigDecimal(noteEntity.getNoteChildrenCount()).add(new BigDecimal(1)));
                // ([3]主)便签:便签内容
                mainNoteData.put(Constants.NOTE_CONTENT, noteEntity.getNoteContent());
                // ([4]主)便签:标签内容
                mainNoteData.put(Constants.NOTE_TAG, noteEntity.getNoteTag());
                // ([5]主)便签:录入时间
                mainNoteData.put(Constants.NOTE_INSERT_TIME, noteEntity.getNoteInsertTime());
                // ([6]主)便签:副便签录入时间
                mainNoteData.put(Constants.SUB_NOTE_INSERT_TIME, getSystemTime(FormatEnum.TIME_FORMAT_V1.getVal()));
                // ([7]主)便签:更新时间
                mainNoteData.put(Constants.NOTE_UPDATE_TIME, noteEntity.getNoteUpdateTime());
                // ([8]主)便签:删除时间
                mainNoteData.put(Constants.NOTE_DELETE_TIME, noteEntity.getNoteDeleteTime());
                // ([9]主)便签:更新次数
                BigDecimal updateCount = StringUtil.isEmptyReturnBigDecimal(noteEntity.getNoteUpdateCount());
                updateCount = updateCount.add(new BigDecimal(1));
                mainNoteData.put(Constants.NOTE_UPDATE_COUNT, updateCount.toString());
                // ([10]主)便签:删除Flag
                mainNoteData.put(Constants.NOTE_DELETE_FLAG, noteEntity.getNoteDeleteFlag());
                // ([11]主)便签:当前页面级别
                mainNoteData.put(Constants.NOTE_CURRENT_PAGE_LEVEL, noteEntity.getNoteCurrentPageLevel());
                // ([12]主)便签:父类便签ID
                mainNoteData.put(Constants.NOTE_PARENT_ID, noteEntity.getNoteParentId());
                listWriteData.add(mainNoteData);
                String externalFilesPath = getFilePathByApp();
                FileUtil.write(externalFilesPath, Constants.NOTE_FILE_NAME, listWriteData, null);
                returnNoteMainActivity();
                // 执行修改操作
            } else {
                TextView updateCountStr = findViewById(R.id.v_id_note_sub_update_count);
                BigDecimal updateCount = StringUtil.isEmptyReturnBigDecimal(String.valueOf(updateCountStr.getText()));
                updateCount = updateCount.add(new BigDecimal(1));
                // 更新标记
                addContent.put(Constants.NOTE_UPDATE_COUNT, updateCount.toString());
                // 更新时间
                addContent.put(Constants.NOTE_UPDATE_TIME, getSystemTime(FormatEnum.TIME_FORMAT_V1.getVal()));
                commonTransitionPage(addContent);
            }
        }
    }

    /**
     * 共通:获取页面内容并放入Map中
     *
     * @return
     */
    private Map<String, Object> commonSetWriteContent() {
        // 放置添加的内容
        Map<String, Object> addContent = new HashMap<String, Object>();
        // [1]便签:便签Id
        TextView idHidden = findViewById(R.id.v_id_note_sub_id);
        String id = idHidden.getText().toString();
        addContent.put(Constants.NOTE_ID, id);
        // [2]便签:(副)便签数量
        TextView childrenCountHidden = findViewById(R.id.v_id_note_sub_children_count);
        String childrenId = childrenCountHidden.getText().toString();
        addContent.put(Constants.NOTE_CHILDREN_COUNT, childrenId);
        // [3]便签:便签内容
        EditText contentEditext = findViewById(R.id.v_id_note_sub_content_editext);
        String contentStr = contentEditext.getText().toString();
        addContent.put(Constants.NOTE_CONTENT, contentStr);
        // [4]便签:标签内容
        EditText tagEditext = findViewById(R.id.v_id_note_sub_tag_editext);
        String tagStr = tagEditext.getText().toString();
        addContent.put(Constants.NOTE_TAG, tagStr);
        // [5]便签:更新时间
        TextView updateTimeHidden = findViewById(R.id.v_id_note_sub_update_time);
        String updateTime = updateTimeHidden.getText().toString();
        addContent.put(Constants.NOTE_UPDATE_TIME, updateTime);
        // [6]便签:更新次数
        TextView updateCountHidden = findViewById(R.id.v_id_note_sub_update_count);
        String updateCount = updateCountHidden.getText().toString();
        addContent.put(Constants.NOTE_UPDATE_COUNT, updateCount);
        // [7]删除标记
        addContent.put(Constants.NOTE_DELETE_FLAG, Constants.DELETE_OFF);
        // [8]便签:当前页面级别
        TextView currentPageLevelHidden = findViewById(R.id.v_id_note_sub_current_page_level);
        String currentPageLevelSrt = currentPageLevelHidden.getText().toString();
        addContent.put(Constants.NOTE_CURRENT_PAGE_LEVEL, currentPageLevelSrt);
        // [9]便签:副便签插入时间
        TextView subNoteInsertTimeHidden = findViewById(R.id.v_id_note_sub_subnote_insert_time);
        String subNoteInsertTimeSrt = subNoteInsertTimeHidden.getText().toString();
        addContent.put(Constants.SUB_NOTE_INSERT_TIME, subNoteInsertTimeSrt);
        // [10]删除时间
        addContent.put(Constants.NOTE_DELETE_TIME, StringUtil.EMPTY);
        // [11]便签:父类便签ID
        TextView noteParentIdHidden = findViewById(R.id.v_id_note_sub_parent_id);
        String noteParentIdStr = noteParentIdHidden.getText().toString();
        addContent.put(Constants.NOTE_PARENT_ID, noteParentIdStr);

        return addContent;
    }

    /**
     * 共通:写入新的数据并跳转画面
     *
     * @param addContent 向Text中放入的数据
     */
    private void commonTransitionPage(Map<String, Object> addContent) {
        String externalFilesPath = getFilePathByApp();
        FileUtil.write(externalFilesPath, Constants.NOTE_FILE_NAME, null, addContent);
        returnNoteMainActivity();
    }

    /**
     * 重新系统自带返回键
     */
    @Override
    public void onBackPressed() {
        returnNoteMainActivity();
    }

}
