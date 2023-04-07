package kr.or.iei.member.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.iei.member.model.dao.MemberDao;
import kr.or.iei.member.model.vo.CampingPayment;
import kr.or.iei.member.model.vo.Member;
import kr.or.iei.member.model.vo.MemberPageData;

@Service
public class MemberService {

	
	@Autowired
	private MemberDao dao;

	public Member selectOneMember(Member member) {
			return dao.selectOneMember(member);
		}

	@Transactional
	public int insertMember(Member member) {
		dao.insertMember(member);
		return 0;
	}

	
	//회원탈퇴
	public int deleteMember(int memberNo) {
		return dao.deleteMember(memberNo);
	}
	
	public MemberPageData selectPayList(int memberNo,int reqPage) {
		int numPerpage = 5;
		
		//reqPage = 1 -> 1~2,  reqPage = 2 -> 2~3
		int end = reqPage * numPerpage;
		int start = end - numPerpage + 1;
		
		//계산된 start, end를 가지고 게시물 목록 조회
		//Mybatis는 매개변수는 한개만 설정이 가능 -> 필요한 값이 여러개면 1개로 묶어야함 (vo또는 map)
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("start",start);
		map.put("end",end);
		map.put("memberNo", memberNo);
		
		ArrayList<CampingPayment> list = dao.selectPayList(map);
		
		//pageNavi 제작 시작
		//전체 게시물 수 필요 -> 전체 게시물 수 조회 
		System.out.println(memberNo);
		int totalCount = dao.selectPayListCount(memberNo);
		
		//전체게시물로 전체 페이지 수 계산
		int totalPage = (int)Math.ceil(totalCount/(double)numPerpage);
		
		
		//pageNavi사이즈
		int pageNaviSize = 5;
				
		int pageNo = 1;
			if(reqPage > 3) {
				pageNo = reqPage-2;
			}
				
		//페이지네비 생성 시작
			String pageNavi = "<ul class='pagination circle-style'>";
		
		//이전 버
		if(pageNo != 1) {
			
			pageNavi += "<li>";
			pageNavi += "<a class='page-item' href='/cmapingPayList.do?reqPage="+(pageNo-1)+"&memberNo="+memberNo+"'>";
			pageNavi += "<span class='material-icons'>chevron_left</span>";
			pageNavi += "</a></li>";

			}
			
			//페이지 숫자 생성
			for(int i=0; i<pageNaviSize; i++) {
				if(pageNo == reqPage) {
					pageNavi += "<li>";
					pageNavi += "<a class='page-item active-page' href='/cmapingPayList.do?reqPage="+pageNo+"&memberNo="+memberNo+"'>";
					pageNavi += pageNo;
					pageNavi += "</a></li>";
				}else {
					pageNavi += "<li>";
					pageNavi += "<a class='page-item' href='/cmapingPayList.do?reqPage="+pageNo+"&memberNo="+memberNo+"'>";
					pageNavi += pageNo;
					pageNavi += "</a></li>";
				}
				pageNo++;
				//for문을 중간에 탈출해야하는 경우가 있음 - 페이지가 끝나면 그 이후페이지(없는페이지)는 출력X
				if(pageNo>totalPage) {
					break;
				}
			}
			
			//다음버튼
			if(pageNo <= totalPage) {
				pageNavi += "<li>";
				pageNavi += "<a class='page-item' href='/cmapingPayList.do?reqPage="+pageNo+"&memberNo="+memberNo+"'>";
				pageNavi += "<span class='material-icons'>chevron_right</span>";
				pageNavi += "</a></li>";
			}
			pageNavi += "</ul>";
			
			MemberPageData mpd = new MemberPageData(list,pageNavi);
			 return mpd;
			}
		

	
	
		/*
		//이전 버튼
		if(pageNo != 1) {
		pageNavi += "<a href='/cmapingPayList.do?reqPage="+(pageNo-1)+"&memberNo="+memberNo+"'>[이전]</a>";
		}
		
		//페이지 숫자 생성
		for(int i=0;i<pageNaviSize;i++) {
		if(pageNo ==  reqPage) {
			pageNavi += "<span>"+pageNo+"</span>";
		}else {
			pageNavi += "<a href='/cmapingPayList.do?reqPage="+pageNo+"&memberNo="+memberNo+"'>"+pageNo+"</a>";
				}
		pageNo++;
		if(pageNo > totalPage) {
		 break;
			}
		}
		//다음버튼
		if(pageNo<=totalPage) {
		 pageNavi += "<a href='/cmapingPayList.do?reqPage="+pageNo+"&memberNo="+memberNo+" '>[다음]</a>";
		}
		MemberPageData mpd = new MemberPageData(list,pageNavi);
		 return mpd;
		}
		 */
	}
	

