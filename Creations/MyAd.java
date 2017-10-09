/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.dcm_dfa.Creations;

import com.google.api.client.util.DateTime;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.model.ClickThroughUrl;
import com.google.api.services.dfareporting.model.CreativeAssignment;
import com.google.api.services.dfareporting.model.CreativeRotation;
import com.google.api.services.dfareporting.model.DeliverySchedule;
import com.google.api.services.dfareporting.model.PlacementAssignment;
import com.google.common.collect.ImmutableList;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
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
public class MyAd {
    
    private static final long SAXO_PROFILE_ID = 2842175; 
    
    //testet og virker
    //test assign creatives til samme ad
    public static void newAd(Dfareporting reporting, String adName,
      long campaignId, long creativeId, long placementId, String placementName) throws Exception { 
        
        //long placementId = getPlacementIdByName(reporting, placementName);        
        com.google.api.services.dfareporting.model.Campaign campaign = reporting.campaigns().get(SAXO_PROFILE_ID, campaignId).execute();
        // Create a click-through URL.
        ClickThroughUrl clickThroughUrl = new ClickThroughUrl();
        clickThroughUrl.setDefaultLandingPage(true);
        //clickThroughUrl.setCustomClickThroughUrl(customLp);

        // Create a creative assignment.
        CreativeAssignment creativeAssignment = new CreativeAssignment();
        creativeAssignment.setActive(true);
        creativeAssignment.setCreativeId(creativeId);
        creativeAssignment.setClickThroughUrl(clickThroughUrl);

        // Create a placement assignment.
        PlacementAssignment placementAssignment = new PlacementAssignment();
        placementAssignment.setActive(true);
        placementAssignment.setPlacementId(placementId);

        // Create a creative rotation.
        CreativeRotation creativeRotation = new CreativeRotation();
        creativeRotation.setCreativeAssignments(ImmutableList.of(creativeAssignment));

        // Create a delivery schedule.
        DeliverySchedule deliverySchedule = new DeliverySchedule();
        deliverySchedule.setImpressionRatio(1L);
        deliverySchedule.setPriority("AD_PRIORITY_01");   

        DateTime startDate = new DateTime(new Date());  
        DateTime endDate = new DateTime("2017-12-31T15:29:22.362+02:00");        
        
        // Create a rotation group.
        com.google.api.services.dfareporting.model.Ad rotationGroup = new com.google.api.services.dfareporting.model.Ad();
        rotationGroup.setActive(true);
        rotationGroup.setCampaignId(campaignId);
        rotationGroup.setCreativeRotation(creativeRotation);
        rotationGroup.setDeliverySchedule(deliverySchedule);
        rotationGroup.setStartTime(startDate);
        rotationGroup.setEndTime(endDate);        
        rotationGroup.setName(adName);
        rotationGroup.setPlacementAssignments(ImmutableList.of(placementAssignment));
        rotationGroup.setType("AD_SERVING_STANDARD_AD");

        // Insert the rotation group.
        com.google.api.services.dfareporting.model.Ad result = reporting.ads().insert(SAXO_PROFILE_ID, rotationGroup).execute();

        // Display the new ad ID.
        System.out.printf("Ad with ID %d was created.%n", result.getId());
    }
    
    public static void setUpAdsFromIO(Dfareporting rep, String theFilePath) throws Exception{
        String filePath = theFilePath;
        FileInputStream fis = null;        
        long campaignId = 0, creativeId = 0, placementId = 0; 
        String placementName = null;
        String adName = null;
        boolean camp = false, crea = false, plac = false, placN = false, adN = false;
        
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
                        if (cell.getRowIndex() == 0){
                            //System.out.println(placementName+" "+height+"x"+width);
                            continue outerloop;
                        } else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()){  
                            long cellValue = (long) cell.getNumericCellValue();
                            if (cell.getColumnIndex() == 0 && cell.getRowIndex() != 0) {
                                campaignId = cellValue;
                                System.out.println(campaignId);         
                                camp = true;
                            } else if (cell.getColumnIndex() == 1 && cell.getRowIndex() != 0){
                                creativeId = cellValue;
                                System.out.println(creativeId);
                                crea = true;
                            } else if (cell.getColumnIndex() == 2 && cell.getRowIndex() != 0){
                                placementId = cellValue;    
                                System.out.println(placementId);
                                plac = true;
                            }                  
                        } else if (Cell.CELL_TYPE_STRING == cell.getCellType()){
                            String cellValue2 = cell.getStringCellValue();                            
                            /*if(cell.getColumnIndex() == 0){
                                //System.out.println(placementName+" "+height+"x"+width);
                                System.out.println("out");
                                continue outerloop;                            
                            } */ if(cell.getColumnIndex() == 3 && !cellValue2.equals("placementName")){
                                placementName = cellValue2;
                                System.out.println(placementName);
                                placN = true;
                            } else if (cell.getColumnIndex() == 4 && !cellValue2.equals("adName")){
                                adName = cellValue2;
                                System.out.println(adName);      
                                adN = true;
                            } 
                            
                            if(camp && crea && plac && placN && adN ){ 
                                newAd(rep, adName, campaignId, creativeId, placementId, placementName);    
                                camp = false;
                                crea = false;
                                plac = false;
                                placN = false;
                                adN = false; 
                                //System.out.println("ok");
                                                               
                            } //else {System.out.println("Something missing..");}                          
                        }
                        /*if((campaignId!=0 || placementId!=0 || creativeId!=0) && (placementName!=null || adName!=null)){
                           // newAd(rep, adName, campaignId, creativeId, placementId, placementName);                          
                            System.out.println("ok");
                        }*/
                    }                   
                }
            } fis.close();            
        }   catch (FileNotFoundException ex) {    
            Logger.getLogger(MyCreation.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (IOException ex) {    
            Logger.getLogger(MyCreation.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }    
}
