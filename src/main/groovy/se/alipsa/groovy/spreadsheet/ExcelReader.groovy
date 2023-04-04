package se.alipsa.groovy.spreadsheet

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

class ExcelReader implements Closeable {
    private Workbook workbook;

    ExcelReader(File excelFile) {
        workbook = WorkbookFactory.create(excelFile)
    }

    ExcelReader(String filePath) {
        this(FileUtil.checkFilePath(filePath))
    }

    /**
     * @return a List of the names of the sheets in the excel
     */
    List<String> getSheetNames() throws Exception {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            names.add(workbook.getSheetAt(i).getSheetName());
        }
        return names
    }

    static List<String> getSheetNames(Workbook workbook) throws Exception {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            names.add(workbook.getSheetAt(i).getSheetName());
        }
        return names
    }

    /**
     * Find the first row index matching the content.
     * @param sheetNumber the sheet index (1 indexed)
     * @param colNumber the column number (1 indexed)
     * @param content the string to search for
     * @return the Row as seen in Excel (1 is first row)
     * @throws Exception if something goes wrong
     */
    int findRowNum(int sheetNumber, int colNumber, String content) {
        Sheet sheet = workbook.getSheetAt(sheetNumber -1)
        return findRowNum(sheet, colNumber, content)
    }

    /**
     * @param sheetName the name of the sheet
     * @param colNumber the column number (1 indexed)
     * @param content the string to search for
     * @return the Row as seen in Excel (1 is first row)
     */
    int findRowNum(String sheetName, int colNumber, String content) {
        Sheet sheet = workbook.getSheet(sheetName)
        return findRowNum(sheet, colNumber, content)
    }

    /**
     * Find the first row index matching the content.
     * @param sheetName the name of the sheet to search in
     * @param colName the column name (A for first column etc.)
     * @param content the string to search for
     * @return the Row as seen in Excel (1 is first row)
     */
    int findRowNum(String sheetName, String colName, String content) {
        Sheet sheet = workbook.getSheet(sheetName)
        return findRowNum(sheet, SpreadsheetUtil.asColumnNumber(colName), content)
    }

    /**
     *
     * @param sheet the sheet to search in
     * @param colNumber the column number (1 indexed)
     * @param content the string to search for
     * @return the Row as seen in Excel (1 is first row)
     */
    int findRowNum(Sheet sheet, int colNumber, String content) {
        ExcelValueExtractor ext = new ExcelValueExtractor(sheet)
        int poiColNum = colNumber -1
        for (int rowCount = 0; rowCount < sheet.getLastRowNum(); rowCount ++) {
            Row row = sheet.getRow(rowCount)
            if (row == null) continue
            Cell cell = row.getCell(poiColNum)
            if (content == ext.getString(cell)) {
                return rowCount + 1
            }
        }
        return -1;
    }

    /**
     * Find the first column index matching the content criteria
     * @param sheetNumber the sheet index (1 indexed)
     * @param rowNumber the row number (1 indexed)
     * @param content the string to search for
     * @return the row number that matched or -1 if not found
     */
    int findColNum(int sheetNumber, int rowNumber, String content) {
        Sheet sheet = workbook.getSheetAt(sheetNumber - 1)
        return findColNum(sheet, rowNumber, content)
    }

    /** return the column as seen in excel (e.g. using column(), 1 is the first column etc
     * @param sheetName the name of the sheet
     * @param rowNumber the row number (1 indexed)
     * @param content the string to search for
     * @return the row number that matched or -1 if not found
     */
    int findColNum(String sheetName, int rowNumber, String content) {
        Sheet sheet = workbook.getSheet(sheetName)
        return findColNum(sheet, rowNumber, content)
    }

    /**
     * Find the first column index matching the content criteria
     * @param sheet the Sheet to search
     * @param rowNumber the row number (1 indexed)
     * @param content the string to search for
     * @return return the column as seen in excel (e.g. using column(), 1 is the first column etc
     */
    int findColNum(Sheet sheet, int rowNumber, String content) {
        if (content==null) return -1
        ExcelValueExtractor ext = new ExcelValueExtractor(sheet)
        int poiRowNum = rowNumber - 1
        Row row = sheet.getRow(poiRowNum)
        for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
            Cell cell = row.getCell(colNum);
            if (content == ext.getString(cell)) {
                return colNum + 1
            }
        }
        return -1
    }

    @Override
    void close() throws IOException {
        if (workbook != null) {
            workbook.close()
            workbook = null
        }
    }
}
