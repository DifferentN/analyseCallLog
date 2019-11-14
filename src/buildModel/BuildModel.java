package buildModel;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import analyseMethodCall.MyMethod;
import graph.CommonBlockSequence;

public class BuildModel {
	private PreProcess preProcess ;
	public BuildModel() {
		preProcess = new PreProcess();
	}
	public List<String> reckonModelInstance(List<List<MyMethod>> target,List<String> userInput){
		//对调用序列进行预处理
		target = preProcess.preProcess(target,userInput);
		CommonBlockSequence common = new CommonBlockSequence();
		//获取多个调用序列实例的公共子集
		List<String> res = common.reckonCommonBlock(target);
		return res;
	}
	public List<String> reckonModel(List<String> userInput,List<String> instance){
		String item = null;
		for(int i=0;i<instance.size();i++) {
			item = instance.get(i);
			if(item.startsWith("{")) {
				JSONObject json = JSONObject.parseObject(item);
				if(json.getString("method").equals("setText")) {
					changeJSONParameter(json,userInput);
					instance.remove(i);
					instance.add(i, json.toJSONString());
				}
			}
		}
		for(String str:instance) {
			System.out.println(str);
		}
		return instance;
	}
	private void changeJSONParameter(JSONObject json, List<String> userInput) {
		
		for(int i=0;i<userInput.size();i++) {
			if(userInput.get(i).equals(json.getString("parameter"))) {
				json.put("parameter", ""+i);
			}
		}
		
	}
}
