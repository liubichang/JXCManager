
package cn.jxc.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

import cn.jxc.pojo.Employee;
import cn.jxc.pojo.Product;
import cn.jxc.pojo.PurchaseRequest;
import cn.jxc.pojo.PurchaseRequestDetail;
import cn.jxc.pojo.Supplier;
import cn.jxc.service.EmployeeService;
import cn.jxc.service.ProductService;
import cn.jxc.service.PurchaseRequestDetailService;
import cn.jxc.service.PurchaseRequestService;
import cn.jxc.service.SupplierService;

@Controller
public class PurchaseRequestController {

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private ProductService productService;
	@Autowired
	private PurchaseRequestService purchaseRequestService;
	@Autowired
	private PurchaseRequestDetailService purchaseRequestDetailService;

	@RequestMapping("/gopurchase")
	public String gopurchase(@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "empNo", required = false) String empNo,
			@RequestParam(value = "singleNo", required = false) String singleNo,
			@RequestParam(value = "suppName", required = false) String suppName, Model model) {
		if (pageNo == null) {
			pageNo = 1;
		}
		PageInfo<PurchaseRequest> purchaseRequestByBlurry = purchaseRequestService.getPurchaseRequestByBlurry(pageNo,
				5,empNo, singleNo, suppName);
		model.addAttribute("prbb", purchaseRequestByBlurry);
		model.addAttribute("empNo", empNo); // ��ѯ��������
		model.addAttribute("singleNo", singleNo);
		model.addAttribute("suppName", suppName);
		// model.addAttribute("msg", "a"); //ǰ̨��ʾ���
		return "purchase/purchaseRequest";
	}

	/**
	 * ��ת���ɹ���������ҳ��
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/goPurchaseRequest")
	public String goPurchaseRequest(Model model) {
		List<Employee> employees = employeeService.getEmployeeAll();
		List<Supplier> suppliers = supplierService.getSupplierAll();
		PageInfo<Product> productAll = productService.getProductAll(1); // ��ҳ��ѯ �տ�ʼ�����ѯ��һҳ������

		model.addAttribute("employees", employees);
		model.addAttribute("suppliers", suppliers);
		model.addAttribute("productAll", productAll);

		return "purchase/purchaseRequestAdd";
	}

	/**
	 * ͨ��ajaxʵ�ַ�ҳ��Ʒ��Ϣ �ɹ���������ҳ����
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getProductByPage", method = RequestMethod.POST)
	@ResponseBody
	public String getProductPage(@RequestParam("pageNum") Integer pageNum) {
		PageInfo<Product> productAll = productService.getProductAll(pageNum);
		return JSON.toJSONString(productAll.getList());
	}

	/**
	 * ͨ��ajaxʵ�� ���ݲɹ����Ų�ѯһ���ɹ�����������Ϣ
	 * 
	 * @param singleNo
	 * @return
	 */
	@RequestMapping("getPurchaseRequestBySingleNo")
	@ResponseBody
	public String getPurchaseRequestBySingleNo(@RequestParam("singleNo") String singleNo,
			@RequestParam(value = "pageNo", required = false) Integer pageNo) {
		if (null == pageNo) {
			pageNo = 1;
		}
		PurchaseRequest purchaseRequestBySingleNo = purchaseRequestService.getPurchaseRequestBySingleNo(singleNo);
		PageInfo<PurchaseRequestDetail> pageInfos = purchaseRequestDetailService.getPurchaseRequestDetail(singleNo,
				pageNo, 3);
		pageInfos.getList().get(0).setPurchaserequest(purchaseRequestBySingleNo);
		String jsonString = JSON.toJSONString(pageInfos);
		return jsonString;
	}

	/**
	 * �ɹ������������
	 * 
	 * @return
	 * @author Killy
	 * @param purchaseRequestadd
	 *            �����еĲ���
	 * @param products
	 *            �ɹ�������ϸ
	 */
	@RequestMapping("/productRequestAdd")
	public String productRequestAdd(PurchaseRequest purchaseRequestadd, String products) {
		List<PurchaseRequestDetail> purchaseRequestDetails = JSONObject.parseArray(products,
				PurchaseRequestDetail.class);
		purchaseRequestadd.setPurchaserequestdetails(purchaseRequestDetails);
		int addPurchaseRequest = purchaseRequestService.addPurchaseRequest(purchaseRequestadd);
		if (addPurchaseRequest == 1) { // ����ʧ��
			return "error";
		} else { // ����ɹ�
			return "redirect:gopurchase"; // ��ת����ҳ��ѯҳ��
		}
	}

	/**
	 * ��ת���޸ĵ�ҳ��
	 * 
	 * @param singleNo
	 * @param model
	 * @return
	 */
	@RequestMapping("/goPurchaseUpdate")
	public String goPurchaseUpdate(String singleNo, Model model, Integer productPageNo) {
		if (null == productPageNo) {
			productPageNo = 1;
		}
		// ���ݶ����Ų�ѯһ������������Ϣ
		PurchaseRequest purchase = purchaseRequestService.getPurchaseRequestBySingleNo(singleNo);
		PageInfo<PurchaseRequestDetail> purchaseRequestDetails = purchaseRequestDetailService
				.getPurchaseRequestDetail(singleNo, 1, 1000);
		List<Employee> employeeAll = employeeService.getEmployeeAll();
		List<Supplier> supplierAll = supplierService.getSupplierAll();
		PageInfo<Product> productAll = productService.getProductAll(productPageNo);
		model.addAttribute("purchase", purchase);
		model.addAttribute("purchaseDetails", purchaseRequestDetails);
		model.addAttribute("employeeAll", employeeAll);
		model.addAttribute("supplierAll", supplierAll);
		model.addAttribute("productAll", productAll);
		return "purchase/purchaseUpdate";
	}

	/**
	 * �ɹ������޸ĵķ��� ͬʱ���Ӳɹ�������ϸ
	 * 
	 * @return
	 */
	@RequestMapping("productRequestUpdate")
	public String productRequestUpdate(PurchaseRequest purchaseRequest, Model model, String products) {
		// �޸Ļ�����Ϣ
		List<PurchaseRequestDetail> parseArray = JSONObject.parseArray(products, PurchaseRequestDetail.class);
		purchaseRequest.setPurchaserequestdetails(parseArray);// Ϊ��Ʒ���鸳ֵ
		int updatePurchaseRequest = purchaseRequestService.updatePurchaseRequest(purchaseRequest);
		if (updatePurchaseRequest > 0) { // �޸ĳɹ�
			return "redirect:gopurchase";
		} else { // �޸�ʧ��
			return "error";
		}
	}

	/**
	 * 
	 * �޸�ҳ���ҳ��ѯ�ɹ�������ϸ
	 * 
	 * @param pageNum
	 * @param purchaseId
	 * @return
	 */
	@RequestMapping("getExistProductByPage")
	@ResponseBody
	public String getExistProductByPage(Integer pageNum, String purchaseId) {
		PageInfo<PurchaseRequestDetail> purchaseRequestDetail = purchaseRequestDetailService
				.getPurchaseRequestDetail(purchaseId, pageNum, 1000);
		return JSON.toJSONString(purchaseRequestDetail);
	}

	/**
	 * ʹ��ajaxʵ�ָ���idɾ��һ���ɹ���ϸ
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("delPurchaseDetailById")
	@ResponseBody
	public String delPurchaseDetailById(int id) {
		int delPurchaseRequestDetail = purchaseRequestDetailService.delPurchaseRequestDetail(id);
		if (delPurchaseRequestDetail > 0) { // ɾ���ɹ�
			return "1";
		} else
			return "0";
	}

	/**
	 * �ύ����
	 * 
	 * @return
	 */
	@RequestMapping("operaOrder")
	public String operaOrder(String singleNo, Integer statusNo) {
		int updatePurchaseOrderStatus = purchaseRequestService.updatePurchaseOrderStatus(singleNo, statusNo);// ֱ�ӰѶ���״̬�ĳɶ��������
		if (updatePurchaseOrderStatus > 0) { // �޸ĳɹ�
			return "redirect:gopurchase"; // ��ת����ѯ����
		} else {
			return "error";
		}
	}

	/**
	 * ������� ���ž������
	 * 
	 * @param singleNo
	 *            ������
	 * @param no
	 *            �Ƿ�ͨ��
	 * @param reason
	 *            ԭ��
	 * @return
	 */
	@RequestMapping("deptReview")
	public String deptReview(String singleNo, Integer no, String reason, HttpServletRequest request) {
		Employee employee = (Employee) request.getSession().getAttribute("loginEmp");
		int updateDeptReivewStatus = purchaseRequestService.updateDeptReivewStatus(singleNo, employee.getEmpLoginName(),
				new Date(), no, reason);
		if (updateDeptReivewStatus > 0) { // ִ�гɹ�
			return "redirect:gopurchase";
		} else {
			return "error";
		}
	}

	@RequestMapping("finalReview")
	public String finalReview(String singleNo, Integer no, String reason, HttpServletRequest request) {
		Employee employee = (Employee) request.getSession().getAttribute("loginEmp");
		int updateFinancialReivewStatus = purchaseRequestService.updateFinancialReivewStatus(singleNo,
				employee.getEmpLoginName(), new Date(), no, reason);
		if (updateFinancialReivewStatus > 0) {// ִ�гɹ�
			return "redirect:gopurchase";
		} else
			return "error";
	}

	/**
	 * ʹ��ajax��֤�Ƿ����ִ�в���
	 * 
	 * @param singleNo
	 * @return
	 */
	@RequestMapping("judgmen")
	@ResponseBody
	public String judgmen(String singleNo) {
		PurchaseRequest purchaseRequestBySingleNo = purchaseRequestService.getPurchaseRequestBySingleNo(singleNo);
		int no = purchaseRequestBySingleNo.getOrderStatus().getNo();
		System.out.println(no+"\t"+purchaseRequestBySingleNo.getOrderStatus().getOrderType());
		if (no == 2 || no == 5 || no == 7) { // ������״̬����ȡ�� �ܾ� ȫ������״̬�ſ���ɾ��
			return "1";
		} else
			return "0"; // ������ɾ��
	}
	
	/**
	 * ɾ����ѡ����
	 * @return
	 */
	@RequestMapping("deletePurchases")
	public String deletePurchases(String singleNos) {
		try {
			String[] single = singleNos.split("-");
			for (int i = 1; i < single.length; i++) {
				purchaseRequestService.deletePurchaseRequest(single[i]);
			}
			return "redirect:gopurchase";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

}