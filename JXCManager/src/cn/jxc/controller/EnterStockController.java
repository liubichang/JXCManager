package cn.jxc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ���
 * @author ������
 *
 */
@Controller
public class EnterStockController {
	
	@RequestMapping("/goenterstock")
	public String goenterstock() {
		return "enterStock";
	}
	
}
