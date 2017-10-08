package local.jp.jack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckOwnerController {
	
	static String FB_AUTH_KEY = "XXXXXXXXXXXXXXX";
	
	HashMap<Object,Object> accessTokenHashMap = new HashMap<>();
	String result = new String();

	DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
	
	@RequestMapping("/")
	public String index() {
		return "Try /facebook";
	}
	
	/*
	 * check the owner of access token
	 * If user_name is equal to name in Facebook API response, it returns true
	 */
	@RequestMapping("/facebook")
	public String checkFacebookTokenOwner(@RequestParam String access_token, String user_name ) throws Exception {
		/*
		 * Take care that Facebook API is slow or unstable sometime
		 */
		HttpGet httpGet = new HttpGet("https://graph.facebook.com/v2.10/me?access_token="+access_token);
		httpGet.addHeader("Authorization", FB_AUTH_KEY);
		
		/*
		 * This is a sample response of Facebook API
		 * {"name":"Taro Raku","id":"1234567890"}
		 */
		HttpResponse response = defaultHttpClient.execute(httpGet);
		System.out.println("status code="+response.getStatusLine().getStatusCode());
		
		if(accessTokenHashMap.get(access_token.hashCode())==null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    String line = null;
		    while ((line = reader.readLine()) != null) {
	           result = result + line  + "\n";
	        }
		    
		    System.out.println("Facebook API response="+result);
		    
		    accessTokenHashMap.put(access_token.hashCode(), result);
		}else {
			result = accessTokenHashMap.get(access_token.hashCode()).toString();
			System.out.println("Cache Hit="+result);
		}
	    
	    return "{ ownership:"+result.contains(user_name)+"}";
	}
}