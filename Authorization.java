/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.dcm_dfa;

import com.mycompany.dcm_dfa.Creations.MyCreation;
import com.mycompany.dcm_dfa.Creations.MyPlacement;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import static com.google.api.client.util.Charsets.UTF_8;
import com.google.api.client.util.Strings;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.DfareportingScopes;
import com.google.api.services.dfareporting.model.Placement;
import com.google.api.services.drive.DriveScopes;
import com.google.common.collect.ImmutableSet;
import com.mycompany.dcm_dfa.Creations.MyAd;
import com.mycompany.dcm_dfa.Creations.MyCreative;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cindy.nguyen
 */
public class Authorization {
  private static final String PATH_TO_CLIENT_SECRETS = "";

  // Location where authorization credentials will be cached.
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/dfareporting_auth_sample");

  // The OAuth 2.0 scopes to request.
  private static final ImmutableSet<String> OAUTH_SCOPES =
      //ImmutableSet.of(DfareportingScopes.DFAREPORTING); 
          ImmutableSet.of(DfareportingScopes.DFATRAFFICKING);
  
  // Set up id lists for sites, advertiser

  
  
  private static Credential getUserAccountCredential(
    String pathToClientSecretsFile, DataStoreFactory dataStoreFactory) throws Exception {
    HttpTransport httpTransport = Utils.getDefaultTransport();
    JsonFactory jsonFactory = Utils.getDefaultJsonFactory();

    // Load the client secrets JSON file.
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(
            jsonFactory, Files.newBufferedReader(Paths.get(pathToClientSecretsFile), UTF_8));

    // Set up the authorization code flow.
    //
    // Note: providing a DataStoreFactory allows auth credentials to be cached, so they survive
    // multiple runs of the program. This avoids prompting the user for authorization every time the
    // access token expires, by remembering the refresh token.
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, OAUTH_SCOPES)
            .setDataStoreFactory(dataStoreFactory)
            .build();

    // Authorize and persist credentials to the data store.
    //
    // Note: the "user" value below is used to identify a specific set of credentials in the data
    // store. You may provide different values here to persist credentials for multiple users to
    // the same data store.
    Credential credential =
        new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    return credential;
  }


  public static void main(String[] args) throws Exception {
    // Build installed application credential.    
    Credential credential =
        getUserAccountCredential(PATH_TO_CLIENT_SECRETS, new FileDataStoreFactory(DATA_STORE_DIR));
        //getServiceAccountCredential(PATH_TO_CLIENT_SECRETS, EMAIL_TO_IMPERSONATE);
    // Create a Dfareporting client instance.
    //
    // Note: application name below should be replaced with a value that identifies your
    // application. Suggested format is "MyCompany-ProductName/Version.MinorVersion".
    Dfareporting reporting =
        new Dfareporting.Builder(credential.getTransport(), credential.getJsonFactory(), credential)
<<<<<<< HEAD
            .setApplicationName("dfareporting-groupm")
            .build();
    
    
    //Creation.setUpPlacementsFromIO(reporting, 20169557, "/Users/cindy.nguyen/Desktop/DCM/test.xlsx");    
      //com.mycompany.dcm_dfa.Creations.Placement.newPlacement("testeren", 20169557, 2068252, 120, 600, reporting, 0, 0, null, "");
      //com.mycompany.dcm_dfa.Creations.MyPlacement.setUpPlacementsFromIO(reporting, 20169557, "/Users/cindy.nguyen/Desktop/DCM/test.xlsx");
    /*Creation.createPlacement(PATH_TO_CLIENT_SECRETS, 0, 0, 0, 0, reporting, 0, 0);  
    Creation.createPlacement("XSG-116 160x600 Trade Signals SA Easier UF AU EN (Xaxis)", 20210631 , 2068252, 160, 600, reporting, 0, 0);
    Creation.createPlacement("XSG-116 300x600 Trade Signals SA Easier UF AU EN (Xaxis)", 20210631 , 2068252, 300, 600, reporting, 0, 0);
    Creation.createPlacement("XSG-116 300x250 Trade Signals SA Easier UF AU EN (Xaxis)", 20210631 , 2068252, 300, 250, reporting, 0, 0);
    Creation.createPlacement("XSG-116 728x90 Trade Signals SA Easier UF AU EN (Xaxis)", 20210631 , 2068252, 728, 90, reporting, 0, 0);*/
    //Creation.createPlacement("", 20169557, 1015597, 728, 90, reporting, (long) 1.5, (long) 9.48, "");
    //Reporting.runExample(reporting);
    //Creation.setUpAdsFromIO(reporting, "/Users/cindy.nguyen/Desktop/DCM/placementTest.xlsx");
    //Reporting.runExample(reporting);
    //Ad.newAd(reporting, "tester", 20169557 , 77640346, 204431113, "");
    //Ad.setUpAdsFromIO(reporting, "/Users/cindy.nguyen/Desktop/DCM/adTest.xlsx");
    //Reporting.tæææst(reporting);
    //Reporting.generateCreativeOverview(reporting);
    //MyPlacement.newPlacement("addValuetæst", 20169557, 3367768, 160, 600, reporting, 100, 10, "country=ee;language=en", "CPM", 19, 10, 2017);
    //MyPlacement.setUpPlacementsFromIO(reporting, 20169557, "/Users/cindy.nguyen/Desktop/DCM/test+customValues.xlsx");
      MyCreative.updateClickTagsForCreatives(reporting, "/Users/cindy.nguyen/Desktop/DCM/Creative Ids for clicktag update.xlsx");
//System.out.println(MyCreation.setEndDate(31, 12, 2018));
    
=======
            .setApplicationName("dfareporting")
            .build();           
>>>>>>> c527454bbdd70943c7b811b9bddb37a4bb4c28d6
  }
}
