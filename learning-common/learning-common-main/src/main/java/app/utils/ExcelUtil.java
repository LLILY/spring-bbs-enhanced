package app.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Created by lili19289 on 2016/8/31.
 */
public class ExcelUtil {

    public static void main(String[] args) throws Exception {
        File file = new File(".xls");
        String[][] result = getData(file, 0).get(0);
        int rowLength = result.length;
        for (int i = 0; i < rowLength; i++) {
            for (int j = 0; j < result[i].length; j++) {
                System.out.print(result[i][j] + "\t");
            }
            System.out.println();
        }
    }

    /**
     *
     * 读取Excel的内容，第一维数组存储的是一行中格列的值，二维数组存储的是多少个行
     *
     * @param file
     *            读取数据的源Excel
     *
     * @param ignoreRows
     *            读取数据忽略的行数，比喻行头不需要读入 忽略的行数为1
     *
     * @return 读出的Excel中数据的内容
     *
     * @throws FileNotFoundException
     *
     * @throws IOException
     */

    public static List<String[][]> getData(File file, int ignoreRows)

            throws FileNotFoundException, IOException {
        List<String[][]> result = new ArrayList<String[][]>();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                file));
        // 打开HSSFWorkbook
        POIFSFileSystem fs = new POIFSFileSystem(in);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFCell cell = null;
        for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
            HSSFSheet st = wb.getSheetAt(sheetIndex);
            List<String[]> sheetResult = new ArrayList<String[]>();
            int rowSize = 0;
            for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum(); rowIndex++) {
                HSSFRow row = st.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                int tempRowSize = row.getLastCellNum();
                if (tempRowSize > rowSize) {
                    rowSize = tempRowSize;
                }
                String[] values = new String[rowSize];
                Arrays.fill(values, "");
                boolean hasValue = false;
                for (int columnIndex = 0; columnIndex < row.getLastCellNum(); columnIndex++) {
                    String value = "";
                    cell = row.getCell(columnIndex);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case HSSFCell.CELL_TYPE_STRING:
                                value = cell.getStringCellValue();
                                break;
                            case HSSFCell.CELL_TYPE_NUMERIC:
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    Date date = cell.getDateCellValue();
                                    if (date != null) {
                                        value = new SimpleDateFormat("yyyy-MM-dd")
                                                .format(date);
                                    } else {
                                        value = "";
                                    }
                                } else {
                                    String cellValue = cell.getNumericCellValue()
                                            + "";
                                    if (cellValue.contains(".0")) {
                                        value = new DecimalFormat("#").format(cell
                                                .getNumericCellValue());
                                    } else {
                                        value = new DecimalFormat("#").format(cell
                                                .getNumericCellValue()) + "";
                                    }

                                }
                                break;
                            case HSSFCell.CELL_TYPE_FORMULA:
                                // 导入时如果为公式生成的数据则无值
                                if (!cell.getStringCellValue().equals("")) {
                                    value = cell.getStringCellValue();
                                } else {
                                    value = cell.getNumericCellValue() + "";
                                }
                                break;
                            case HSSFCell.CELL_TYPE_BLANK:
                                break;
                            case HSSFCell.CELL_TYPE_ERROR:
                                value = "";
                                break;
                            case HSSFCell.CELL_TYPE_BOOLEAN:
                                value = (cell.getBooleanCellValue() == true ? "Y"
                                        : "N");
                                break;
                            default:
                                value = "";
                        }
                    }
                    if (columnIndex == 0 && value.trim().equals("")) {
                        break;
                    }
                    values[columnIndex] = value.replace(" ", "");
                    hasValue = true;
                }
                if (hasValue) {
                    sheetResult.add(values);
                }
            }
            String[][] returnArray = new String[sheetResult.size()][rowSize];
            for (int i = 0; i < returnArray.length; i++) {
                returnArray[i] = (String[]) sheetResult.get(i);
            }
            result.add(returnArray);
        }
        in.close();
        return result;
    }

    public static void export(String[][] data, File file) throws IOException {
        // 创建工作薄
        HSSFWorkbook wb = new HSSFWorkbook();
        // 创建SHEET
        HSSFSheet sheet = wb.createSheet("data");
        // 画数据表行列
        for (int i = 0; i < data.length; i++) {
            HSSFRow row = sheet.createRow(i);
            HSSFCell[] cell = new HSSFCell[data[i].length];
            for (int j = 0; j < cell.length; j++) {
                cell[j] = row.createCell(j);
                cell[j].setCellValue(data[i][j]);
            }
        }
        // 自动调整列宽
        for (int i = 0; i < data.length; i++) {
            sheet.autoSizeColumn(i);
        }
        FileOutputStream fos = new FileOutputStream(file);
        wb.write(fos);
        fos.flush();
        fos.close();
    }

    /**
     *
     * 去掉字符串右边的空格
     *
     * @param str
     *            要处理的字符串
     *
     * @return 处理后的字符串
     */

    public static String rightTrim(String str) {

        if (str == null) {

            return "";

        }

        int length = str.length();

        for (int i = length - 1; i >= 0; i--) {

            if (str.charAt(i) != 0x20) {

                break;

            }

            length--;

        }

        return str.substring(0, length);

    }
}
