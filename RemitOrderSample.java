
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import javax.net.ssl.HttpsURLConnection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

public class RemitOrderSample {

	private String ACCESS_KEY="<YOUR PAYMENT ACCESS KEY>";
	private String SECRET_KEY="<YOUR PAYMENT SECRET KEY>";
	private String apiUrl="https://api.btcchina.com/api.php/payment"; 
	
	private static String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	private static Logger logger = Logger.getLogger(RemitOrderSample.class.getName());
	
	public static void main(String[] args) {
		RemitOrderSample sample = new RemitOrderSample();
		try{
			sample.getRemitQuote(20, "CNY");
			
			sample.createRemitOrder(2, "CNY", "13500000000", "CN");
//			sample.createRemitOrder(200, "CNY", "ARemitOrderSample@btcchina.com");
			
//			sample.getRemitOrder(<YOUR REMIT ORDER ID>);
			sample.getRemitOrder();
			
		} catch ( Exception e){
			logger.log(Level.SEVERE, "Exception found!! : {0}", e.toString());
			return;
		}
	}
	
	public static String getSignature(String data,String key) throws Exception {
		// get an hmac_sha1 key from the raw key bytes
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
		// get an hmac_sha1 Mac instance and initialize with the signing key
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);
		// compute the hmac on input data bytes
		byte[] rawHmac = mac.doFinal(data.getBytes());
		return bytArrayToHex(rawHmac);
	}
 
	private static String bytArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder();
		for(byte b: a)
			sb.append(String.format("%02x", b&0xff));
		return sb.toString();
	}

	private static String removeZero(String s){  
        if(s.indexOf(".") > 0){  
            s = s.replaceAll("0+?$", "");//remove redundant 0  
            s = s.replaceAll("[.]$", "");//if the last is ., remove  
        }  
        return s;  
	}
	
	public String sha1(String s) throws Exception{
	    MessageDigest sha1 = MessageDigest.getInstance("SHA1");
	    sha1.update(s.getBytes());
        byte[] hash = sha1.digest();
        return bytArrayToHex(hash);
	}
	
	public String getRemitQuote(double amount, String currency) throws Exception{

		String tonce = ""+(System.currentTimeMillis() * 1000);
		
		BigDecimal tmp = new BigDecimal(new Double(amount).toString());	
		String param_price = removeZero(tmp.toPlainString());
		
		String params = "tonce="+tonce.toString()+"&accesskey="+ACCESS_KEY+"&requestmethod=post&id=1&method=getRemitQuote&params="+param_price+","+currency+""; //
		String hash = getSignature(params, SECRET_KEY);
		 
		System.out.println("Params: "+ params);
		String userpass = ACCESS_KEY + ":" + hash;
		String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());
		
		URL obj = new URL(apiUrl);
	    HttpsURLConnection httpsConn = (HttpsURLConnection)obj.openConnection();;
	    
	    //add reuqest header
	    httpsConn.setRequestMethod("POST");
	    httpsConn.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
	    httpsConn.setRequestProperty ("Authorization", basicAuth);
	 
		String postdata = "{\"method\": \"getRemitQuote\", \"params\": ["+param_price+",\""+currency+"\"], \"id\": 1}";
		System.out.println("Postdata: "+ postdata);

		// Send post request
		httpsConn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(httpsConn.getOutputStream());
		wr.writeBytes(postdata);
		wr.flush();
		wr.close();
	 
		logger.log(Level.INFO, "Post parameters : {0}", params);
		logger.log(Level.INFO, "Post data : {0}", postdata);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
		
		String inputLine;
		StringBuffer response = new StringBuffer();
	
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
	
		logger.log(Level.WARNING, "Response : {0}", response.toString());

		return response.toString();
	}

	public String createRemitOrder(double amount, String currency, String receiver, String countrycode) throws Exception{

		String tonce = ""+(System.currentTimeMillis() * 1000);
		
		BigDecimal tmp = new BigDecimal(new Double(amount).toString());	
		String param_price = removeZero(tmp.toPlainString());
		
		String params = "tonce="+tonce.toString()+"&accesskey="+ACCESS_KEY+"&requestmethod=post&id=1&method=createRemitOrder&params="+param_price+","+currency+","+receiver+","+countrycode; //
		String hash = getSignature(params, SECRET_KEY);
		 
		System.out.println("Params: "+ params);
		String userpass = ACCESS_KEY + ":" + hash;
		String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());
		
		URL obj = new URL(apiUrl);
	    HttpsURLConnection httpsConn = (HttpsURLConnection)obj.openConnection();;
	    
	    //add reuqest header
	    httpsConn.setRequestMethod("POST");
	    httpsConn.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
	    httpsConn.setRequestProperty ("Authorization", basicAuth);
	 
		String postdata = "{\"method\": \"createRemitOrder\", \"params\": ["+param_price+",\""+currency+"\",\""+receiver+"\",\""+countrycode+"\"], \"id\": 1}";
		System.out.println("Postdata: "+ postdata);

		// Send post request
		httpsConn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(httpsConn.getOutputStream());
		wr.writeBytes(postdata);
		wr.flush();
		wr.close();
	 
		logger.log(Level.INFO, "Post parameters : {0}", params);
		logger.log(Level.INFO, "Post data : {0}", postdata);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
		
		String inputLine;
		StringBuffer response = new StringBuffer();
	
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
	
		logger.log(Level.WARNING, "Response : {0}", response.toString());

		return response.toString();
	}

	public String createRemitOrder(double amount, String currency, String receiver) throws Exception{

		String tonce = ""+(System.currentTimeMillis() * 1000);
		
		BigDecimal tmp = new BigDecimal(new Double(amount).toString());	
		String param_price = removeZero(tmp.toPlainString());
		
		String params = "tonce="+tonce.toString()+"&accesskey="+ACCESS_KEY+"&requestmethod=post&id=1&method=createRemitOrder&params="+param_price+","+currency+","+receiver; //
		String hash = getSignature(params, SECRET_KEY);
		 
		System.out.println("Params: "+ params);
		String userpass = ACCESS_KEY + ":" + hash;
		String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());
		
		URL obj = new URL(apiUrl);
	    HttpsURLConnection httpsConn = (HttpsURLConnection)obj.openConnection();;
	    
	    //add reuqest header
	    httpsConn.setRequestMethod("POST");
	    httpsConn.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
	    httpsConn.setRequestProperty ("Authorization", basicAuth);
	 
		String postdata = "{\"method\": \"createRemitOrder\", \"params\": ["+param_price+",\""+currency+"\",\""+receiver+"\"], \"id\": 1}";
		System.out.println("Postdata: "+ postdata);

		// Send post request
		httpsConn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(httpsConn.getOutputStream());
		wr.writeBytes(postdata);
		wr.flush();
		wr.close();
	 
		logger.log(Level.INFO, "Post parameters : {0}", params);
		logger.log(Level.INFO, "Post data : {0}", postdata);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
		
		String inputLine;
		StringBuffer response = new StringBuffer();
	
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
	
		logger.log(Level.WARNING, "Response : {0}", response.toString());

		return response.toString();
	}

	
	public String getRemitOrder(int order_id) throws Exception{

		String tonce = ""+(System.currentTimeMillis() * 1000);
		
		String params = "tonce="+tonce.toString()+"&accesskey="+ACCESS_KEY+"&requestmethod=post&id=1&method=getRemitOrder&params="+order_id; //
		String hash = getSignature(params, SECRET_KEY);
		 
		System.out.println("Params: "+ params);
		String userpass = ACCESS_KEY + ":" + hash;
		String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());
		
		URL obj = new URL(apiUrl);
	    HttpsURLConnection httpsConn = (HttpsURLConnection)obj.openConnection();;
	    
	    //add reuqest header
	    httpsConn.setRequestMethod("POST");
	    httpsConn.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
	    httpsConn.setRequestProperty ("Authorization", basicAuth);
	 
		String postdata = "{\"method\": \"getRemitOrder\", \"params\": ["+order_id+"], \"id\": 1}";
		System.out.println("Postdata: "+ postdata);

		// Send post request
		httpsConn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(httpsConn.getOutputStream());
		wr.writeBytes(postdata);
		wr.flush();
		wr.close();
	 
		logger.log(Level.INFO, "Post parameters : {0}", params);
		logger.log(Level.INFO, "Post data : {0}", postdata);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
		
		String inputLine;
		StringBuffer response = new StringBuffer();
	
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
	
		logger.log(Level.WARNING, "Response : {0}", response.toString());

		return response.toString();
	}

	public String getRemitOrder() throws Exception{

		String tonce = ""+(System.currentTimeMillis() * 1000);
		
		String params = "tonce="+tonce.toString()+"&accesskey="+ACCESS_KEY+"&requestmethod=post&id=1&method=getRemitOrder&params="; //
		String hash = getSignature(params, SECRET_KEY);
		 
		System.out.println("Params: "+ params);
		String userpass = ACCESS_KEY + ":" + hash;
		String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());
		
		URL obj = new URL(apiUrl);
	    HttpsURLConnection httpsConn = (HttpsURLConnection)obj.openConnection();;
	    
	    //add reuqest header
	    httpsConn.setRequestMethod("POST");
	    httpsConn.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
	    httpsConn.setRequestProperty ("Authorization", basicAuth);
	 
		String postdata = "{\"method\": \"getRemitOrder\", \"params\": [], \"id\": 1}";
		System.out.println("Postdata: "+ postdata);

		// Send post request
		httpsConn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(httpsConn.getOutputStream());
		wr.writeBytes(postdata);
		wr.flush();
		wr.close();
	 
		logger.log(Level.INFO, "Post parameters : {0}", params);
		logger.log(Level.INFO, "Post data : {0}", postdata);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
		
		String inputLine;
		StringBuffer response = new StringBuffer();
	
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
	
		logger.log(Level.WARNING, "Response : {0}", response.toString());

		return response.toString();
	}

}
