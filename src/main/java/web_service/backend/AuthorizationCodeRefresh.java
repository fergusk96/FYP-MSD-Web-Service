package web_service.backend;


	

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AuthorizationCodeRefresh {
	private static final String refreshToken = "AQC096QcGKdUssK5sDL83bsu3pILBvmC7wTthfJiXDfGhDeHkEdxWxHkyygeyo0CSQoVP9d13VwXKd8PGmv10cvyGSjgPDB5VtQnO37md129bdYmWeFAMd5BgodBqri8Di0";
	private static final String clientId = "a13e6f026d0c494ca3ca5f25e74e0a6e";
	private static final String clientSecret = "484e8d2413554e96b7d8ae3813f5446c";
	
  private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
          .setClientId(clientId)
          .setClientSecret(clientSecret)
          .setRefreshToken(refreshToken)
          .build(); /*These three access tokens are retrieved by following steps 
  					  detailed in Spotify API documentation
  					  Each of these three values are constant and needed to 
  					  obtain access token which expires every hour */
  
  private static final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
          .build();

  public static String authorizationCodeRefresh_Sync() {
    try {
      final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();   
      
      String accessToken = authorizationCodeCredentials.getAccessToken(); /*Retrieves access token required to 
      															make queries to Spotify web service*/
      return accessToken;
      
    } catch (IOException | SpotifyWebApiException e) {
     return	("Error: " + e.getMessage());
      
    }
  }


}
