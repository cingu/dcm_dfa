/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.dcm_dfa.Creations;

import com.google.api.client.util.Strings;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.model.PlacementsListResponse;
import com.google.api.services.dfareporting.model.PricingSchedule;
import com.google.api.services.dfareporting.model.PricingSchedulePricingPeriod;
import com.google.api.services.dfareporting.model.Size;
import com.google.api.services.dfareporting.model.TagSetting;
import com.google.common.collect.ImmutableList;
import static com.mycompany.dcm_dfa.Creations.MyCreation.getEndDate;
import static com.mycompany.dcm_dfa.Creations.MyCreation.getStartDate;
import static com.mycompany.dcm_dfa.Creations.MyCreation.setEndDate;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
public class MyPlacement {
    private static final long SAXO_PROFILE_ID = 2842175;
    
    private static final HashMap<String, Long> siteNameIds = new HashMap<String,Long>(){{
        put("www.trade2win.com",(long)1147601);
        put("https://www.bloomberg.com/markets/stocks",(long)933913);
        put("https://uk.finance.yahoo.com/",(long)1077113);
        put("http://www.investorschronicle.co.uk/",(long)1015597);
        put("http://www.blis.com/",(long)3367768);
        put("http://www.investopedia.com/",(long)866996);
        put("www.advfn.co.uk",(long)2697043);
        
    }};   
    
    private static final HashMap<String, String> pricingTypes = new HashMap<String,String>(){{
        put("CPM","PRICING_TYPE_CPM");
        put("CPC","PRICING_TYPE_CPC");
        put("Flat Rate - Impressions","PRICING_TYPE_FLAT_RATE_IMPRESSIONS");               
    }};   
    
    //set up for placement mangler at inkludere cost (cpm, cpc etc) - done
    //og dato
    public static void newPlacement(String placementName, long campaignId, long dfaSiteId, int width, int height, 
                                        Dfareporting rep, long unit, long rateOrCost, String additionalKeyValues, String pricingType, int day, int month, int year){
        com.google.api.services.dfareporting.model.Placement placement = new com.google.api.services.dfareporting.model.Placement();
        placement.setName(placementName);
        placement.setCampaignId(campaignId);
        placement.setCompatibility("DISPLAY");
        placement.setPaymentSource("PLACEMENT_AGENCY_PAID");
        placement.setSiteId(dfaSiteId);
        placement.setTagFormats(ImmutableList.of("PLACEMENT_TAG_STANDARD"));        
        
        // Set the size of the placement.
        Size size = new Size();
        size.setWidth(width);
        size.setHeight(height);       
        placement.setSize(size);                                                                 
        
        List<PricingSchedulePricingPeriod> unitAndRate = new ArrayList<PricingSchedulePricingPeriod>();
        PricingSchedulePricingPeriod pspp = new PricingSchedulePricingPeriod();
        pspp.setStartDate(getStartDate());
        pspp.setEndDate(setEndDate(day, month, year));
        pspp.setUnits(unit);
        pspp.setRateOrCostNanos(rateOrCost*1000000000);        
        unitAndRate.add(pspp);       
        
        //Set the pricing schedule for the placement.
        PricingSchedule pricingSchedule = new PricingSchedule();
        pricingSchedule.setEndDate(setEndDate(day, month, year));
        String thePricingType = pricingTypes.get(pricingType);
        pricingSchedule.setPricingType(thePricingType);                       
        pricingSchedule.setStartDate(getStartDate());
        pricingSchedule.setPricingPeriods(unitAndRate);
        placement.setPricingSchedule(pricingSchedule);
        
        TagSetting ts = new TagSetting();
        ts.setAdditionalKeyValues(additionalKeyValues);
        placement.setTagSetting(ts);
                
        //pricingSchedule.setPricing
        
        try {        
            com.google.api.services.dfareporting.model.Placement result = rep.placements().insert(SAXO_PROFILE_ID, placement).execute();
        } catch (IOException ex) {
            Logger.getLogger(MyCreation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static long getPlacementIdByName(Dfareporting rep, String placementName) throws IOException{
        String fields = "nextPageToken,placements(id,name)";
        PlacementsListResponse placements;
        String nextPageToken = null; 
        
        do {
      // Create and execute the campaigns list request.
        placements = rep.placements().list(SAXO_PROFILE_ID).setFields(fields)
          .setPageToken(nextPageToken).execute();

      //Get specific campaigns
      for (com.google.api.services.dfareporting.model.Placement placement : placements.getPlacements()) {
                if(placement.getName().equals(placementName)) 
                //if(campaign.getId() == 20053330 )
                {
                    return placement.getId();                                              
                }
            } 

      // Update the next page token.
      nextPageToken = placements.getNextPageToken();        
    } while (!placements.getPlacements().isEmpty() && !Strings.isNullOrEmpty(nextPageToken));
      return 0;
    }
    
    public static void setUpPlacementsFromIO(Dfareporting rep, long campId, String theFilePath){
        //Get values from IO
        String filePath = theFilePath;
        FileInputStream fis = null;
        String site = null, placementName = null, costStructure = null, pricingType = null; //sitename hashmap
        int width = 0, height = 0; 
        long unit = 0, rateOrCost = 0, siteId = 0;    
        int rateOrCostIndex = 0;
        boolean siteCheck = false, placCheck = false, widthHeightCheck = false, priceCheck = false, unitCheck = false, rateOrCostCheck = false;
        
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
                        if (cell.getColumnIndex() == 0 && cell.getStringCellValue().isEmpty()){
                            //System.out.println(placementName+" "+height+"x"+width);
                            continue outerloop;
                        }
                        //The Cell Containing String will is name.
                        else if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
                            String cellValue = cell.getStringCellValue();
                            if (cell.getColumnIndex() == 0 && !cellValue.equals("Site Name (URL)")) {
                                site = cellValue;
                                siteId = siteNameIds.get(site);  
                                siteCheck = true;
                                System.out.println(siteCheck);
                            } else if (cell.getColumnIndex() == 6 && !cellValue.equals("Placement Name")){
                                placementName = cellValue;
                                placCheck = true;
                            } else if (cell.getColumnIndex() == 1 && !cellValue.equals("Banner size")){
                                String [] format = cellValue.split("x");
                                width = Integer.valueOf(format[0]);
                                height = Integer.valueOf(format[1]);
                                widthHeightCheck = true;
                            } else if (cell.getColumnIndex() == 7 && !cellValue.equals("Cost Structure")){
                                pricingType = cellValue;
                                priceCheck = true;
                                if (pricingType.equals("CPM")){rateOrCostIndex = 9; System.out.println("9");}
                                else if (pricingType.equals("Flat Rate - Impressions")){rateOrCostIndex = 11; System.out.println("11");}
                            }                          
                        } else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()){
                            double cellValue2 = cell.getNumericCellValue();
                            if (cell.getColumnIndex() == 8 && cell.getRowIndex()!=0){
                                unit = (long)cellValue2;
                                unitCheck = true;
                            } else if (cell.getColumnIndex() == rateOrCostIndex && cell.getRowIndex()!=0){
                                rateOrCost = (long)cellValue2; 
                                rateOrCostCheck = true;
                                System.out.println(rateOrCost + "w");
                            } 
                            
                        /*String cellValue = cell.getStringCellValue();
                        if (cell.getColumnIndex() == 0 && !cellValue.equals("Site Name (URL)")) {
                            site = cellValue;
                        } else if (cell.getColumnIndex() == 6 && !cellValue.equals("Placement Name")){
                            placementName = cellValue;
                        } else if (cell.getColumnIndex() == 1 && !cellValue.equals("Banner size")){
                            String size = cellValue;
                            String[] format = size.split("x");                                
                            height = Integer.valueOf(format[0]);
                            width = Integer.valueOf(format[1]);
                        }                                      */     
                        }
                    } //site!=null || placementName!=null
                    if(siteCheck && placCheck && widthHeightCheck && priceCheck && unitCheck && rateOrCostCheck){
                        //newPlacement(placementName, campId, siteId, width, height, rep, unit, rateOrCost, null, pricingType);
                        System.out.println("ok");
                        siteCheck = false;
                        placCheck = false;
                        widthHeightCheck = false;
                        priceCheck = false;
                        unitCheck = false;
                        rateOrCostCheck = false;
                        System.out.println("false check");
                        //createPlacement(placementName, campId, siteId, height, width, rep);
                        //System.out.println(placementName+" "+height+"x"+width);
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
