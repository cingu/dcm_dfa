/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.dcm_dfa.Creations;

import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.model.ClickTag;
import com.google.api.services.dfareporting.model.Creative;
import com.google.api.services.dfareporting.model.CreativeCustomEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 *
 * @author cindy.nguyen
 */
public class MyCreative {
    private static final long SAXO_PROFILE_ID = 2842175;
    
    public static void updateClickTagsForCreatives(Dfareporting rep, String theFilePath) throws IOException{
        String filePath = theFilePath;
        FileInputStream fis = null;    
        long creativeId = 0;
        String lp = null;
        boolean creativeCheck = false, lpCheck = false;
        
        try {

            fis = new FileInputStream(filePath);
            // Using XSSF for xlsx format, for xls use HSSF
            Workbook workbook = new XSSFWorkbook(fis);
            int numberOfSheets = workbook.getNumberOfSheets();

            //looping over each workbook sheet
            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                Iterator rowIterator = sheet.iterator();
                
                //iterating over each row
        outerloop:  while (rowIterator.hasNext()) {                  
                    Row row = (Row) rowIterator.next();
                    Iterator cellIterator = row.cellIterator();
                                        
                    //Iterating over each cell (column wise)  in a particular row.
        innerloop:  while (cellIterator.hasNext()) {
                        Cell cell = (Cell) cellIterator.next();
                        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType() && cell.getColumnIndex()==0) {
                            long cellValue = (long) cell.getNumericCellValue();
                            creativeId = cellValue;
                            creativeCheck = true;
                        } else if (Cell.CELL_TYPE_STRING == cell.getCellType() && cell.getColumnIndex()==1){
                            String cellValue = cell.getStringCellValue();
                            lp = cellValue;
                            lpCheck = true;
                        }
                        
                        if(creativeCheck && lpCheck){
                            setClickTags(rep, creativeId, lp); 
                            creativeCheck = false;
                            lpCheck = false;
                        }
                    }
                }
            } fis.close();            
        }   catch (FileNotFoundException ex) {    
            Logger.getLogger(MyCreation.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (IOException ex) {    
            Logger.getLogger(MyCreation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setClickTags (Dfareporting rep, long creativeId, String lp) throws IOException{
        Creative creative = new Creative();    
        
        ClickTag ct = new ClickTag();
        ct.setEventName("exit");
        ct.setName("clickTag");
        ct.setValue(lp);
        List<ClickTag> clickTags = new ArrayList<>();
        clickTags.add(ct);           
        //creative.setExitCustomEvents(list);
        creative.setClickTags(clickTags);

        Creative result = rep.creatives().patch(SAXO_PROFILE_ID, creativeId, creative).execute();
    }
}