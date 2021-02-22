package apiAdapter.data;

import java.io.Serializable;

public class MyMethod implements Serializable {
    private static final long serialVersionUID = 1L;
    private String methodName,className,signature;
    private MyClass declaringClass;
    private int modifiers;

    public String getMethodName() {
        return methodName;
    }

    public String getClassName() {
        return className;
    }

    public String getSignature() {
        return signature;
    }

    public MyClass getDeclaringClass() {
        return declaringClass;
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setDeclaringClass(MyClass declaringClass) {
        this.declaringClass = declaringClass;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }
}
