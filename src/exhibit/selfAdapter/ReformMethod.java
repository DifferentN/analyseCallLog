package exhibit.selfAdapter;

import analyseMethodCall.MethodSequenceUtil;
import analyseMethodCall.MyMethod;

import java.util.List;

public class ReformMethod {
    public void reformMethod(String srcPath,String savePath){
        List<MyMethod> methodList = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/selfAdapter/anki/methodLog_change.txt");
        String path = "reformLog.txt";
        reformCall.ReformMethod reformMethod = new reformCall.ReformMethod(path);
        reformMethod.reformMethod(methodList);
    }
}
