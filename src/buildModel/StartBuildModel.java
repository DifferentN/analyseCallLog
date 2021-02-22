package buildModel;

import java.util.ArrayList;
import java.util.List;

import FileUtil.MyFileUtil;
import analyseMethodCall.MethodSequenceUtil;
import analyseMethodCall.MyMethod;
import com.alibaba.fastjson.JSONArray;
import eventSimilarity.Event;
import eventSimilarity.GenerateEventUtil;
import eventSimilarity.MyParameter;
import eventSimilarity.ProcessEventUtil;

public class StartBuildModel {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        useBuildModel2(new String[0]);
	}
	public List<Event> generateAPI(List<String> instancePaths,List<List<String>> userInputs){
	    BuildModel2 buildModel2 = new BuildModel2();
	    int len = instancePaths.size();
	    for(int i=0;i<len;i++){
	        List<MyMethod> callSeq = MethodSequenceUtil.getSequence(instancePaths.get(i));
	        List<String> userInput = userInputs.get(i);
	        buildModel2.addCallLogData(callSeq,userInput);
        }
	    List<Event> modelEvents = buildModel2.obtainModel();
        return modelEvents;
    }

	private static void useBuildModel2(String userData[]){
        //读取指定文件中的打印序列
        List<MyMethod> callSeq1 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/exprience/musicplayer/playMusic/methodLog-1.txt");
        List<MyMethod> callSeq2 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/exprience/musicplayer/playMusic/methodLog-2.txt");
        List<MyMethod> callSeq3 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/exprience/musicplayer/playMusic/methodLog-3.txt");
        BuildModel2 buildModel2 = new BuildModel2();
        printCallSequence(callSeq1);
        List<Event> events = GenerateEventUtil.generateEvents(callSeq1);
//        for(Event event:events){
//            System.out.println("invoke size: "+event.getInvokeList().size());
//            if(event.getInvokeList().size()>2){
//                continue;
//            }
//            for(MyMethod myMethod:event.getInvokeList()){
//                System.out.println(myMethod.selfJson);
//            }
//        }
//        for(MyMethod myMethod:callSeq1){
//            System.out.println(myMethod.methodName);
//        }
		ArrayList<String> userInput1 = new ArrayList<>();
		userInput1.add("薛之谦");//李白 锅盔  中国机长 网红 摸摸头 三体 武汉 苹果 米饭
//        userInput1.add("cat");
		buildModel2.addCallLogData(callSeq1,userInput1);

        ArrayList<String> userInput2 = new ArrayList<>();
        userInput2.add("薛之谦");//李荣浩 面条  少年的你 重生 纵横 绝世高手 湖北 香蕉 鸡蛋
//        userInput2.add("dog");
        buildModel2.addCallLogData(callSeq2,userInput2);

        ArrayList<String> userInput3 = new ArrayList<>();
        userInput3.add("薛之谦");//吴亦凡 汤圆  钢铁侠 最强 都市 捡漏 口罩 橘子 牛奶
//        userInput3.add("fish");
        buildModel2.addCallLogData(callSeq3,userInput3);

        System.out.println("微服务模板生成中......");
        List<Event> modelEvents = buildModel2.obtainModel();
        System.out.println(modelEvents.size());
//        for(Event event:modelEvents){
//            if(event.getMethodName().equals(Event.SETTEXT)){
//                for(MyParameter param:event.getParameters()){
//                    System.out.println(param.type+" "+param.value);
//                }
//            }
//        }

        JSONArray jsonArray = ProcessEventUtil.transformAPIEventsToJSONArray(modelEvents);
//        MyFileUtil.writeEventJSONArray("execute.txt",jsonArray);
        MyFileUtil.writeLineJSONArray("execute.txt",jsonArray);
        System.out.println("微服务模板生成完成");
	}
	private static void printCallSequence(List<MyMethod> callSeq){
	    for(MyMethod myMethod:callSeq){
	        System.out.println(myMethod.methodName);
        }
    }
}
