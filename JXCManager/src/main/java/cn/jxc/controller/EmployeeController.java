package cn.jxc.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;

import cn.jxc.exception.CustomException;
import cn.jxc.pojo.Dept;
import cn.jxc.pojo.Employee;
import cn.jxc.pojo.Role;
import cn.jxc.service.DeptService;
import cn.jxc.service.EmployeeService;
import cn.jxc.service.RoleService;

@Controller
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private DeptService deptService;
	@Autowired
	private RoleService roleService;

	@RequestMapping("/")
	public String index() {
		System.out.println("跳转至首页");
		return "index";
	}
	
	@RequestMapping("/gowelcome")
	public String gowelcome() {
		System.out.println("跳转至欢迎页面");
		return "redirect:welcome.jsp";
	}

	/**
	 * 登录方法
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/login")
	public ModelAndView login1(String username, String password, HttpSession session, HttpServletRequest request,
			Model model) throws CustomException, Exception {
		// 如果登录失败从request中获取认证异常信息,shiroLoginFailure就是shiro异常类的全限定名
		String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
		System.err.println(exceptionClassName + "==============异常xxxx");
		// 根据shiro返回的异常类路径判断，抛出指定异常信息
		String message = null;
		if (exceptionClassName != null) {
			if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
				// 最终会抛给异常处理器
				message = "账号不存在";
			} else if (IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
				message = "用户名/密码错误";
			} else {
				message = "未知错误======" + exceptionClassName;
			}
		}
		// 此方法不处理登录成功，shiro认证成功会自动跳转到上一个路径
		// 登录失败返回到login页面
		ModelAndView mav = new ModelAndView("login");
		mav.addObject("message", message);
		return mav;
	}

	/**
	 * 退出登录
	 * 
	 * @param session
	 * @return
	 */
	// @RequestMapping("/logout")
	// public String logout(HttpSession session) {
	// session.invalidate();
	// return "login";
	// }

	/**
	 * 跳转到查看所有员工的页面
	 * 
	 * @param pageNum
	 * @param model
	 * @return
	 */
	@RequiresPermissions("employee:list")
	@RequestMapping("/goEmployee")
	public String goemployee(@RequestParam(value = "pageNo", required = false) Integer pageNum, Model model) {
		if (pageNum == null) {
			pageNum = 1;
		}
		PageInfo<Employee> employeeAll = employeeService.getEmployeeAll(pageNum, 5);
		List<Role> roles = roleService.getRoleAll(1, 10000).getList();
		model.addAttribute("employeeAll", employeeAll);
		model.addAttribute("roleAll", roles);
		return "employee/employee";
	}

	/**
	 * 跳转到新增员工的页面
	 * 
	 * @param model
	 * @return
	 */
	@RequiresPermissions("employee:add")
	@RequestMapping("/goEmployeeAdd")
	public String goemployeeadd(Model model) {
		List<Dept> deptAll = deptService.getDeptAll();
		model.addAttribute("deptAll", deptAll);
		return "employee/employeeAdd";
	}

	/**
	 * 添加员工信息
	 * 
	 * @param employee
	 *            员工对象
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/goEmployeeInsert")
	public String goEmployeeInsert(Employee employee) throws Exception {
		try {
			employeeService.addEmployee(employee);
			return "redirect:goEmployee";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new Exception("新增出错+" + e.getMessage());
			// return "";
		}
	}

	/**
	 * 根据用户名查询相关权限 以及所有权限
	 * 
	 * @param empLoginName
	 *            用户名
	 * @return
	 */
	@RequestMapping("/findRolesByEmp")
	@ResponseBody
	public String findRolesByEmp(@RequestParam("emploginname") String empLoginName) {
		System.out.println(empLoginName);
		// 根据用户名查询拥有的角色
		List<Role> findRoleByEmp = roleService.findRoleByEmp(empLoginName);
		System.out.println(findRoleByEmp.size() + "===拥有的角色个数");
		return JSON.toJSONString(findRoleByEmp);
	}

	/**
	 * 为用户分配角色
	 * 
	 * @return
	 */
	@RequestMapping("/goEmpRoleAssign")
	public String goEmpRoleAssign(String[] assignRole, String empLoginName) {
		List<String> list = Arrays.asList(assignRole);
		int addEmpRole = roleService.addEmpRole(list, empLoginName);
		if (addEmpRole > 0) { // 代表成功分配角色
			return "redirect:goEmployee";
		}
		return "error";
	}

}
