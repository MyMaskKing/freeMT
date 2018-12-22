package free.android.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import free.android.common.ActivityCommon;

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
	public static void write(String filePath, String fileName, List<Map<String,Object>> listData, Map<String, Object> singleData) {
		File file = createFile(filePath, fileName);
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, true)));
			if (listData == null || listData.isEmpty()) {
				// Master 1
				bw.write(Constants.NOTE_MASTER_ID + Constants.EQUAL_SYMBOL + getIdByTime());
				bw.newLine();
				// Master 2
				bw.write(Constants.NOTE_MASTER_TITLE + Constants.EQUAL_SYMBOL + singleData.get(Constants.NOTE_MASTER_TITLE));
				bw.newLine();
				// Master 3
				bw.write(Constants.NOTE_MASTER_SPEND_TIME + Constants.EQUAL_SYMBOL + singleData.get(Constants.NOTE_MASTER_SPEND_TIME));
				bw.newLine();
				// Master 4
				bw.write(Constants.NOTE_MASTER_ADDRESS + Constants.EQUAL_SYMBOL + singleData.get(Constants.NOTE_MASTER_ADDRESS));
				bw.newLine();
				// Sub 1
				bw.write(Constants.NOTE_SUB_ITEM + Constants.EQUAL_SYMBOL + singleData.get(Constants.NOTE_SUB_ITEM));
				bw.newLine();
				// Sub 2
				bw.write(Constants.NOTE_SUB_OVER_FLAG + Constants.EQUAL_SYMBOL + singleData.get(Constants.NOTE_SUB_OVER_FLAG));
				bw.newLine();
				// Sub 3
				bw.write(Constants.NOTE_SUB_APPRAISAL + Constants.EQUAL_SYMBOL + singleData.get(Constants.NOTE_SUB_APPRAISAL));
				bw.newLine();
				bw.write(Constants.NOTE_SUB_TYPE + Constants.EQUAL_SYMBOL + singleData.get(Constants.NOTE_SUB_TYPE));
				bw.newLine();
				bw.write(Constants.NOTE_SUB_CITY + Constants.EQUAL_SYMBOL + singleData.get(Constants.NOTE_SUB_CITY));
				bw.newLine();
			}

			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * check文件是否存在
	 * @param file
	 * @return
	 */
	public static boolean checkFileExist(File file) {
		return file.exists();
	}
}
