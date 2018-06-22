package com.lx.fuzzytaskunit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.http.auth.NTCredentials;
import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.aip.nlp.AipNlp;

import jdk.internal.org.objectweb.asm.tree.IntInsnNode;
import smile.Network;
import smile.SMILEException;


/**
 * 自然语言-》模糊任务-》精确任务
 * @author Lx
 *
 */
public class FuzzyTaskModel{
	// 设置APPID/AK/SK
	public static final String APP_ID = "11042637";
	public static final String API_KEY = "Z8VcruPzKGgwpG89z6ixejDN";
	public static final String SECRET_KEY = "5fp4PfVeyX4YMaN4I2KKg3dtM14E2T7X";
	public static FuzzyTaskInput ftInput = new FuzzyTaskInput();// 存储从自然语言解析的模糊任务
	public static FuzzyTaskOutput ftOutput = new FuzzyTaskOutput();// 存储从自然语言解析的模糊任务
	/**
	 * 构造函数
	 * @param Text
	 * @throws IOException
	 */
	public FuzzyTaskModel(String Text) throws IOException {
		// 初始化一个AipNlp
		AipNlp client = new AipNlp(APP_ID, API_KEY, SECRET_KEY);
		// 可选：设置网络连接参数
		client.setConnectionTimeoutInMillis(2000);
		client.setSocketTimeoutInMillis(60000);
		// 可选：设置log4j日志输出格式，若不设置，则使用默认配置
		// 也可以直接通过jvm启动参数设置此环境变量
		System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

		NLPTest.NLtoFuzzyTask(Text,client,ftInput);
		BNTFuzzyTaskModel(ftInput,ftOutput);
		double []spaceBoundery=new double[4];
		GoogleNametoLocation(ftInput.getObservedspace(),spaceBoundery);
		ftOutput.setSpaceBoundery(spaceBoundery);
		//System.out.print(ftOutput.getSpaceBoundery()[1]);
	}
	/**
	 * 返回word1和word2的词义相似度
	 * @param word1
	 * @param word2
	 * @param client
	 * @return
	 */
	public static double textSim(String word1, String word2, AipNlp client) {
		// 传入可选参数调用接口
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put("model", "CNN");
		// 短文本相似度
		JSONObject res = client.simnet(word1, word2, options);
		System.out.print("textSim("+word1+","+word2+")=");
		double score = (double) res.getDouble("score");
		System.out.print(score+"\n");
		return score;
	}

	/**
	 * 自然语言处理函数，分词-》语义相似度匹配-》模糊任务
	 * @param nltext
	 * @param client
	 * @param ftInput
	 */
	public static void NLtoFuzzyTask(String nltext,AipNlp client,FuzzyTaskInput ftInput) {
		JSONObject res = client.lexer(nltext, null);
		System.out.println(res.toString(2));
		JSONArray items = res.getJSONArray("items");
		for (int i = 0; i < items.length(); i++) {
			JSONObject item = (JSONObject) items.get(i);
			if (item.getString("ne").equals("LOC")) {// 获取地理位置信息
				ftInput.setObservedspace(item.getString("item"));
				System.out.println("LOC:" + item.getString("item"));
			}
			if (item.getString("ne").equals("TIME")||item.getString("pos").equals("t")) {// 获取时间尺度信息
				String ObservedTimeScaleValue="";double tempval=0;
				for(int j=0;j<FuzzyTaskModelStaticObject.ObservedTimeScale.length;j++) {
					double tempval1 = textSim(FuzzyTaskModelStaticObject.ObservedTimeScale[j], item.getString("item"), client) ;
					System.out.println("tempval1:"+tempval1);
					if (tempval1> 0.5&& tempval1> tempval) {
						tempval=tempval1;
						ObservedTimeScaleValue= FuzzyTaskModelStaticObject.ObservedTimeScaleValue[j];						
					}
				}
				if(!ObservedTimeScaleValue.equals("")) {
					System.out.println("ObservedTimeScale:"+ObservedTimeScaleValue+"="+tempval);
					ftInput.setObservedTimeScale(ObservedTimeScaleValue);
				}
			}
			if (item.getString("pos").equals("n")) {
				String[] nodeNameAndValue = { "", "" };
				getNodeTypeandValue(item.getString("item"), client, nodeNameAndValue);
				System.out.println(nodeNameAndValue[0] + "," + nodeNameAndValue[1]);
				if(nodeNameAndValue[0].equals("FloodType")) {
					ftInput.setFloodType(nodeNameAndValue[1]);
				}
				if(nodeNameAndValue[0].equals("FloodStage")) {
					ftInput.setFloodStage(nodeNameAndValue[1]);
				}
				if(nodeNameAndValue[0].equals("FloodResponseLevel")) {
					ftInput.setFloodResponseLevel(nodeNameAndValue[1]);
				}
				if(nodeNameAndValue[0].equals("Topography")) {
					ftInput.setTopography(nodeNameAndValue[1]);
				}

			}
		}
	}

	/**
	 * 判断输入item属于哪一节点并返回相似度最高的节点值
	 * @param item
	 * @param client
	 * @param nodeNameAndValue
	 */
	public static void getNodeTypeandValue(String item, AipNlp client, String[] nodeNameAndValue) {
		double tempval = 0;
		for (int j = 0; j < 6; j++) {
			double tempval1= textSim(FuzzyTaskModelStaticObject.floodType[j], item, client);
			if (tempval1> 0.5&& tempval1> tempval) {
				nodeNameAndValue[0] = "FloodType";
				nodeNameAndValue[1] = FuzzyTaskModelStaticObject.floodTypeValue[j];
				tempval=tempval1;
				//System.out.println(nodeNameAndValue[0]+":"+nodeNameAndValue[1]+"="+tempval);
			}
		}
		tempval = 0;
		for (int j = 0; j < 4; j++) {
			double tempval1 = textSim(FuzzyTaskModelStaticObject.floodStage[j], item, client);
			if (tempval1> 0.5&& tempval1> tempval) {
				nodeNameAndValue[0] = "FloodStage";
				nodeNameAndValue[1] = FuzzyTaskModelStaticObject.floodStageValue[j];
				tempval=tempval1;
				//System.out.println(nodeNameAndValue[0]+":"+nodeNameAndValue[1]+"="+tempval);
			}

		}
		tempval = 0;
		for (int j = 0; j < 5; j++) {
			double tempval1 = textSim(FuzzyTaskModelStaticObject.floodResponseLevel[j], item, client);
			if (tempval1> 0.5&& tempval1> tempval) {
				nodeNameAndValue[0] = "floodResponseLevel";
				nodeNameAndValue[1] = FuzzyTaskModelStaticObject.floodResponseLevelValue[j];
				tempval=tempval1;
				//System.out.println(nodeNameAndValue[0]+":"+nodeNameAndValue[1]+"="+tempval);
			}

		}
		tempval = 0;
		for (int j = 0; j < 4; j++) {
			double tempval1 = textSim(FuzzyTaskModelStaticObject.Topography[j], item, client);
			if (tempval1> 0.5&& tempval1> tempval) {
				nodeNameAndValue[0] = "Topography";
				nodeNameAndValue[1] = FuzzyTaskModelStaticObject.TopographyValue[j];
				tempval=tempval1;
				//System.out.println(nodeNameAndValue[0]+":"+nodeNameAndValue[1]+"="+tempval);
			}

		}
	}

	/**
	 * 贝叶斯网络推理模型实现
	 * @param ftInput
	 * @param ftOutput
	 */
	public static void BNTFuzzyTaskModel(FuzzyTaskInput ftInput,FuzzyTaskOutput ftOutput) {
		try{
			new smile.License(

					"SMILE LICENSE 19a56b7c 42d4a244 2e297190 " +
							"THIS IS AN ACADEMIC LICENSE AND CAN BE USED " +
							"SOLELY FOR ACADEMIC RESEARCH AND TEACHING, " +
							"AS DEFINED IN THE BAYESFUSION ACADEMIC " +
							"SOFTWARE LICENSING AGREEMENT. " +
							"Serial #: 4q3gf60v0ja5hobk3svppgr8b " +
							"Issued for: Lin xin (papaya961128@gmail.com) " +
							"Academic institution: China University of Geography " +
							"Valid until: 2018-10-20 " +
							"Issued by BayesFusion activation server",
							new byte[] {
									24,127,-18,-22,106,104,70,-11,71,-92,-128,26,3,-46,-25,-50,
									17,-14,-39,112,75,55,8,-81,63,101,-108,-21,-14,110,-83,38,
									12,74,-68,57,-58,27,87,51,-84,-91,12,69,-36,-59,32,43,
									35,-32,11,0,-10,-17,-13,91,23,-40,6,65,-85,23,116,49
					}
					); 
			Network net = new Network();
			net.readFile("FuzzyTaskModel.xdsl");
			net.clearAllEvidence();
			net.updateBeliefs();
			net.setEvidence("SolarRadiation", ftInput.getSolarRadiation());
			net.setEvidence("CloudCover", ftInput.getCloudCover());
			net.setEvidence("Topography", ftInput.getTopography());
			net.setEvidence("FloodResponseLevel", ftInput.getFloodResponseLevel());
			net.setEvidence("FloodStage", ftInput.getFloodStage());
			net.setEvidence("FloodType", ftInput.getFloodType());
			net.updateBeliefs();
			ftOutput.setRadiationResolution(net.getNodeValue("RadiationResolution"));
			ftOutput.setAccurateObservedTimeScale(net.getNodeValue("AccurateObservedTimeScale"));
			ftOutput.setAccurateObservedSpaceScale(net.getNodeValue("AccurateObservedSpaceScale"));
			ftOutput.setRevisitCycle(net.getNodeValue("RevisitCycle"));
			ftOutput.setSpaceResolution(net.getNodeValue("SpaceResolution"));
			ftOutput.setBandType(net.getNodeValue("BandType"));		 
			//打印贝叶斯网络推理结果
			printFuzzyTaskOutput(ftOutput);

		}
		catch (SMILEException e) {
			System.out.println(e.getMessage()); 
		}

	}
	/**
	 * 打印BN结果
	 * @param fTaskOutput
	 */
	public static void printFuzzyTaskOutput(FuzzyTaskOutput fTaskOutput) {
		System.out.println("value of RadiationResolution:");
		for(int i=0;i<fTaskOutput.getRadiationResolution().length;i++) {
			System.out.print(fTaskOutput.getRadiationResolution()[i]+",");
		}
		System.out.print(";"+"\n");

		System.out.println("value of RevisitCycle:");
		for(int i=0;i<fTaskOutput.getRevisitCycle().length;i++) {
			System.out.print(fTaskOutput.getRevisitCycle()[i]+",");
		}
		System.out.print(";"+"\n");
		System.out.println("value of SpaceResolution:");
		for(int i=0;i<fTaskOutput.getSpaceResolution().length;i++) {
			System.out.print(fTaskOutput.getSpaceResolution()[i]+",");
		}
		System.out.print(";"+"\n");
		System.out.println("value of BandType:");
		for(int i=0;i<fTaskOutput.getBandType().length;i++) {
			System.out.print(fTaskOutput.getBandType()[i]+",");
		}
		System.out.print(";"+"\n");
		System.out.println("value of AccurateObservedTimeScale:");
		for(int i=0;i<fTaskOutput.getAccurateObservedTimeScale().length;i++) {
			System.out.print(fTaskOutput.getAccurateObservedTimeScale()[i]+",");
		}
		System.out.print(";"+"\n");
		System.out.println("value of AccurateObservedSpaceScale:");
		for(int i=0;i<fTaskOutput.getAccurateObservedSpaceScale().length;i++) {
			System.out.print(fTaskOutput.getAccurateObservedSpaceScale()[i]+",");
		}
		System.out.print(";"+"\n");
	}

	/**
	 * Google API 获取地址的边界信息
	 * @param address
	 * @param boundery
	 * @throws IOException
	 */
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
				//深复制
				for(int i=0;i<boun.length;i++) {
					boundery[i]=boun[i];
				}
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
	
	
	public static void main(String []agres) throws IOException {
		FuzzyTaskModel fTaskModel=new FuzzyTaskModel("监测武汉城市内涝");
		System.out.println(x);
	}
}
