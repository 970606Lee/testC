package com.kh.board.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

import com.kh.board.model.service.BoardService;
import com.kh.board.model.vo.Attachment;
import com.kh.board.model.vo.Board;
import com.kh.board.model.vo.Category;
import com.kh.common.MyFileRenamePolicy;
import com.oreilly.servlet.MultipartRequest;

/**
 * Servlet implementation class BoardInsertController
 */
@WebServlet("/insert.bo")
public class BoardInsertController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BoardInsertController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		ArrayList<Category> list=new BoardService().selectCategoryList();
		
		request.setAttribute("list", list);
		request.getRequestDispatcher("views/board/boardEnrollerForm.jsp").forward(request, response);
		
		
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * form태그의 전송방법을 multipart/form-data로 전송하는 경우
		 * request로 부터 값을 뽑는게 불가능.
		 * 
		 * multipart로 값을 이관시킨 후 다뤄줘야한다
		 * 
		 */
		// enctpe이 multipart/form-data로 전송되었는지 확인
		if(ServletFileUpload.isMultipartContent(request)){
			//System.out.println("실행중");
			// 파일 업로드를 위한 라이브러리 : cos.jar
			
			//1. 전송되는 파일을 처리할 작업내용(전송되는 파일의 용량제한, 전달된 파일을 저장할 폴더경로)
			// 1_1. 전송파일 용량제한(byte단위로 제시)
			/* 
			 * byte -> kbyte -> mbyte -> tbyte
			 * 
			 * 1kbyte == 1024 byte
			 * 1mbyte == 1024k == 1024*1024byte(2의 20승)
			 * */
			
			int maxSize=10*1024*1024; // 10Mbyte;
			
			// 1_2. 전달된 파일을 저장할 서버의 폴더 경로 알아내기
			// 세션 객체에서 제공하는 getRealPath메서드를 통해 알아내기
			// 매개변수로 board_upfiles폴더까지의 경로를 제시해줘야한다.
			String savePath=request.getSession().getServletContext().getRealPath("/resource/board_upfiles/");
			//System.out.println(savePath);
				
			/*
			 *	2. 전달된 파일명 수정 및 서버에 업로드
			 *	-	HttpServeltRequest request=> MultipartRequest multiRequest로 변환
			 *
			 *	매개변수 생성자로 MultipartRequest객체를 생성(cos.jar에서 제공하는 객체)
			 *	MultipartRequest multiRequest=new MultipartRequest(request객체,
			 *	저장할 폴더경로, 용량제한, 인코딩값, 파일명을 수정시켜주는 객체);
			 *
			 *	위 구문 한줄 실행만으로 넘어온 첨부파일들이 해당 폴더에 업로드됨.
			 *	그리고 사용자가 올린 파일명은 그대로 해당 폴더에 업로드하지 않는다(같은 파일명이 있을 경우
			 *	덮어씌워질 수 도 있고, 서버에따라 문제가 발생할 수도 있다.)
			 *
			 *	cos.jar에서 제공하는 파일명 수정작업을 해주는 객체
			 *	- DefaultFileRenamePolicy객체
			 *	- 내부적으로 rename()이라는 메서드가 실행이 되면서 파일명 수정이 진행됨
			 *	- 작동방식은 동일한 파일명이 존재할 경우 카운팅된 숫자를 붙여서 파일명을 수정해줌
			 *	ex) aaa.jpb, aaa1.jpb, aaa2.jpg
			 *
			 *	- 우리 입맛대로 파일명이 겹치지 않게끔 rename메서드를 오버라이딩 해줄 예정.
			 */
			MultipartRequest multiRequest=new MultipartRequest(request,savePath
					,maxSize,"UTF-8",new MyFileRenamePolicy());
			
			// 3. DB에 기록할 데이터들을 뽑기
			// - Board테이블에 추가할 데이터 (카테고리 번호, 제목, 내용, 작성자회원번호)
			// - Attachment테이블에 추가할 데이터(원본명, 수정명, 저장경로)
			String category=multiRequest.getParameter("category");
			String boardTitle=multiRequest.getParameter("title");
			String boardContent=multiRequest.getParameter("content");
			String boardWriter=multiRequest.getParameter("userNo");
			
			Board b=new Board.Builder()
					.categoryNo(Integer.parseInt(category))
					.boardTitle(boardTitle)
					.boardContent(boardContent)
					.boardWriter(boardWriter)
					.build();
			
			// 첨부파일은 필수가 아니기 때문에 우선 null로 초기화, 등록한 첨부파일이 있을 경우 객체 생성예정
			Attachment at=null;
			// nultiRequest.getOriginFileName("키")
			// 첨부파일이 있었을 경우 원본 파일명, 없었을 경우 null값이 반한
			if(multiRequest.getOriginalFileName("upfile")!=null) {
				at=new Attachment();
				at.setOriginName(multiRequest.getOriginalFileName("upfile")); // 원본명
				at.setChangeName(multiRequest.getFilesystemName("upfile")); // 수정명(실제 서버에 올라간 파일명)
				at.setFilePath("resource/board_upfiles/");
			}
			
			// 4. 서비스 요청(DML)
			int result=new BoardService().insertBoard(b,at);
			
			// 5. 응답화면 지정
			if(result>0) { // 성공 => list.bo
				request.getSession().setAttribute("alertMsg", "게시글 작성에 성공했습니다");
				response.sendRedirect("list.bo?currentPage=1");
			}else {
				// 실패시 -> 첨부파일이 있었을 경우 이미 업로드된 첨부파일을 서버에서 삭제후,
				// 에러페이지로 포워딩
				if(at!=null) {
					// 삭제시키고자하는 파일객체 생성후 delete메서드 호출
					new File(savePath+at.getChangeName()).delete();
				}
				
				request.setAttribute("errorMsg", "게시글 작성 실패");
				request.getRequestDispatcher("views/common/errorPage.jsp").forward(request, response);
			}
			
			
		}
	}


}
