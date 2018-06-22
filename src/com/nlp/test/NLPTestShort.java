package com.nlp.test;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.aip.nlp.AipNlp;
import com.lx.fuzzytaskunit.FuzzyTaskInput;
import com.lx.fuzzytaskunit.FuzzyTaskModelStaticObject;

public class NLPTestShort {

	// 设置APPID/AK/SK
	public static final String APP_ID = "11042637";
	public static final String API_KEY = "Z8VcruPzKGgwpG89z6ixejDN";
	public static final String SECRET_KEY = "5fp4PfVeyX4YMaN4I2KKg3dtM14E2T7X";

	public static double textSim(String word1, String word2, AipNlp client) {
		// 传入可选参数调用接口
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put("model", "CNN");

		// 短文本相似度
		JSONObject res = client.simnet(word1, word2, options);
		System.out.print(word1+","+word2+"=");
		double score = (double) res.getDouble("score");
		System.out.print(score+"\n");
		return score;
	}

	// 自然语言处理函数，分词-》语义相似度匹配-》模糊任务
	public static void NLtoFuzzyTask(String nltext) {

	}

	// 判断名词属于哪一节点并返回相似度最高的节点值
	public static void getNodeTypeandValue(String item, AipNlp client, String[] nodeNameAndValue) {
		double tempval = 0;
		for (int j = 0; j < 6; j++) {
			if (textSim(FuzzyTaskModelStaticObject.floodType[j], item, client) > 0.5
					&& textSim(FuzzyTaskModelStaticObject.floodType[j], item, client) > tempval) {
				nodeNameAndValue[0] = "FloodType";
				nodeNameAndValue[1] = FuzzyTaskModelStaticObject.floodTypeValue[j];
				tempval = textSim(FuzzyTaskModelStaticObject.floodType[j], item, client);
				System.out.println(nodeNameAndValue[0]+":"+nodeNameAndValue[1]+"="+tempval);
			}
		}
		for (int j = 0; j < 4; j++) {
			if (textSim(FuzzyTaskModelStaticObject.floodStage[j], item, client) > 0.5
					&& textSim(FuzzyTaskModelStaticObject.floodStage[j], item, client) > tempval) {
				nodeNameAndValue[0] = "FloodStage";
				nodeNameAndValue[1] = FuzzyTaskModelStaticObject.floodStageValue[j];
				tempval = textSim(FuzzyTaskModelStaticObject.floodStage[j], item, client);
				System.out.println(nodeNameAndValue[0]+":"+nodeNameAndValue[1]+"="+tempval);
			}

		}
		for (int j = 0; j < 5; j++) {
			if (textSim(FuzzyTaskModelStaticObject.floodResponseLevel[j], item, client) > 0.5
					&& textSim(FuzzyTaskModelStaticObject.floodResponseLevel[j], item, client) > tempval) {
				nodeNameAndValue[0] = "floodResponseLevel";
				nodeNameAndValue[1] = FuzzyTaskModelStaticObject.floodResponseLevelValue[j];
				tempval = textSim(FuzzyTaskModelStaticObject.floodResponseLevel[j], item, client);
				System.out.println(nodeNameAndValue[0]+":"+nodeNameAndValue[1]+"="+tempval);
			}

		}
	}

	public static void main(String[] args) {
		// 初始化一个AipNlp
		AipNlp client = new AipNlp(APP_ID, API_KEY, SECRET_KEY);

		// 可选：设置网络连接参数
		client.setConnectionTimeoutInMillis(2000);
		client.setSocketTimeoutInMillis(60000);

		// // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
		// client.setHttpProxy("proxy_host", proxy_port); // 设置http代理
		// client.setSocketProxy("proxy_host", proxy_port); // 设置socket代理

		// 可选：设置log4j日志输出格式，若不设置，则使用默认配置
		// 也可以直接通过jvm启动参数设置此环境变量
		System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

		// 调用接口
		String text = "监测今周武汉城市内涝";
		FuzzyTaskInput ftInput = new FuzzyTaskInput();// 存储从自然语言解析的模糊任务
		JSONObject res = client.lexer(text, null);
		System.out.println(res.toString(2));
		JSONArray items = res.getJSONArray("items");
		for (int i = 0; i < items.length(); i++) {
			JSONObject item = (JSONObject) items.get(i);
//			if (item.getString("ne").equals("LOC")) {// 获取地理位置信息
//				ftInput.setObservedspace(item.getString("item"));
//				System.out.println("LOC:" + item.getString("item"));
//			}
//			if (item.getString("ne").equals("TIME")) {// 获取地理位置信息
//				String ObservedTimeScaleValue="";double tempval=0;
//				for(int j=0;j<FuzzyTaskModelStaticObject.ObservedTimeScale.length;j++) {
//					double tempval1 = textSim(FuzzyTaskModelStaticObject.ObservedTimeScale[j], item.getString("item"), client) ;
//					System.out.println("tempval1:"+tempval1);
//					if (tempval1> 0.5&& tempval1> tempval) {
//						tempval=tempval1;
//						ObservedTimeScaleValue= FuzzyTaskModelStaticObject.ObservedTimeScaleValue[j];
//						System.out.println("ObservedTimeScale:"+ObservedTimeScaleValue+"="+tempval);
//					}
//				}
//			}
			if (item.getString("pos").equals("n")) {
				String[] nodeNameAndValue = { "", "" };
				textSim("洪涝类型", item.getString("item"), client);
				textSim("洪涝观测阶段", item.getString("item"), client);
				textSim("洪涝响应级别", item.getString("item"), client);
				//System.out.println(nodeNameAndValue[0] + "," + nodeNameAndValue[1]);
			}
		}
	}

}
