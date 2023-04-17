package kr.or.iei.shop.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import common.FileManager;
import kr.or.iei.shop.model.service.ShopService;
import kr.or.iei.shop.model.vo.Shop;
import kr.or.iei.shop.model.vo.ShopListMainData;
import kr.or.iei.shop.model.vo.ShopPhoto;
import kr.or.iei.shop.model.vo.ShopReview;
import kr.or.iei.shop.model.vo.ShopReviewListData;
import kr.or.iei.shop.model.vo.ShopReviewPhoto;

@Controller
public class ShopController {

	@Autowired
	private ShopService service;
	@Autowired
	private FileManager manager;
	
	@RequestMapping(value="/shopMainList.do")
	public String shopMainList(Model model) {
	    int reqPage = 1;
		for(int shopCategory=0;shopCategory<3;shopCategory++) {
			ShopListMainData slmd = service.selectShopList(shopCategory, reqPage);
			if(shopCategory == 0) {
				model.addAttribute("campingList", slmd.getShopList());
				model.addAttribute("campingPageNavi", slmd.getPageNavi());
			} else if(shopCategory == 1) {
				model.addAttribute("carList", slmd.getShopList());
				model.addAttribute("carPageNavi", slmd.getPageNavi());
			} else {
				model.addAttribute("etcList", slmd.getShopList());
				model.addAttribute("etcPageNavi", slmd.getPageNavi());
			}
		}
		return "shop/shopMainList";
	}
	
	@RequestMapping(value="/shopList.do")
	public String shopList(int shopCategory, int reqPage) {
		return "shop/shopList";
	}
	
	@ResponseBody
	@RequestMapping(value="/getMoreList.do", produces = "application/json;charset=utf-8")
	public String getMoreList(int shopCategory, int reqPage) {
		ShopListMainData slmd = service.selectShopList(shopCategory, reqPage);
		return new Gson().toJson(slmd);
	}
	
	@RequestMapping(value="/insertShopFrm.do")
	public String insertShopFrm() {
		return "shop/insertShopFrm";
	}
	
	@Transactional
	@RequestMapping(value="/insertShop.do")
	public String insertShop(Shop shop,  MultipartFile[] shopFileList, HttpServletRequest requset) {
		int result = service.insertShop(shop);
		int finalResult = 1;
		if(result > 0) {
			ArrayList<ShopPhoto> photoList = new ArrayList<ShopPhoto>();
			if(!shopFileList[0].isEmpty()) {
				String savePath = requset.getSession().getServletContext().getRealPath("/resources/upload/shop/");
				for(MultipartFile file : shopFileList) {
					String filepath = manager.upload(savePath, file);
					ShopPhoto shopPhoto = new ShopPhoto();
					shopPhoto.setFilepath(filepath);
					photoList.add(shopPhoto);
				}
			}
			int shopNo = service.selectLatestShop();
			for( ShopPhoto sp : photoList) {
				int photoResult = service.insertShopPhoto(sp.getFilepath(), shopNo);
				if(photoResult == 0) {
					finalResult = 0;
				}
			}
		}
		if(finalResult > 0) {
			return "redirect:/shopMainList.do";
		} else {
			return "redirect:/";
		}
	}
	
	@RequestMapping(value="/viewShop.do")
	public String viewShop(int shopNo, int reqPage, int menu, Model model) {
		Shop shop = service.selectOneShop(shopNo);
		ShopReviewListData srld = service.selectShopReviewList(shopNo, reqPage);
		model.addAttribute("shop", shop);
		model.addAttribute("menu", menu);
		model.addAttribute("shopReviewList", srld.getShopReviewList());
		model.addAttribute("reviewPageNavi", srld.getReviewPageNavi());
		return "shop/viewShop";
	}
	
	@Transactional
	@ResponseBody
	@RequestMapping(value="/insertShopReview.do")
	public String insertShopReview(ShopReview sr, MultipartFile[] photoList, HttpServletRequest request ) {
		sr.setMemberId("user01");
		int result = service.insertShopReview(sr);
		int finalResult = 1;
		ArrayList<ShopReviewPhoto> srpList = new ArrayList<ShopReviewPhoto>();
		if(result > 0 ) {
			if(!photoList[0].isEmpty()) {
				String savePath = request.getSession().getServletContext().getRealPath("/resources/upload/shopReview/");
				for(MultipartFile file : photoList) {
					String filepath = manager.upload(savePath, file);
					ShopReviewPhoto srp = new ShopReviewPhoto();
					srp.setFilepath(filepath);
					srpList.add(srp);
				}
			}
			int shopReviewNo = service.selectLatestShopReview();
			for(ShopReviewPhoto srp : srpList) {
				srp.setShopReviewNo(shopReviewNo);
				int photoResult = service.insertShopReviewPhoto(srp);
				if(photoResult == 0) {
					finalResult = 0;
				}
			}
		}
		if(finalResult > 0) {
			return "success";
		} else {
			return "fail";
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/insertBasket.do")
	public String insertBasket() {
		
	}
}