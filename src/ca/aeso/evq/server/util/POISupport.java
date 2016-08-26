package ca.aeso.evq.server.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * POISupport
 * This class supports creation of Apache POI objects
 * 
 * @author mbodor
 */
public class POISupport {

	/**
	 * Creates a cell and aligns it a certain way.
	 *
	 * @param wb        the workbook
	 * @param row       the row to create the cell in
	 * @param column    the column number to create the cell in
	 * @param align     the alignment for the cell.
	 * @param value     the string value to assign to the cell
	 */
	public static HSSFCell createCell(HSSFWorkbook wb, HSSFCellStyle style, HSSFRow row, short column, String value) {
		HSSFCell cell = row.createCell(column);
		cell.setCellValue(new HSSFRichTextString(value));
		cell.setCellStyle(style);
		return cell;
	}

	public static HSSFCell createCell(HSSFWorkbook wb, HSSFCellStyle style, HSSFRow row, short column, double value) {
		HSSFCell cell = row.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
		return cell;
	}

	public static HSSFCell createCell(HSSFWorkbook wb, HSSFCellStyle style, HSSFRow row, short column, int value) {
		HSSFCell cell = row.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
		return cell;
	}

	public static HSSFCell createCell(HSSFWorkbook wb, HSSFCellStyle style, HSSFRow row, short column, long value) {
		HSSFCell cell = row.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
		return cell;
	}

	public static HSSFCellStyle createHeaderStyle(HSSFWorkbook wb, boolean underline) {
		HSSFFont boldFont = wb.createFont();
		boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		HSSFCellStyle style = createDetailStyle(wb, HSSFCellStyle.ALIGN_CENTER, null, boldFont );
		if (underline)
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		return style;
	}

	public static HSSFCellStyle createDetailStyle(HSSFWorkbook wb, short align, String dataFormat, HSSFFont font) {
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(align);
		if (dataFormat!=null) {
			style.setDataFormat(wb.createDataFormat().getFormat(dataFormat));
		}
		
		if (font!=null)
			style.setFont(font);

		return style;
	}
}
