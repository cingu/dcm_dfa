/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.dcm_dfa.Creations;

/**
 *
 * @author cindy.nguyen
 */

import com.google.api.client.util.DateTime;
import com.google.api.client.util.Strings;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.Dfareporting.EventTags;
import com.google.api.services.dfareporting.model.Ad;
import com.google.api.services.dfareporting.model.Campaign;
import com.google.api.services.dfareporting.model.CampaignsListResponse;
import com.google.api.services.dfareporting.model.ClickThroughUrl;
import com.google.api.services.dfareporting.model.ClickThroughUrlSuffixProperties;
import com.google.api.services.dfareporting.model.CreativeAssignment;
import com.google.api.services.dfareporting.model.CreativeRotation;
import com.google.api.services.dfareporting.model.DeliverySchedule;
import com.google.api.services.dfareporting.model.EventTag;
import com.google.api.services.dfareporting.model.EventTagsListResponse;
import com.google.api.services.dfareporting.model.Flight;
import com.google.api.services.dfareporting.model.LandingPage;
import com.google.api.services.dfareporting.model.Placement;
import com.google.api.services.dfareporting.model.PlacementAssignment;
import com.google.api.services.dfareporting.model.PlacementsListResponse;
import com.google.api.services.dfareporting.model.PricingSchedule;
import com.google.api.services.dfareporting.model.PricingSchedulePricingPeriod;
import com.google.api.services.dfareporting.model.Size;
import com.google.api.services.dfareporting.model.TagSetting;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class MyCreation {
    private static final long SAXO_PROFILE_ID = 2842175;   
    
    public static DateTime getStartDate(){
        Calendar today = Calendar.getInstance();
        DateTime startDate = new DateTime(true, today.getTimeInMillis(), null); 
        
        return startDate;
    }
    
    public static DateTime getEndDate(){
        Calendar toEnd = Calendar.getInstance();
        toEnd.set(Calendar.YEAR,2017);
        toEnd.set(Calendar.MONTH, 11);
        toEnd.set(Calendar.DAY_OF_MONTH, 31);        
        DateTime endDate = new DateTime(true, toEnd.getTimeInMillis(), null);
        
        return endDate;
    }
    
    public static DateTime setEndDate(int day, int month, int year){
        Calendar toEnd = Calendar.getInstance();
        toEnd.set(Calendar.YEAR,year);
        toEnd.set(Calendar.MONTH, month-1);
        toEnd.set(Calendar.DAY_OF_MONTH, day);        
        DateTime endDate = new DateTime(true, toEnd.getTimeInMillis(), null);
        
        return endDate;
    }
    
    
    
    public static void insertLandingpage(Dfareporting rep, long campaignId, String landingPageName, String url) throws IOException{
        LandingPage requestBody = new LandingPage();
        requestBody.setName(landingPageName);
        requestBody.setUrl(url);
        
        Dfareporting.LandingPages.Insert request =
        rep.landingPages().insert(SAXO_PROFILE_ID, campaignId , requestBody);

        LandingPage response = request.execute();
        
        //For deletion of landing page
        /*Dfareporting.LandingPages.Delete request =
        rep.landingPages().delete(SAXO_PROFILE_ID, (long)20170491 , (long)20324238);

        Void response = request.execute();
        */
    }                           
       
    public static void setEventTags(Dfareporting rep, long campId) throws IOException{
        Campaign camp = new Campaign();
        EventTag clickTag = new EventTag();
        //clickTag.setCampaignId(campId);
        //clickTag.setUrl("https://t.gscontxt.net/j?z=appnexus_xaxisapc;vn=1;plat=dcm;adv=%eadv!;camp=%ebuy!;creative=%ecid!;lineitem=%eaid!;site=%esid!;place=%epid!");
        EventTags.Get tags = rep.eventTags().get((long)20053333, (long)2018373);        
    }
   
}
