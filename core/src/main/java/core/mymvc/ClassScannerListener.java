package core.mymvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import core.annotation.RequestMapping;

public class ClassScannerListener implements ServletContextListener {

	public static final String REQUEST_MAPPING = "requestMapping";
	private static final String SUFFIX = ".class";
	private static Logger log = Logger.getLogger(ClassScannerListener.class);
	private URL resource;
	private String relativePath;
	private static final Map<String,Handler> requestMapping = new HashMap<String, Handler>();
	
	
	public ClassScannerListener() {
		super();
		relativePath = getRelativePath();
		resource = this.getClass().getClassLoader().getResource(relativePath);
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			ServletContext servletContext = sce.getServletContext();
			String initParameter = servletContext.getInitParameter(
					"doscan-package");
			log.info("relativePath = " + relativePath);
			log.info("initParameter = " + initParameter);
			doScan(initParameter);
			servletContext.setAttribute(REQUEST_MAPPING, requestMapping);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void doScan(String... packages) throws FileNotFoundException,
			IOException {
		for (String pkg : packages) {
			String path = this.resource.getPath().replace(relativePath, pkg);

			File file = new File(path);
			if (file.exists() && file.canRead()) {
				log.info("doScan path --> " + file.getAbsolutePath());
				doScan(file);
			}
		}
	}

	private String getRelativePath() {
		return this.getClass().getName().replace(".", "/").concat(SUFFIX);
	}

	private void doScan(File file) throws FileNotFoundException, IOException {
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			if (listFiles.length > 0) {
				for (File subFile : listFiles) {
					doScan(subFile);
				}
			}
		} else {
			log.info(file.getAbsolutePath());
			if (file.getName().endsWith(SUFFIX)) {
				ClassReader reader = new ClassReader(new FileInputStream(file));
				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
				reader.accept(new MyClassVisitor(cw), 0);
			}
		}
	}

	static class MyClassVisitor extends ClassAdapter {

		private String owner;
		private boolean isInterface;

		public MyClassVisitor(ClassVisitor cv) {
			super(cv);
		}

		@Override
		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			cv.visit(version, access, name, signature, superName, interfaces);
			owner = name;
			
			isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			if(desc.contains("Ljava/lang/annotation/Target")){
				log.debug("owner : "+owner+", annotation--> desc: "+desc +" , visible: "+visible);
			}
			return super.visitAnnotation(desc, visible);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {
			log.debug("owner : "+owner+",method visitor -->name:"+ name +",desc:"+desc+",signature:"+signature);
			try {
				if(!"<init>".equals(name) && (access & Opcodes.ACC_PUBLIC)!=0){
					Class clz = getInstance(owner);
					Method method = clz.getMethod(name, getParameterTypes(desc));
					Annotation typeAnnotation = clz.getAnnotation(RequestMapping.class);
					RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
					if(typeAnnotation != null && typeAnnotation instanceof RequestMapping && methodAnnotation != null){
						String path1 = ((RequestMapping)typeAnnotation).value();
						String path2 = methodAnnotation.value();
						requestMapping.put(path1+path2, new Handler(clz.newInstance(), method));
						//method.invoke(clz.newInstance(), "成功过了");
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public static Class getInstance(String clsInfo){
		String className = clsInfo.replace("/", ".");
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Class[] getParameterTypes(String methodDescriptor){
		Type[] types = Type.getArgumentTypes(methodDescriptor);
		List<Class> result = null;
		if(types.length>0){
			result = new ArrayList<Class>();
			for (Type type : types) {
				try {
					int sort = type.getSort();
					if(sort == Type.INT){
						result.add(int.class);
					}else if(sort == Type.SHORT){
						result.add(short.class);
					}else if(sort == Type.BYTE){
						result.add(byte.class);
					}else if(sort == Type.LONG){
						result.add(long.class);
					}else if(sort == Type.BOOLEAN){
						result.add(boolean.class);
					}else if(sort == Type.DOUBLE){
						result.add(double.class);
					}else if(sort == Type.FLOAT){
						result.add(float.class);
					}else if(sort == Type.CHAR){
						result.add(char.class);
					}else if(sort == Type.ARRAY){
						Type eType = type.getElementType();
						result.add(Array.newInstance(Class.forName(eType.getClassName()), 0).getClass());
					}else if(sort == Type.OBJECT){
						result.add(Class.forName(type.getClassName()));
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			return result.toArray(new Class[result.size()]);
		}
		return null;
	}
	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}

}
