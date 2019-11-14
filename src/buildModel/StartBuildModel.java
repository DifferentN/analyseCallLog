package buildModel;

import java.util.ArrayList;
import java.util.List;

import FileUtil.MyFileWriter;
import analyseMethodCall.MethodSequenceUtil;
import analyseMethodCall.MyMethod;
import com.alibaba.fastjson.JSONArray;
import eventSimilarity.Event;
import eventSimilarity.ProcessEventUtil;

public class StartBuildModel {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        useBuildModel2();
	}
	private static void useBuildModel(){
		//读取指定文件中的打印序列
		List<MyMethod> callSeq1 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/anki/methodLog-1.txt");
		List<MyMethod> callSeq2 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/anki/methodLog-2.txt");
		List<MyMethod> callSeq3 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/anki/methodLog-3.txt");

		List<List<MyMethod>> src = new ArrayList<>();
		src.add(callSeq1);
		src.add(callSeq2);
		src.add(callSeq3);

		//模拟用户的输入
		List<String> userInput = new ArrayList<>();
		userInput.add("哪吒");
		userInput.add("锅盔");
		BuildModel buildModel = new BuildModel();
		//获取API的模板
		List<String> instance = buildModel.reckonModelInstance(src,userInput);
		//获取API模板与用户输入的联系
		buildModel.reckonModel(userInput, instance);
	}
	private static void useBuildModel2(){
        //读取指定文件中的打印序列
        List<MyMethod> callSeq1 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/3APP/douban/searchMovie/methodLog-1.txt");
        List<MyMethod> callSeq2 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/3APP/douban/searchMovie/methodLog-2.txt");
        List<MyMethod> callSeq3 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/3APP/douban/searchMovie/methodLog-3.txt");
//        List<MyMethod> callSeq4 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/3APP/qqmusic/searchMusic/methodLog-4.txt");
//        List<MyMethod> callSeq5 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/3APP/douban/searchMovie/methodLog-5.txt");
//        List<MyMethod> callSeq6 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/3APP/douban/searchMovie/methodLog-6.txt");
//        List<MyMethod> callSeq7 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/3APP/douban/searchMovie/methodLog-7.txt");
        BuildModel2 buildModel2 = new BuildModel2();
		ArrayList<String> userInput3 = new ArrayList<>();
		userInput3.add("钢铁侠");//钢铁侠  吴亦凡 汤圆
		buildModel2.addCallLogData(callSeq3,userInput3);
        ArrayList<String> userInput1 = new ArrayList<>();
        userInput1.add("中国机长");//中国机长  李白 锅盔
        buildModel2.addCallLogData(callSeq1,userInput1);

        ArrayList<String> userInput2 = new ArrayList<>();
        userInput2.add("少年的你");//少年的你  李荣浩 面条
        buildModel2.addCallLogData(callSeq2,userInput2);


//        ArrayList<String> userInput4 = new ArrayList<>();
//        userInput4.add("丑八怪");//海王
//        buildModel2.addCallLogData(callSeq4,userInput4);
//
//        ArrayList<String> userInput5 = new ArrayList<>();
//        userInput5.add("罗小黑战记");
//        buildModel2.addCallLogData(callSeq5,userInput5);

//        ArrayList<String> userInput6 = new ArrayList<>();
//        userInput6.add("黑豹");
//        buildModel2.addCallLogData(callSeq6,userInput6);
        List<Event> modelEvents = buildModel2.obtainModel();

        JSONArray jsonArray = ProcessEventUtil.transformAPIEventsToJSONArray(modelEvents);
        MyFileWriter.writeEventJSONArray("execute.txt",jsonArray);
	}

}
