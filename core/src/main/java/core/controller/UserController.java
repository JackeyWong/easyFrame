package core.controller;

import core.annotation.RequestMapping;
import core.annotation.RequestParam;

@RequestMapping("/user")
public class UserController {

	@RequestMapping("/login")
	public String login(@RequestParam("user") String username,
			@RequestParam("pwd") String pwd){
		String result;
		if("wangjie".equals(username) && "123".equals(pwd)){
			result = "欢迎 wangjie,登录成功。";
		}else{
			result = "登录失败请重新登录";
		}
		return result;
	}
}
