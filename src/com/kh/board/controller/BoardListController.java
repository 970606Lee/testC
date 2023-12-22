package com.kh.board.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kh.board.model.service.BoardService;
import com.kh.board.model.vo.Board;
import com.kh.common.model.vo.PageInfo;

/**
 * Servlet implementation class BoardListController
 */
@WebServlet("/list.bo")
public class BoardListController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BoardListController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//---------------페이징처리----------------------
				int listCount; // 총 게시글 갯수
				int currentPage; //현재 요청한 페이지
				int pageLimit; // 페이지바 하단에 보여질 페이징바의 페이지 최대 갯수
				int boardLimit;// 한 페지에 보여질 게시글의 최대 갯수
				
				int maxPage; // 가장 마지막 페이지가 몇번 페이지인지( 총 페이지 수)
				int startPage; // 페이지 하단에 보여질 페이징바의 시작수
				int endPage; // 페이지 하단에 보여질 페이징바의 끝수
				
				// *listCount : 총 게시글 갯수
				listCount=new BoardService().selectListCount();
				
				// *currentpage : 사용자 요청한 페이지
				currentPage=Integer.parseInt(request.getParameter("currentPage")==null
						?"1":request.getParameter("currentPage"));
				// *pageLimit : 한 페이지에 보여질 페이징바의 최대개수
				pageLimit = 10;
				
				//* boarLimit : 한 페이지에 보여질 게시글의 최대개수
				boardLimit=10;
				
				// * maxpage : 가장 마지막 페이지가 몇번 페이지인지(총 페이지 수)
				/*
				 * listCount, boardLimit에 영향을 받음
				 * 
				 * - 공식 구하기
				 * 	단, boardLimit 10이라는 가정하에 규칙을 세워봄
				 * 
				 * 	총 개수			boardLimit		maxPage
				 * 	2000개			10개				200번 페이지
				 * 	2001객			10개				200.1페이지=> 올림처리 201번페이지
				 * -------------------------------------------
				 * 	2011개			10개				201.1페이지= 올림처리 202번페이지
				 * -> 나눈셈연산한 결과를 올림처리한다면 maxPage값이 나온다.
				 * 
				 * 	1) listCount를 double로 형변환
				 * 	2) listCount/boardLimit
				 * 	3) 결과에 올림처리후
				 * 	4) 결과값을  int로 변환\
				 * */
				
				maxPage=(int)Math.ceil((double)listCount/boardLimit);
				
				// *startPage ; 페이징바의 시작수
				/*
				 * pageLimit, currentPage에 영향을 받음
				 * 
				 * 	- 공식 구하기
				 * 	단, pageLimit는 10이라는 가정하에 규칙을 세워보기
				 * 
				 * 	startPage : 1, 11, 21, 31, .... n=> n*10+1
				 * 
				 * 	만약에 pageLimit가 5가 된다? 1, 6, 11, 16... => n*5+1
				 * 
				 * 	즉 => n*pageLimit+1
				 * 
				 * 	currentPage		startPage
				 * 		1				1
				 * 		5				1
				 * 		10				1
				 * 
				 * 		11				11
				 * 		15				11
				 * 		20				11
				 * 		
				 * 		1~10 => 1 =>	n*pageLimit+1 ===> n=0
				 * 		11~20 => 11 =>	n*pageLimit+1 ===> n=1
				 * 		21~30 => 21 =>	n*pageLimit+1 ===> n=2
				 * 		n=(currentPage-1)/pageLimit
				 * 			0~9			/10=0
				 * 			10~19		/10=1
				 * 			20~29		/10=2
				 * 		startPage=n*pageLimit+1
				 * 			=> (currentpage-1)/pageLimit*pageLimit+1	
				 */
				startPage=(currentPage-1)/pageLimit*pageLimit+1;
				
				// *endPage : 페이징바의 끝수
				/*
				 * startpage. pageLimit에 영향을 받음 + maxPage에도
				 * 
				 * - 공식 구하기(pageLimit는 10이라는 가정)
				 * 
				 * startPage : 1 => 끝수 : 10
				 * startPage : 11 => 20
				 * startPage : 21 => 30
				 * 
				 * endPage=startpage+pageLimit -1;
				 * 
				 */
				endPage=startPage+pageLimit-1;
				
				if(endPage>maxPage) {
					endPage=maxPage;
				}
				
				// 페이지 정보들을 하나의 공간에 담아서 보내기.
				// 페이지 정보들을 담아주는 vo클래스가 필요함
				// -> 사진게시판 공지사항 게시판, 그외에 추가될수 있는 게시판에서도 쓰일것이므로
				// common패키지에 만들기
				
				// 1. 페이징바 만들때 필요한 객체
				PageInfo pi=new PageInfo(listCount, currentPage, pageLimit, boardLimit
						, maxPage, startPage, endPage);
				
				// 2. 현재 사용자가 요청한 페이지에 보여질 게시글 리스트
				ArrayList<Board> list=new BoardService().selectList(pi);
				
				request.setAttribute("pi",pi);
				request.setAttribute("list",list);
				
				request.getRequestDispatcher("views/board/boardListView.jsp").forward(request, response);
				
				
				
			}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		
		
	}

}
