package core.mymvc;

import java.lang.reflect.Method;

public class Handler {

	private Object targetObj;
	private Method invokeMethod;
	public Handler(Object targetObj, Method invokeMethod) {
		super();
		this.targetObj = targetObj;
		this.invokeMethod = invokeMethod;
	}
	public Handler() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Object getTargetObj() {
		return targetObj;
	}
	public void setTargetObj(Object targetObj) {
		this.targetObj = targetObj;
	}
	public Method getInvokeMethod() {
		return invokeMethod;
	}
	public void setInvokeMethod(Method invokeMethod) {
		this.invokeMethod = invokeMethod;
	}

}
