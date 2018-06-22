package com.nlp.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;
import com.sun.net.httpserver.HttpExchange;





public class NameToLocation {
	
static class  MyAuthenticator extends Authenticator {
	    private String user = "";
	    private String password = "";
	    public MyAuthenticator(String user, String password) {
	        this.user = user;
	        this.password = password;
	    }
	    protected PasswordAuthentication getPasswordAuthentication() {
	        return new PasswordAuthentication(user, password.toCharArray());
	    }
		public Result authenticate(HttpExchange arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	public static void Test() {
		 try {
	            URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&sensor=false&key=AIzaSyBynH1men1jg9dmNTEBwP6o8H1qzSrzG8s");
	            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
	            InputStream inputStream = httpURLConnection.getInputStream();
	            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	            String string = "";
	            String currentString;
	            while((currentString = bufferedReader.readLine())!=null)
	            {
	                string+=currentString;
	            }
	            JSONObject jsonObject = new JSONObject(string);
	            JSONArray resultsArray = jsonObject.getJSONArray("results");
	            for(int i = 0; i < resultsArray.length(); ++i)
	            {
	                JSONArray addressArray = resultsArray.getJSONObject(i).getJSONArray("address_components");
	                for(int l = 0; l < addressArray.length(); l++)
	                {
	                    JSONObject addressObject = addressArray.getJSONObject(l);
	                    String long_name = addressObject.getString("long_name");
	                    System.out.print("long_name: ");
	                    System.out.println(long_name);
	                    JSONArray typesArray = addressObject.getJSONArray("types");
	                    for(int j = 0; j < typesArray.length(); ++j)
	                    {
	                        System.out.print("types: ");
	                        System.out.println(typesArray.getString(j));
	                    }
	                    String short_name = addressObject.getString("short_name");
	                    System.out.print("short_name: ");
	                    System.out.println(short_name);
	                }
	                String formattedString = resultsArray.getJSONObject(i).getString("formatted_address");
	                System.out.print("formatted_address: ");
	                System.out.println(formattedString);
	                JSONObject geometryObject = resultsArray.getJSONObject(i).getJSONObject("geometry");
	                JSONObject locationObject = geometryObject.getJSONObject("location");
	                System.out.print("lat: ");
	                System.out.println(locationObject.getDouble("lat"));
	                System.out.print("lng: ");
	                System.out.println(locationObject.getDouble("lng"));
	                System.out.print("location_type: ");
	                System.out.println(geometryObject.getString("location_type"));
	                JSONObject viewportObject = geometryObject.getJSONObject("viewport");
	                JSONObject northeastObject = viewportObject.getJSONObject("northeast");
	                System.out.print("northeast lat: ");
	                System.out.println(northeastObject.getDouble("lat"));
	                System.out.print("northeast lng: ");
	                System.out.println(northeastObject.getDouble("lng"));
	                JSONObject southwestObject = viewportObject.getJSONObject("southwest");
	                System.out.print("southwest lat: ");
	                System.out.println(southwestObject.getDouble("lat"));
	                System.out.print("southwest lng: ");
	                System.out.println(southwestObject.getDouble("lng"));
	                JSONArray typesArray = resultsArray.getJSONObject(i).getJSONArray("types");
	                for(int k = 0; k < typesArray.length(); k++)
	                {
	                    System.out.print("types: ");
	                    System.out.println(typesArray.getString(i));
	                }
	            }
	            System.out.print("status: ");
	            System.out.println(jsonObject.getString("status"));
	            
	        } catch (JSONException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	    }


	public static void GoogleNametoLocation(String address, double[] boundery) throws IOException {
//		MyAuthenticator authenticator=new MyAuthenticator("root","lx1019424650");
//		Authenticator.setDefault(authenticator);
//        SocketAddress sa = new InetSocketAddress("144.202.40.246", 8388);
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, sa);

		// "https://maps.googleapis.com/maps/api/geocode/json?address="+name+"&key=AIzaSyC_mia5_dX4mblw-u9ASuTOyYTSYtHR440";
		String key = "AIzaSyBynH1men1jg9dmNTEBwP6o8H1qzSrzG8s";
		address = URLEncoder.encode(address , "UTF-8");
		String url = String.format("https://ditu.google.cn/maps/api/geocode/json?address=%s&key=%s", address, key);
		//String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s", address, key);
		URL myURL = null;
		URLConnection httpsConn = null;
		try {
			myURL = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		InputStreamReader insr = null;
		BufferedReader br = null;
		try {
			httpsConn = (URLConnection) myURL.openConnection();// 使用代理
			if (httpsConn != null) {
				insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
				br = new BufferedReader(insr);
				String line;
				String result = "";
				while ((line = br.readLine()) != null) {
					result += line;
				}
				br.close();
				JSONObject jsonObject = new JSONObject(result);
				JSONObject resultsArray = jsonObject.getJSONArray("results").getJSONObject(0);
				JSONObject geometry=resultsArray.getJSONObject("geometry");
	            JSONObject bounds=geometry.getJSONObject("bounds");
	            JSONObject southwest=bounds.getJSONObject("southwest");//左下角
	            JSONObject northeast=bounds.getJSONObject("northeast");//右上角
	            double [] boun= {southwest.getDouble("lng"),southwest.getDouble("lat"),northeast.getDouble("lng"),northeast.getDouble("lat")};
	            boundery=boun;
	            System.out.println(bounds.toString(1));

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (insr != null) {
				insr.close();
			}
			if (br != null) {
				br.close();
			}
		}
	}

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		double[] bounds = new double[4];
		GoogleNametoLocation("武汉", bounds);
		//Test();
	}

}
