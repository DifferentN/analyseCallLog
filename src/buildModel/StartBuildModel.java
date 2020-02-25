package buildModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import FileUtil.MyFileWriter;
import analyseMethodCall.MethodSequenceUtil;
import analyseMethodCall.MyMethod;
import com.alibaba.fastjson.JSONArray;
import eventSimilarity.Event;
import eventSimilarity.ProcessEventUtil;

public class StartBuildModel {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//        Scanner reader = new Scanner(System.in);
//        int size = reader.nextInt();
//        reader.nextLine();
//        String data[] = new String[size];
//        for(int i=0;i<size;i++){
//            data[i] = reader.nextLine();
//        }
//        useBuildModel2(data);
        useBuildModel2(new String[0]);
	}
	public String generateAPI(List<String> instancePaths,List<List<String>> userInputs){
	    BuildModel2 buildModel2 = new BuildModel2();
	    int len = instancePaths.size();
	    for(int i=0;i<len;i++){
	        List<MyMethod> callSeq = MethodSequenceUtil.getSequence(instancePaths.get(i));
	        List<String> userInput = userInputs.get(i);
	        buildModel2.addCallLogData(callSeq,userInput);
        }
	    List<Event> modelEvents = buildModel2.obtainModel();
        JSONArray jsonArray = ProcessEventUtil.transformAPIEventsToJSONArray(modelEvents);
        String apiFileName = "execute.txt";
        MyFileWriter.writeEventJSONArray("execute.txt",jsonArray);
        return apiFileName;
    }

	private static void useBuildModel2(String userData[]){
        //读取指定文件中的打印序列
        List<MyMethod> callSeq1 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/3APP/cuco/selectBenChiC200L/methodLog-1.txt");
        List<MyMethod> callSeq2 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/3APP/cuco/selectBenChiC200L/methodLog-2.txt");
        List<MyMethod> callSeq3 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/3APP/cuco/selectBenChiC200L/methodLog-3.txt");
        BuildModel2 buildModel2 = new BuildModel2();

//        for(MyMethod myMethod:callSeq1){
//            System.out.println(myMethod.methodName);
//        }
		ArrayList<String> userInput1 = new ArrayList<>();
//		userInput1.add("肩颈");//李白 锅盔  中国机长 网红 摸摸头 三体 武汉 苹果 米饭
		buildModel2.addCallLogData(callSeq1,userInput1);

        ArrayList<String> userInput2 = new ArrayList<>();
//        userInput2.add("瘦身");//李荣浩 面条  少年的你 重生 纵横 绝世高手 湖北 香蕉 鸡蛋
        buildModel2.addCallLogData(callSeq2,userInput2);

        ArrayList<String> userInput3 = new ArrayList<>();
//        userInput3.add("冥想");//吴亦凡 汤圆  钢铁侠 最强 都市 捡漏 口罩 橘子 牛奶
        buildModel2.addCallLogData(callSeq3,userInput3);

        System.out.println("微服务模板生成中......");
        List<Event> modelEvents = buildModel2.obtainModel();

        JSONArray jsonArray = ProcessEventUtil.transformAPIEventsToJSONArray(modelEvents);
        MyFileWriter.writeEventJSONArray("execute.txt",jsonArray);
        System.out.println("微服务模板生成完成");
	}

}
