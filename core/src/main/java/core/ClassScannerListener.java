package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class ClassScannerListener implements ServletContextListener {

	private static final String CORE_CLASS_SCANNER_LISTENER_CLASS = "core/ClassScannerListener.class";
	private static final String basePackages = "core";
	private static Logger log = Logger.getLogger(ClassScannerListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			URL resource = this.getClass().getClassLoader()
					.getResource(CORE_CLASS_SCANNER_LISTENER_CLASS);
			File file = new File(resource.getFile().replace(
					CORE_CLASS_SCANNER_LISTENER_CLASS, basePackages));
			doScan(file);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void doScan(File file) throws FileNotFoundException, IOException {
		File[] listFiles = file.listFiles();
		for (File subFile : listFiles) {
			if (subFile.isDirectory()) {
				doScan(subFile);
			} else {
				log.info(subFile.getAbsolutePath());
				ClassReader reader = new ClassReader(new FileInputStream(
						subFile));
				reader.accept(new MyClassVisitor(), 0);
			}
		}
	}

	static class MyClassVisitor implements ClassVisitor {

		@Override
		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			System.out.println("class name:" + name);
			System.out.println("super class name:" + superName);
			System.out.println("class version:" + version);
			System.out.println("class access:" + access);
			System.out.println("class signature:" + signature);
			if (interfaces != null && interfaces.length > 0) {
				for (String str : interfaces) {
					System.out.println("implemented interface name:" + str);
				}
			}
			System.out.println("-----------------------------------------");
		}

		@Override
		public void visitSource(String source, String debug) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visitOuterClass(String owner, String name, String desc) {
			// TODO Auto-generated method stub

		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void visitAttribute(Attribute attr) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visitInnerClass(String name, String outerName,
				String innerName, int access) {
			// TODO Auto-generated method stub

		}

		@Override
		public FieldVisitor visitField(int access, String name, String desc,
				String signature, Object value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void visitEnd() {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}

}
