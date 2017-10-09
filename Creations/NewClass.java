/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.dcm_dfa.Creations;

import com.google.api.client.util.Strings;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.model.AdsListResponse;
import com.google.api.services.dfareporting.model.Creative;
import com.google.api.services.dfareporting.model.CreativeAssignment;
import com.google.api.services.dfareporting.model.CreativeRotation;
import com.mycompany.dcm_dfa.Reporting;
import static com.mycompany.dcm_dfa.Reporting.appendToCSV;
import static com.mycompany.dcm_dfa.Reporting.closeCSV;
import static com.mycompany.dcm_dfa.Reporting.createCsvFile;
import static com.mycompany.dcm_dfa.Reporting.getSpecificSaxoCampaigns;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cindy.nguyen
 */
public class NewClass {
    private static final long SAXO_PROFILE_ID = 2842175;
    private static PrintWriter pw; 
    private static StringBuilder sb;
    
    public static void createCsvFile(){
        try {
            pw = new PrintWriter(new File("CreativeOverview.csv"));
            sb = new StringBuilder();
            sb.append("Campaign");
            sb.append(',');
            sb.append("Creative");
            sb.append('\n');
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Reporting.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public static void appendToCSV(String campaign, String creative){
        sb.append(campaign);
        sb.append(",");
        sb.append(creative);
        sb.append('\n');         
    }
    
    public static void closeCSV(){
        pw.write(sb.toString());
        pw.close();
        System.out.println("done!");
    }
    
    public static void runExample(Dfareporting rep) throws Exception {
    // Limit the fields returned.
    String fields = "nextPageToken,ads(advertiserId,id,name,active,campaignId,creativeRotation)";
    String fieldsTest = "nextPageToken,creatives(id,name,active,creativeRotation)";


    AdsListResponse ads;
    String nextPageToken = null;
    createCsvFile();
    ArrayList<com.google.api.services.dfareporting.model.Campaign> campList = new ArrayList<com.google.api.services.dfareporting.model.Campaign>();
    campList = (ArrayList<com.google.api.services.dfareporting.model.Campaign>) getSpecificSaxoCampaigns(rep);
    List<com.google.api.services.dfareporting.model.Ad> adList = new ArrayList<>();

    do {
      // Create and execute the ad list request.
      ads = rep.ads().list(SAXO_PROFILE_ID).setActive(true).setFields(fields)
          .setPageToken(nextPageToken).execute();

      for (com.google.api.services.dfareporting.model.Ad ad : ads.getAds()) {         
                    
          /*System.out.printf("Ad with ID %d and name \"%s\" is associated with advertiser ID %d.%s.%n", ad.getId(),
            ad.getName(), ad.getAdvertiserId(), ad.getActive());   */ 
          
            //if(ad.getCampaignId() == (long) 8784868){    
            //System.out.println(ad.getCampaignId());
            //}
          
            for(com.google.api.services.dfareporting.model.Campaign campaign : campList){                
                if(Objects.equals(ad.getCampaignId(), campaign.getId())){                      
                    //System.out.println("a" +ad.getCampaignId() + "+" + "b"+campaign.getId());
                    //System.out.println("a");                                        
                    CreativeRotation cr = ad.getCreativeRotation();                      
                    List<CreativeAssignment> caList = cr.getCreativeAssignments(); 
                    //System.out.println(caList);                   
                        for(int i=0; i<caList.size();i++){     
                            //System.out.println(caList.size());
                            //System.out.println("b");
                            CreativeAssignment ca = caList.get(i);
                            long creativeId = ca.getCreativeId();
                            //System.out.println(creativeId);
                            Creative creative = rep.creatives().get(SAXO_PROFILE_ID,creativeId).execute();
                            //Ad ad2 = rep.ads().get(SAXO_PROFILE_ID, (long)315741945).execute();
                            //System.out.println(campaign.getName());
                            String creativeName = creative.getName();
                            System.out.println(creativeName);
                            appendToCSV(campaign.getName(), creativeName);
                            //System.out.println("c");
                        
                        }                       
                }
            }                   
        }

      // Update the next page token.
      nextPageToken = ads.getNextPageToken();
    } while (!ads.getAds().isEmpty() && !Strings.isNullOrEmpty(nextPageToken));
      closeCSV();
}}
