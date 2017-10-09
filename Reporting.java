/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.dcm_dfa;

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Strings;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.Dfareporting.Ads;
import com.google.api.services.dfareporting.model.Account;
import com.google.api.services.dfareporting.model.Ad;
import com.google.api.services.dfareporting.model.AdsListResponse;
import com.google.api.services.dfareporting.model.Advertiser;
import com.google.api.services.dfareporting.model.Campaign;
import com.google.api.services.dfareporting.model.CampaignsListResponse;
import com.google.api.services.dfareporting.model.AdvertisersListResponse;
import com.google.api.services.dfareporting.model.CampaignCreativeAssociation;
import com.google.api.services.dfareporting.model.CampaignCreativeAssociationsListResponse;
import com.google.api.services.dfareporting.model.ClickThroughUrl;
import com.google.api.services.dfareporting.model.Creative;
import com.google.api.services.dfareporting.model.CreativeAsset;
import com.google.api.services.dfareporting.model.CreativeAssignment;
import com.google.api.services.dfareporting.model.CreativeRotation;
import com.google.api.services.dfareporting.model.CreativesListResponse;
import com.google.api.services.dfareporting.model.DeliverySchedule;
import com.google.api.services.dfareporting.model.LandingPagesListResponse;
import com.google.api.services.dfareporting.model.Placement;
import com.google.api.services.dfareporting.model.PlacementAssignment;
import com.google.api.services.dfareporting.model.PlacementsListResponse;
import com.google.api.services.dfareporting.model.UserProfileList;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cindy.nguyen
 */
public class Reporting {    
    private static final Map<String, String> advertiserId = new HashMap<String, String>()  {{
        put("UK", "2340493");
        put("AU", "3576380");
        
    }};        
    
    private static final long SAXO_PROFILE_ID = 2842175; 
    private static PrintWriter pw; 
    private static StringBuilder sb;
    private static ArrayList<Long> campCreatives = new ArrayList<Long>();
    private static ArrayList<Long> adCreatives = new ArrayList<Long>();
    private static ArrayList<Long> theCreatives = new ArrayList<Long>();
    private static ArrayList<Campaign> campaignList = new ArrayList<Campaign>();
    
    public static void getPlacements(Dfareporting rep) throws IOException{        
        Dfareporting.Placements.List request = rep.placements().list(SAXO_PROFILE_ID);
        PlacementsListResponse response = request.execute();
        
        response.getPlacements().forEach((cnsmr) -> {          
            //System.out.println(cnsmr.setS);
            });
    }
    
    public static void getLandingpages (Dfareporting rep) throws IOException{
        LandingPagesListResponse response;
        Dfareporting.LandingPages.List lp;   
                        
        Dfareporting.Ads.List request = rep.ads().list(SAXO_PROFILE_ID);      
        AdsListResponse ads = request.execute();
                               
        //ads.getAds().stream().map((ad) -> ad.getClickThroughUrl().toString()).forEachOrdered((url) -> {
        for (Ad ad : ads.getAds()){
            //String url = ad.getClickThroughUrl().toString();
            if (ad.getType().equals("AD_SERVING_STANDARD_AD")){                
                String test = ad.getType();                       
                System.out.println(test + "yes");
        }
        }
        
        /*camp = rep.campaigns().get(SAXO_PROFILE_ID, SAXO_PROFILE_ID);
        
        
        //https://www.home.saxo/da-dk/campaigns/ao/trade-stocks-with-saxo

       
        lp = rep.landingPages().list(SAXO_PROFILE_ID, (long)20170491 );
        response = lp.execute();
        response.getLandingPages().forEach((cnsmr) -> {           
            if(cnsmr.getId() == 20311940){
                String a = cnsmr.getUrl().toString();
                System.out.println(a);
            };
                
          
    });*/
        
    }
            
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
    
    
    
    
    public static List<Campaign> getSpecificSaxoCampaigns(Dfareporting rep) throws IOException{
    // Limit the fields returned.
    String fields = "nextPageToken,campaigns(id,name)";
    CampaignsListResponse campaigns;
    String nextPageToken = null;    
    //createCsvFile();  

    do {
      // Create and execute the campaigns list request.
      campaigns = rep.campaigns().list(SAXO_PROFILE_ID).setFields(fields)
          .setPageToken(nextPageToken).execute();

      //Get specific campaigns
      for (Campaign campaign : campaigns.getCampaigns()) {
                if(campaign.getName().contains("Programmatic") || campaign.getName().contains("Priority") ||
                        campaign.getName().contains("Phase")) 
                //if(campaign.getId() == 20053330 )
                {
                    campaignList.add(campaign);
                    /*long campId = campaign.getId(); 
                    String campName = campaign.getName();
                    CampaignCreativeAssociationsListResponse creativesList = rep.campaignCreativeAssociations().list(SAXO_PROFILE_ID, campId).execute();                    
                    
                    //Get all creatives in campaign (incl. creative id)
                    for (CampaignCreativeAssociation creativeAssociation : creativesList.getCampaignCreativeAssociations()){                           
                            Creative creative = rep.creatives().get(SAXO_PROFILE_ID, creativeAssociation.getCreativeId()).execute();   
                            String creativeName = creative.getName();    
                            campCreatives.add(creative.getId());
                            //if (test(rep, campId, creative.getId(), campaign.getName(), creative.getName())){
                                //appendToCSV(campName, creativeName);
                            //}                                                        
                    }  */                                               
                }
            } 

      // Update the next page token.
      nextPageToken = campaigns.getNextPageToken();        
    } while (!campaigns.getCampaigns().isEmpty() && !Strings.isNullOrEmpty(nextPageToken));
      return campaignList;
      //closeCSV();
  }            
       
    
    public static void test (Dfareporting rep) throws IOException{
    AdsListResponse ads = rep.ads().list(SAXO_PROFILE_ID).execute();   
    createCsvFile();
    ArrayList<Campaign> campList = new ArrayList<Campaign>();
    campList = (ArrayList<Campaign>) getSpecificSaxoCampaigns(rep);
        System.out.println(ads);
        //Get all ads from specific campaign       
        for (Ad ad : ads.getAds()){   
            System.out.println(ad.getActive());
            if(ad.getActive()){
                System.out.println("0");
                
            }         
        }
    } //closeCSV();
 
    
    /*
                CreativeRotation cr = ad.getCreativeRotation();
                System.out.println("a");
                List<CreativeAssignment> caList = cr.getCreativeAssignments();
                System.out.println("b");
                for(Campaign c : campList){                
                    if(Objects.equals(ad.getCampaignId(), c.getId())){

                        System.out.println("c");
                        for(int j=0; j<=caList.size();j++){
                            CreativeAssignment ca = caList.get(j);

                            System.out.println("yes");

                        }            
                    }
                }
    */
    
    public static void test1(Dfareporting rep) throws IOException{
        Ad ad = new Ad();        
        Campaign c = rep.campaigns().get(SAXO_PROFILE_ID, (long)20169557).execute();
                
        CreativeAssignment ca = new CreativeAssignment();
        ca.setActive(Boolean.TRUE);
        ca.setCreativeId((long)90847705);
        //120x600
        
        PlacementAssignment pa = new PlacementAssignment();
        pa.setActive(Boolean.TRUE);
        pa.setPlacementId((long)203379146);
        
        CreativeRotation cr = new CreativeRotation();
        cr.setCreativeAssignments(ImmutableList.of(ca));
        
        ad.setName("test");
        ad.setActive(Boolean.TRUE);
        ad.setPlacementAssignments(ImmutableList.of(pa));
        ad.setType("AD_SERVING_STANDARD_AD");
        ad.setCampaignId((long)20169557);
        ad.setCreativeRotation(cr);
        
        Ad result = rep.ads().insert(SAXO_PROFILE_ID, ad).execute();
        
    }
    
    
    
    
    public static void runExample(Dfareporting rep) throws Exception {
    // Limit the fields returned.
    String fields = "nextPageToken,ads(advertiserId,id,name,active,campaignId,creativeRotation)";
    String fieldsTest = "nextPageToken,creatives(id,name,active,creativeRotation)";


    AdsListResponse ads;
    String nextPageToken = null;
    createCsvFile();
    ArrayList<Campaign> campList = new ArrayList<Campaign>();
    campList = (ArrayList<Campaign>) getSpecificSaxoCampaigns(rep);
    List<Ad> adList = new ArrayList<>();

    do {
      // Create and execute the ad list request.
      ads = rep.ads().list(SAXO_PROFILE_ID).setActive(true).setFields(fields)
          .setPageToken(nextPageToken).execute();

      for (Ad ad : ads.getAds()) {         
                    
          /*System.out.printf("Ad with ID %d and name \"%s\" is associated with advertiser ID %d.%s.%n", ad.getId(),
            ad.getName(), ad.getAdvertiserId(), ad.getActive());   */ 
          
            //if(ad.getCampaignId() == (long) 8784868){    
            //System.out.println(ad.getCampaignId());
            //}
          
            for(Campaign campaign : campList){                
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
  }
   
    /*
            
    public static void bla(Dfareporting rep) throws GeneralSecurityException{   
        
        CampaignsListResponse response;  
        Dfareporting.Campaigns.List request; 
        try {
            request = rep.campaigns().list(SAXO_PROFILE_ID);
            response = request.execute();
            
            response.getCampaigns().forEach((camp) -> {
                if (camp.containsValue("phase two")){
                    System.out.println(camp);
                }                
            });         
            
            request.setPageToken(response.getNextPageToken());
            while (response.getNextPageToken() != null);
        } catch (IOException ex) {
            Logger.getLogger(Reporting.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }

    public static void getCreatives(Dfareporting rep, long campId) throws IOException {
        
        Campaign campaign = rep.campaigns().get(SAXO_PROFILE_ID, campId).execute();        
        CampaignCreativeAssociationsListResponse creativesList = rep.campaignCreativeAssociations().list(SAXO_PROFILE_ID, campId).execute();
        
        for(CampaignCreativeAssociation creatives : creativesList.getCampaignCreativeAssociations()){
            System.out.println(creatives.getCreativeId());
            
            Dfareporting.Creatives.Get creative = rep.creatives().get(SAXO_PROFILE_ID, creatives.getCreativeId());
            Creative c = creative.execute();
            System.out.println(c.getName());
        }
        
        CreativesListResponse creativesList = rep.creatives().list(SAXO_PROFILE_ID).execute();
        
        for(Creative creative : creativesList.getCreatives()){
            for(CreativeAsset ca : creative.getCreativeAssets()){
                System.err.println(ca.getAssetIdentifier().getName());
            }
        }
        
        AdsListResponse adsList = rep.ads().list(SAXO_PROFILE_ID).execute();
        System.out.println(1);
        for (Ad ad : adsList.getAds()){
            
            System.out.println(ad.getCampaignId());
            if(ad.getCampaignId() == campId){
                System.out.println(3);
                
                System.out.println(ad.getCreativeGroupAssignments());
                
               
            }
        }
        
        
        AdvertisersListResponse response;  
        Dfareporting.Advertisers.List request; 
        
        CreativesListResponse ca = rep.creatives().list(SAXO_PROFILE_ID).execute();
        
        for(Creative creative : ca.getCreatives()){
            System.out.println(creative.getName());
            
        }
        
        
        try {
            request = rep.advertisers().list(SAXO_PROFILE_ID);
            response = request.execute();
            
            response.getAdvertisers().forEach((camp) -> {
                System.out.println(camp);
            });         
            
            request.setPageToken(response.getNextPageToken());
            while (response.getNextPageToken() != null);
        } catch (IOException ex) {
            Logger.getLogger(Reporting.class.getName()).log(Level.SEVERE, null, ex);
        }      */  
}
