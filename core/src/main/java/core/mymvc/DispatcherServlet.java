package core.mymvc;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import core.annotation.RequestParam;

public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = -6605556909407235986L;
	private static final Logger log = Logger.getLogger(DispatcherServlet.class);

	@SuppressWarnings("unchecked")
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			resp.setCharacterEncoding(getServletContext().getInitParameter("charset"));
			Map<String,Handler> mapping = 
					(Map<String, Handler>) getServletContext().getAttribute(ClassScannerListener.REQUEST_MAPPING);
			String requestURI = req.getRequestURI();
			Handler handler = mapping.get(requestURI.substring(getServletContext().getContextPath().length()));
			if(handler!=null){
				log.info(String.format("requet [%1s] ---> handler [%2s]", 
						requestURI,handler.getTargetObj().getClass().getName()+"."+handler.getInvokeMethod().getName()));
				Object targetObj = handler.getTargetObj();
				Method invokeMethod = handler.getInvokeMethod();
				List<Object> params = new ArrayList<Object>();
				
				Class<?>[] pTypes = invokeMethod.getParameterTypes();
				Annotation[][] paramAnno = invokeMethod.getParameterAnnotations();
				
				for (int i = 0;i<pTypes.length;i++) {
					Class<?> clz = pTypes[i];
					if(ServletRequest.class.isAssignableFrom(clz)){
						params.add(req);
					}else if(ServletResponse.class.isAssignableFrom(clz)){
						params.add(resp);
					}else if(clz == String.class){
						for (Annotation anno : paramAnno[i]) {
							if(RequestParam.class.isAssignableFrom(anno.getClass()) ){
//							if(anno instanceof RequestParam){
								String paramName = ((RequestParam)anno).value();
								params.add(req.getParameter(paramName));
							}
						}
					}
				}
				
				Object result = invokeMethod.invoke(targetObj, params.toArray(new Object[params.size()]));
				if(result != null)
					resp.getWriter().write((String)result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
