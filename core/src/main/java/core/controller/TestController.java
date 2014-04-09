package core.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import core.annotation.RequestMapping;
import core.annotation.RequestParam;


@RequestMapping("/hello")
public class TestController {

	@RequestMapping("/test01")
	public String test01(@RequestParam(value="p") String param){
		return "my mvc say : "+ param;
	}
	
	@RequestMapping("/test02")
	public void test02(HttpServletResponse respon){
		PrintWriter writer;
		try {
			writer = respon.getWriter();
			writer.write("这是使用UTF-8输出的一段中文。");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
