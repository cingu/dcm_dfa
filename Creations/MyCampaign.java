/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.dcm_dfa.Creations;

import com.google.api.client.util.Strings;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.model.CampaignsListResponse;
import com.google.api.services.dfareporting.model.ClickThroughUrlSuffixProperties;
import static com.mycompany.dcm_dfa.Creations.MyCreation.getEndDate;
import static com.mycompany.dcm_dfa.Creations.MyCreation.getStartDate;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cindy.nguyen
 */
public class MyCampaign {
    private static final long SAXO_PROFILE_ID = 2842175;
    
    public static void createCampaign(Dfareporting rep, String campaignName, long advertiserId, String landingPageName, String url){
        // Create the campaign structure.
        com.google.api.services.dfareporting.model.Campaign campaign = new com.google.api.services.dfareporting.model.Campaign();
        campaign.setName(campaignName);
        campaign.setAdvertiserId(advertiserId);
        campaign.setArchived(false);
    
        campaign.setStartDate(getStartDate());   
        campaign.setEndDate(getEndDate());                
        
        try {
            com.google.api.services.dfareporting.model.Campaign result = rep.campaigns().insert(SAXO_PROFILE_ID, landingPageName, url, campaign).execute();
        } catch (IOException ex) {
            Logger.getLogger(MyCreation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static long getCampaignIdByName(Dfareporting rep, String campaignName) throws IOException{
       String fields = "nextPageToken,campaigns(id,name)";
       String nextPageToken = null;
       CampaignsListResponse campList;

       do {
       campList = rep.campaigns().list(SAXO_PROFILE_ID).setFields(fields)
         .setPageToken(nextPageToken).execute();

           for(com.google.api.services.dfareporting.model.Campaign campaign : campList.getCampaigns()){
               if(campaign.getName().equals(campaignName)){
                   return campaign.getId();
               }
           }
           nextPageToken = campList.getNextPageToken();
       } while (!campList.getCampaigns().isEmpty() && !Strings.isNullOrEmpty(nextPageToken));
       return 0;
    }         
     
    public static void setLandingPageSuffix(Dfareporting reporting, long campId) throws IOException{       
      com.google.api.services.dfareporting.model.Campaign camp = new com.google.api.services.dfareporting.model.Campaign();
      ClickThroughUrlSuffixProperties suffix = new ClickThroughUrlSuffixProperties();         
      suffix.setClickThroughUrlSuffix("?dfaid=1&cmpid=dfa_%eadv!;%esid!;%epid!;%ebuy!;%ecid!;");
      suffix.setOverrideInheritedSuffix(Boolean.TRUE);
      camp.setClickThroughUrlSuffixProperties(suffix);

      com.google.api.services.dfareporting.model.Campaign result = reporting.campaigns().patch(SAXO_PROFILE_ID, (long)campId , camp).execute();        

      /*
      // Limit the fields returned.
      String fields = "nextPageToken,campaigns(id,name)";

      CampaignsListResponse campaigns;
      String nextPageToken = null;

      do {
        // Create and execute the campaigns list request.
        campaigns = reporting.campaigns().list(SAXO_PROFILE_ID).setFields(fields)
            .setPageToken(nextPageToken).execute();

        for (Campaign campaign : campaigns.getCampaigns()) {
                  if(campaign.getId() == campId)
                  {                    
                      //System.out.println(campaign.getName());  
                      ClickThroughUrlSuffixProperties suffix = new ClickThroughUrlSuffixProperties();         
                      suffix.setClickThroughUrlSuffix("?dfaid=1&cmpid=dfa_%eadv!;%esid!;%epid!;%ebuy!;%ecid!;");
                      suffix.setOverrideInheritedSuffix(Boolean.TRUE);
                      campaign.setClickThroughUrlSuffixProperties(suffix);

                      Dfareporting.Campaigns.Patch request = reporting.campaigns().patch(SAXO_PROFILE_ID, (long)campId , campaign);
                      Campaign result = request.execute();                                                            
                  }
              }

        // Update the next page token.
        nextPageToken = campaigns.getNextPageToken();
      } while (!campaigns.getCampaigns().isEmpty() && !Strings.isNullOrEmpty(nextPageToken));*/
    }
    
}
