package sc.common.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import sc.common.constants.RecordTitleEnum;
import sc.system.model.Record;

public class ExcelUtil {

	public static void main(String[] args) throws FileNotFoundException {
		try (InputStream in = new FileInputStream("E:\\testFile\\excel\\record.xlsx");){
			List<List<Object>> rows = readExcel(in);
			for (int i = 0; i < rows.get(0).size(); i++) {
				if (StringUtil.isNull(RecordTitleEnum.txtOf(rows.get(0).get(i).toString()))) {
					System.out.println("第"+(i+1)+"列标题内容不正确");
				};
			}
			
			List<Record> records = new ArrayList<Record>();
			for (int i = 1; i < rows.size(); i++) {
				Record record = new Record();
				for (int j = 0; j < rows.get(i).size(); j++) {
					if (RecordTitleEnum.DOCTOR_NAME.getTxt().equals(rows.get(0).get(j))) {
						record.setDoctorName(rows.get(i).get(j).toString());
					}else if (RecordTitleEnum.DOCTOR_PHONE.getTxt().equals(rows.get(0).get(j))) {
						record.setDoctorPhone(rows.get(i).get(j).toString());
					}else if (RecordTitleEnum.ORG_NAME.getTxt().equals(rows.get(0).get(j))) {
						record.setOrgName(rows.get(i).get(j).toString());
					}else if (RecordTitleEnum.RECORD_DATE.getTxt().equals(rows.get(0).get(j))) {
						record.setRecordDate((Date)rows.get(i).get(j));
					}else if (RecordTitleEnum.END_DATE.getTxt().equals(rows.get(0).get(j))) {
						record.setEndDate((Date)rows.get(i).get(j));
					}
				}
				records.add(record);
			}
			
			System.out.println("records : "+records);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static List<List<Object>> readExcel(InputStream in) throws Exception{
		
		List<List<Object>> rows = new ArrayList<List<Object>>();
		
		Workbook wb = WorkbookFactory.create(in);
		Sheet sheet = wb.getSheetAt(0);
		for (Row row : sheet) {
			List<Object> cells = new ArrayList<Object>();
			System.out.println("cell num : "+sheet.getRow(0).getLastCellNum());
			for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
				Cell cell = row.getCell(i);
				if(cell == null) {
					cell = row.createCell(i, CellType.BLANK);
				}
	        	switch (cell.getCellType()) {
		            case STRING:
		                cells.add(cell.getRichStringCellValue().getString().trim());
		                break;
		            case NUMERIC:
		                if (DateUtil.isCellDateFormatted(cell)) {
		                	cells.add(cell.getDateCellValue());
		                } else {
		                	cells.add(cell.getNumericCellValue());
		                }
		                break;
		            case BOOLEAN:
		            	cells.add(cell.getBooleanCellValue());
		                break;
		            case FORMULA:
		            	cells.add(cell.getCellFormula());
		                break;
		            case BLANK:
		            	cells.add("");
		                break;
		            default:
		                throw new Exception("第"+(row.getRowNum()+1)+"行 第"+(cell.getColumnIndex()+1)+"列的单元格数据不合法，类型为Error类型:"+cell.getErrorCellValue());
		        }
	        }
	        rows.add(cells);
	    }
		
		return rows;
	}

}
