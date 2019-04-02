package com.github.easyExcel;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Excel相关处理
 *
 */
public class EasyExcelUtil<T>
{
    private static final Logger log = LoggerFactory.getLogger(EasyExcelUtil.class);

    /**
     * Excel sheet最大行数，默认65536
     */
    public static final int sheetSize = 65536;

    /**
     * 工作表名称
     */
    private String sheetName;

    /**
     * 工作薄对象
     */
    private Workbook wb;

    /**
     * 工作表对象
     */
    private Sheet sheet;

    /**
     * 导入导出数据列表
     */
    private List<T> list;

    /**
     * 注解列表
     */
    private List<Field> fields;

    /**
     * 实体对象
     */
    public Class<T> clazz;

    public static String DATA = "date";
    public static String ERROE_INF = "errorInf";

    public EasyExcelUtil(Class<T> clazz)
    {
        this.clazz = clazz;
    }

    public void init(List<T> list, String sheetName)
    {
        if (list == null)
        {
            list = new ArrayList<T>();
        }
        this.list = list;
        this.sheetName = sheetName;
        createExcelField();
        createWorkbook();
    }

    /**
     * 对excel表单默认第一个索引名转换成list
     *
     * @param is 输入流
     * @return 转换后集合
     */
    public Map<String,Object> importExcel(InputStream is) throws Exception
    {
        return importExcel("", is);
    }

    /**
     * 对excel表单指定表格索引名转换成list
     *
     * @param sheetName 表格索引名
     * @param is 输入流
     * @return 转换后集合
     */
    public Map<String,Object> importExcel(String sheetName, InputStream is) throws Exception
    {
        this.wb = WorkbookFactory.create(is);
        List<T> list = new ArrayList<T>();
        Map<String,Object> resultMap = new HashMap<>();
        List<ErrorInf> errorInfList = new ArrayList<>();
        Sheet sheet = null;
        if (!sheetName.isEmpty())
        {
            // 如果指定sheet名,则取指定sheet中的内容.
            sheet = wb.getSheet(sheetName);
        }
        else
        {
            // 如果传入的sheet名不存在则默认指向第1个sheet.
            sheet = wb.getSheetAt(0);
        }

        if (sheet == null)
        {
            throw new IOException("文件sheet不存在");
        }

        int rows = sheet.getPhysicalNumberOfRows();
        if (rows > 0)
        {
            // 默认序号
            int serialNum = 0;
            // 有数据时才处理 得到类的所有field.
            Field[] allFields = clazz.getDeclaredFields();
            // 定义一个map用于存放列的序号和field.
            Map<String, Field> fieldsMap = new HashMap<>();
            for (int col = 0; col < allFields.length; col++)
            {
                Field field = allFields[col];
                //只设置有注解的参数
                if (field.isAnnotationPresent(Excel.class)){
                    Excel attr = field.getAnnotation(Excel.class);
                    String cartName = attr.name();
                    // 设置类的私有字段属性可访问.
                    field.setAccessible(true);
                    fieldsMap.put(cartName, field);
                    serialNum++;
                }
            }
            Field [] sortFields = new Field[serialNum];
            for (int i = 0; i < rows; i++)
            {
                Row row = sheet.getRow(i);
                //取出表头信息与变量上的name进行匹配排序
                if (i==0){
                    for(int j =0;j<serialNum;j++){
                        Object name = this.getCellValue(row,j);
                        if (!fieldsMap.containsKey(name)){
                            errorInfList.add(new ErrorInf(false,"表头信息与预期不符"));
                        }else {
                            sortFields[j] = fieldsMap.get(name);
                        }
                    }
                }else {
                    // 从第2行开始取数据,默认第一行是表头.
                    int cellNum = serialNum;
                    T entity = null;
                    for (int column = 0; column < cellNum; column++)
                    {
                        Object val = this.getCellValue(row, column);
                        // 如果不存在实例则新建.
                        entity = (entity == null ? clazz.newInstance() : entity);
                        // 从map中得到对应列的field.
                        Field field = sortFields[column];
                        // 取得类型,并根据对象类型设置值.
                        Class<?> fieldType = field.getType();
                        if (String.class == fieldType)
                        {
                            String s = Convert.toStr(val);
                            if (StringUtils.endsWith(s, ".0"))
                            {
                                val = StringUtils.substringBefore(s, ".0");
                            }
                            else
                            {
                                val = Convert.toStr(val);
                            }
                        }
                        else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType))
                        {
                            val = Convert.toInt(val);
                        }
                        else if ((Long.TYPE == fieldType) || (Long.class == fieldType))
                        {
                            val = Convert.toLong(val);
                        }
                        else if ((Double.TYPE == fieldType) || (Double.class == fieldType))
                        {
                            val = Convert.toDouble(val);
                        }
                        else if ((Float.TYPE == fieldType) || (Float.class == fieldType))
                        {
                            val = Convert.toFloat(val);
                        }
                        else if (BigDecimal.class == fieldType)
                        {
                            val = Convert.toBigDecimal(val);
                        }
                        else if (Date.class == fieldType)
                        {
                            if (val instanceof String)
                            {
                                val = DateUtils.parseDate(val);
                            }
                            else if (val instanceof Double)
                            {
                                val = DateUtil.getJavaDate((Double) val);
                            }
                        }
                        if (StringUtils.isNotNull(fieldType))
                        {
                            Excel attr = field.getAnnotation(Excel.class);
                            String propertyName = field.getName();
                            //判断是否为空
                            if (attr.isNull()==1){
                                if (val == null || "".equals(val)){
                                    errorInfList.add(new ErrorInf(false,"第"+(i+1)+"行,第"+(column+1)+"列不能为空"));
                                }
                            }
                            ReflectUtils.invokeSetter(entity, propertyName, val);
                        }
                    }
                    //一列的数据取出完成放入集合中
                    list.add(entity);
                }
            }
        }
        if (list.isEmpty()){
            errorInfList.add(new ErrorInf(false,"导入excel文件无数据，请核对后再做操作！"));
        }
        resultMap.put(DATA,list);
        resultMap.put(ERROE_INF,errorInfList);
        return resultMap;
    }


    /**
     * 对list数据源将其里面的数据导入到excel表单
     *
     * @param list 导出数据集合
     * @param sheetName 工作表的名称
     * @return 结果
     */
    public void exportExcel(List<T> list, String sheetName)
    {
        this.init(list, sheetName);
        exportExcel();
    }

    /**
     * 对list数据源将其里面的数据导入到excel表单
     *
     * @param sheetName 工作表的名称
     * @return 结果
     */
    public void importTemplateExcel(String sheetName)
    {
        this.init(null, sheetName);
        exportExcel();
    }

    /**
     * 对list数据源将其里面的数据导入到excel表单
     *
     * @return 结果
     */
    private OutputStream exportExcel()
    {
        OutputStream out = null;
        try
        {
            // 取出一共有多少个sheet.
            double sheetNo = Math.ceil(list.size() / sheetSize);
            for (int index = 0; index <= sheetNo; index++)
            {
                createSheet(sheetNo, index);
                Cell cell = null; // 产生单元格

                // 产生一行
                Row row = sheet.createRow(0);
                // 写入各个字段的列头名称
                for (int i = 0; i < fields.size(); i++)
                {
                    Field field = fields.get(i);
                    Excel attr = field.getAnnotation(Excel.class);
                    // 创建列
                    cell = row.createCell(i);
                    // 设置列中写入内容为String类型
                    cell.setCellType(CellType.STRING);
                    CellStyle cellStyle = wb.createCellStyle();
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
                    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                    Font font = wb.createFont();
                    // 粗体显示
                    font.setBold(true);
                    // 选择需要用到的字体格式
                    cellStyle.setFont(font);
                    cellStyle.setFillForegroundColor(HSSFColorPredefined.LIGHT_YELLOW.getIndex());
                    // 设置列宽
                    sheet.setColumnWidth(i, (int) (20 + 0.72) * 256);
                    row.setHeight((short) (50 * 20));

                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cellStyle.setWrapText(true);
                    cell.setCellStyle(cellStyle);
                    // 写入列名
                    cell.setCellValue(attr.name());
                }
                fillExcelData(index, row, cell);
            }
            String filename = encodingFilename(sheetName);
            out = new FileOutputStream("/Users/hupengfei/Downloads/"+filename);
            wb.write(out);
            out.flush();
        }
        catch (Exception e)
        {
            log.error("导出Excel异常{}"+e, e.getMessage());
        }
        finally
        {
            if (wb != null)
            {
                try
                {
                    wb.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 填充excel数据
     *
     * @param index 序号
     * @param row 单元格行
     * @param cell 类型单元格
     */
    public void fillExcelData(int index, Row row, Cell cell)
    {
        int startNo = index * sheetSize;
        int endNo = Math.min(startNo + sheetSize, list.size());
        // 写入各条记录,每条记录对应excel表中的一行
        CellStyle cs = wb.createCellStyle();
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        for (int i = startNo; i < endNo; i++)
        {
            row = sheet.createRow(i + 1 - startNo);
            // 得到导出对象.
            T vo = (T) list.get(i);
            for (int j = 0; j < fields.size(); j++)
            {
                // 获得field.
                Field field = fields.get(j);
                // 设置实体类私有属性可访问
                field.setAccessible(true);
                Excel attr = field.getAnnotation(Excel.class);
                try
                {
                    // 设置行高
                    row.setHeight((short) (50 * 20));
                    // 根据Excel中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
                    // 创建cell
                    cell = row.createCell(j);
                    cell.setCellStyle(cs);
                    if (vo == null)
                    {
                        // 如果数据存在就填入,不存在填入空格.
                        cell.setCellValue("");
                        continue;
                    }

                    // 用于读取对象中的属性
                    Object value = getTargetValue(vo, field, attr);
                    String dateFormat = attr.dateFormat();
                    if (StringUtils.isNotEmpty(dateFormat) && StringUtils.isNotNull(value))
                    {
                        cell.setCellValue(DateUtils.parseDateToStr(dateFormat, (Date) value));
                    }else {
                        cell.setCellType(CellType.STRING);
                        // 如果数据存在就填入,不存在填入空格.
                        cell.setCellValue(StringUtils.isNull(value) ? attr.defaultValue() : value+"");
                    }

                }
                catch (Exception e)
                {
                    log.error("导出Excel失败{}", e);
                }
            }
        }
    }

    /**
     * 获取bean中的属性值
     *
     * @param vo 实体对象
     * @param field 字段
     * @param excel 注解
     * @return 最终的属性值
     * @throws Exception
     */
    private Object getTargetValue(T vo, Field field, Excel excel) throws Exception
    {
        Object o = field.get(vo);

        return o;
    }
    /**
     * 创建工作表
     *
     * @param sheetNo sheet数量
     * @param index 序号
     */
    public void createSheet(double sheetNo, int index)
    {
        this.sheet = wb.createSheet();
        // 设置工作表的名称.
        if (sheetNo == 0)
        {
            wb.setSheetName(index, sheetName);
        }
        else
        {
            wb.setSheetName(index, sheetName + index);
        }
    }

    /**
     * 编码文件名
     */
    public String encodingFilename(String filename)
    {
        filename = UUID.randomUUID().toString() + "_" + filename + ".xlsx";
        return filename;
    }


    /**
     * 得到所有定义字段
     */
    private void createExcelField()
    {
        this.fields = new ArrayList<Field>();
        List<Field> tempFields = new ArrayList<>();
        Class<?> tempClass = clazz;
        tempFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        while (tempClass != null)
        {
            tempClass = tempClass.getSuperclass();
            if (tempClass != null)
            {
                tempFields.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            }
        }
        putToFields(tempFields);
    }


    /**
     * 放到字段集合中
     */
    private void putToFields(List<Field> fields) {
        for (Field field : fields) {
            this.fields.add(field);
        }
    }
    /**
     * 创建一个工作簿
     */
    public void createWorkbook()
    {
        this.wb = new SXSSFWorkbook(500);
    }


    /**
     * 获取单元格值
     *
     * @param row 获取的行
     * @param column 获取单元格列号
     * @return 单元格值
     */
    public Object getCellValue(Row row, int column)
    {
        if (row == null)
        {
            return row;
        }
        Object val = "";
        try
        {
            Cell cell = row.getCell(column);
            if (cell != null)
            {
                if (cell.getCellTypeEnum() == CellType.NUMERIC)
                {
                    val = cell.getNumericCellValue();
                    if (HSSFDateUtil.isCellDateFormatted(cell))
                    {
                        val = DateUtil.getJavaDate((Double) val); // POI Excel 日期格式转换
                    }
                    else
                    {
                        if ((Double) val % 1 > 0)
                        {
                            val = new DecimalFormat("0.00").format(val);
                        }
                        else
                        {
                            val = new DecimalFormat("0").format(val);
                        }
                    }
                }
                else if (cell.getCellTypeEnum() == CellType.STRING)
                {
                    val = cell.getStringCellValue();
                }
                else if (cell.getCellTypeEnum() == CellType.BOOLEAN)
                {
                    val = cell.getBooleanCellValue();
                }
                else if (cell.getCellTypeEnum() == CellType.ERROR)
                {
                    val = cell.getErrorCellValue();
                }

            }
        }
        catch (Exception e)
        {
            return val;
        }
        return val;
    }
}