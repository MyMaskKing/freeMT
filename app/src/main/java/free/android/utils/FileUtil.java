package free.android.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import free.android.common.ActivityCommon;
import free.android.enums.FormatEnum;

/**
 * 文件 帮助类
 * @author Administrator
 *
 */
public class FileUtil extends ActivityCommon{

	private final String LOG_FILE_NO_EXECUET = "文件不能执行";
	private final String LOG_FILE_NO_WRIRE = "文件不能写入";
	private final String LOG_FILE_NO_READ = "文件不能读取";
	private final static String FILE_UTIL_NAME = "Location:文件帮助类(FileUtil.class)";
	/**
	 *  创建文件
	 * @param filePath
	 * 			文件路径
	 * @param fileName
	 * 			文件名和类型后缀
	 */
	public static File createFile(String filePath, String fileName) {
		File file = new File(filePath, fileName);
		createFileDirectory(filePath);
		// 创建此抽象路径名指定的目录，包括所有必需但不存在的父目录
		try {
			// 创建文件Start
			if (!file.exists()) {
				file.createNewFile();
				LogUtil.i(FILE_UTIL_NAME, "创建文件成功||文件路径:" + file.getAbsolutePath());
			}
			// 设置其他用户对文件只有读取权限
			file.setReadOnly();
		} catch (Exception e) {
			LogUtil.i(FILE_UTIL_NAME, "创建文件失败||错误信息:" + e.getMessage());
		}
		return file;
	}

	/**
	 *  创建文件目录
	 * @param filePath
	 * 			文件目录路径
	 */
	private static void createFileDirectory(String filePath) {
		// 测试此抽象路径名表示的文件或目录是否存在
		File fileDirectory = new File(filePath);
		if (!fileDirectory.exists()) {
			fileDirectory.mkdirs();
		}
	}

	/**
	 * 文件追加数据
	 * @param filePath
	 *             追加文件路径
	 * @param fileName
	 *             追加文件名字
	 * @param listData
	 *             追加多个数据
	 * @param singleData
	 *             追加单个数据
	 */
	public static boolean write(String filePath, String fileName, List<Map<String,Object>> listData, Map<String, Object> singleData) {
        boolean result = true;
		File file = createFile(filePath, fileName);
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, true)));
			if (listData == null || listData.isEmpty()) {
				writeNote(bw, singleData);
			}else if (listData != null && !listData.isEmpty()) {
				Iterator<Map<String, Object>> valsIterator = listData.iterator();
				while (valsIterator.hasNext()) {
					Map<String, Object> val = valsIterator.next();
					writeNote(bw, val);
				}
			}

			bw.flush();
		} catch (Exception e) {
            result = false;
		}
		return result;
	}

    /**
     * 写入指定文件(便签功能专用)
     * @param bw
     * @param singleData
     * @throws Exception
     */
	private static void writeNote(BufferedWriter bw, Map<String, Object> singleData) throws Exception{
		// Master 1
		bw.write("#COMENT#:便签ID(*ID不重复,请勿更改)");
		bw.newLine();
		bw.write(Constants.NOTE_ID + Constants.EQUAL_SYMBOL +
				StringUtil.isEmptyReturnString((StringUtil.isEmptyReturnBoolean(String.valueOf(singleData.get(Constants.NOTE_ID)))
                        ?("NOTE" + getSystemTime(FormatEnum.TIME_FORMAT_ID.getVal())) : String.valueOf(singleData.get(Constants.NOTE_ID)))));
		bw.newLine();
		// Master 2
		bw.write("#COMENT#:(副)便签数量");
		bw.newLine();
		bw.write(Constants.NOTE_CHILDREN_COUNT + Constants.EQUAL_SYMBOL +
                (!StringUtil.isEmptyReturnBoolean(StringUtil.isEmptyReturnString(singleData.get(Constants.NOTE_CHILDREN_COUNT)))
                        ? StringUtil.isEmptyReturnString(singleData.get(Constants.NOTE_CHILDREN_COUNT)) : Constants.NOTE_CHILDREN_COUNT_DEFAULT_VALUE));
		bw.newLine();
		// Master 3
		bw.write("#COMENT#:便签父ID");
		bw.newLine();
		bw.write(Constants.NOTE_PARENT_ID + Constants.EQUAL_SYMBOL + StringUtil.isEmptyReturnString(singleData.get(Constants.NOTE_PARENT_ID)));
		bw.newLine();
		// Master 4
		bw.write("#COMENT#:便签内容");
		bw.newLine();
		bw.write(Constants.NOTE_CONTENT + Constants.EQUAL_SYMBOL + StringUtil.isEmptyReturnString(singleData.get(Constants.NOTE_CONTENT)));
		bw.newLine();
		// Master 5
		bw.write("#COMENT#:便签标签内容");
		bw.newLine();
		bw.write(Constants.NOTE_TAG + Constants.EQUAL_SYMBOL + StringUtil.isEmptyReturnString(singleData.get(Constants.NOTE_TAG)));
		bw.newLine();
		// Master 6
		bw.write("#COMENT#:便签录入时间");
		bw.newLine();
		bw.write(Constants.NOTE_INSERT_TIME + Constants.EQUAL_SYMBOL
				+ StringUtil.isEmptyReturnString((StringUtil.isEmptyReturnBoolean(String.valueOf(singleData.get(Constants.NOTE_INSERT_TIME))) ?
                getSystemTime(FormatEnum.TIME_FORMAT_V1.getVal()) : singleData.get(Constants.NOTE_INSERT_TIME))));
		bw.newLine();
		// Master 7
		bw.write("#COMENT#:便签更新时间");
		bw.newLine();
		bw.write(Constants.NOTE_UPDATE_TIME + StringUtil.isEmptyReturnString(Constants.EQUAL_SYMBOL + singleData.get(Constants.NOTE_UPDATE_TIME)));
		bw.newLine();
		// Master 8
		bw.write("#COMENT#:便签删除时间");
		bw.newLine();
		bw.write(Constants.NOTE_DELETE_TIME + Constants.EQUAL_SYMBOL + StringUtil.isEmptyReturnString(singleData.get(Constants.NOTE_DELETE_TIME)));
		bw.newLine();
        bw.write("#COMENT#:便签删除标识(*修改将会导致数据查询不正确,默认为0)");
        bw.newLine();
		// Master 9 ：数据删除标记
		bw.write(Constants.NOTE_DELETE_FLAG + Constants.EQUAL_SYMBOL + StringUtil.isEmptyReturnString(singleData.get(Constants.NOTE_DELETE_FLAG)));
		bw.newLine();
        bw.write("#COMENT#:便签更新次数标识(*修改将会导致数据查询不正确,默认为0)");
        bw.newLine();
		// Master 10 : 数据更新回数标记
		bw.write(Constants.NOTE_UPDATE_COUNT + Constants.EQUAL_SYMBOL + StringUtil.isEmptyReturnString(singleData.get(Constants.NOTE_UPDATE_COUNT)));
		bw.newLine();
		bw.write("#COMENT#:当前页面级别");
		bw.newLine();
		// Master 11 : 便签:当前页面级别
		bw.write(Constants.NOTE_CURRENT_PAGE_LEVEL + Constants.EQUAL_SYMBOL + StringUtil.isEmptyReturnString(singleData.get(Constants.NOTE_CURRENT_PAGE_LEVEL)));
		bw.newLine();
		bw.write("#COMENT#:副便签插入时间");
		bw.newLine();
		// Master 12 : 便签:副便签插入时间
		bw.write(Constants.SUB_NOTE_INSERT_TIME + Constants.EQUAL_SYMBOL + StringUtil.isEmptyReturnString(singleData.get(Constants.SUB_NOTE_INSERT_TIME)));
		bw.newLine();
	}

	/**
	 * check文件是否存在
	 * @param file
	 * @return
	 */
	public static boolean checkFileExist(File file) {
		return file.exists();
	}

	/**
	 * 复制文件From->To
	 * @param fromFilePath
	 * @param fromFileName
	 * @param toFilePath
	 * @param toFileName
	 * @return
	 */
	public static boolean copy(String fromFilePath, String fromFileName, String toFilePath, String toFileName) {
		File fromFile = new File(fromFilePath, fromFileName);
		if(!checkFileExist(fromFile)){
			createFile(fromFilePath, fromFileName);
		}
		File toFile = new File(toFilePath, toFileName);
		if(!checkFileExist(toFile)){
			createFile(toFilePath, toFileName);
		}
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(fromFile));
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(toFile));
			String readLine = StringUtil.EMPTY;
			while (!StringUtil.isEmptyReturnBoolean(readLine = bReader.readLine())){
				bWriter.write(readLine);
				bWriter.newLine();
			}
			bWriter.flush();
			bWriter.close();
			bReader.close();
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

}
