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
 * Servlet implementation class BoardUpdateFormController
 */
@WebServlet("/update.bo")
public class BoardUpdateFormController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BoardUpdateFormController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		BoardService bService=new BoardService();
		int boardNo=Integer.parseInt(request.getParameter("bno"));

		// 1) 게시글 정보 조회
		Board b = bService.selectBoard(boardNo);
		
		// 2) 첨부파일 정보 조회
		Attachment at=bService.selectAttachment(boardNo);
		
		// 3) 카테고리 목록 조회
		ArrayList<Category> list=bService.selectCategoryList();
		
		request.setAttribute("list", list);
		request.setAttribute("at", at);
		request.setAttribute("b", b);
		
		request.getRequestDispatcher("views/board/boardUpdateForm.jsp").forward(request, response);
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		request.setCharacterEncoding("UTF-8");
		
		if(ServletFileUpload.isMultipartContent(request)) {
			// 전송파일 용량제한
			int maxSize=10*1024*1024;
			
			// 파일을 저장할 물리적인 경로 알아내기
			String savePath=request.getSession()
					.getServletContext()
					.getRealPath("/resource/board_upfiles/");
			// 서버에 업로드
			MultipartRequest multi=new MultipartRequest(request,savePath,maxSize,"UTF-8", new MyFileRenamePolicy());
			
			//SQL문 실행시 필요한 변수들 셋팅
			// - BOARD테이블에 UPDATE시 필요한 변수들
			int boardNo=Integer.parseInt(multi.getParameter("bno"));
			String categoryNo=multi.getParameter("category");
			String boardTitle=multi.getParameter("title");
			String boardContent=multi.getParameter("content");
			
			Board b=new Board.Builder()
						.boardNo(boardNo)
						.categoryNo(Integer.parseInt(categoryNo))
						.boardTitle(boardTitle)
						.boardContent(boardContent)
						.build();
			// 새롭게 전달된 첨부파일이 있을 경우 필요한 값 뽑기
			Attachment at=null;
			
			if(multi.getOriginalFileName("reUpfile")!=null) {
				// at 객체 생성
				at=new Attachment();
				at.setOriginName(multi.getOriginalFileName("reUpfile"));
				at.setChangeName(multi.getFilesystemName("reUpfile"));
				at.setFilePath("resource/board_upfiles/");
				
				// 첨부파일이 이미 존재 했을 경우(fileNo가  hidden으로 넘어왔음)
				if(multi.getParameter("originFileNo")!=null) {
					// 새로운 첨부파일이 있던 케이스.
					// => update attachment 실행
					// update하기 위해서는 기존의 파일 고유번호가 필요함
					at.setFileNo(Integer.parseInt(multi.getParameter("originFileNo")));
					
					// 기존의 첨부파일 삭제
					new File(savePath+multi.getParameter("originFileName")).delete();
				}else {
					// 새롭게 첨부파일을 등록하려고 하지만, 기존에는 첨부파일이 없었을 경우
					// => Insert Attachment
					// insert하기 위해서는 현재 게시글 번호가 필요함
					at.setRefBno(boardNo);
				}
			}
			
			//
			int result =new BoardService().updateBoard(b,at);
			
			// case1 : 새로 등록할 첨부파일이 없는 경우 => b,null => Board Update
			// case2 : 새로운 첨부파일 O, 기존 첨부파일 O => b, fileno가 담긴 at => Board Update, Attachment Update
			// case3 : 새로움 첨부파일 O, 기존 첨부파일 X => b, refBno가 담긴 at => Board Update, Attachment Insert
			if(result>0) {
				// 수정 성공시
				request.getSession().setAttribute("alterMsg", "성공적으로 수정 되었습니다.");
				response.sendRedirect("detail.bo?bno="+boardNo);
			}else {
				request.setAttribute("errorMsg", "게시글 수정에 실패헀습니다..");
				request.getRequestDispatcher("views/common/errorPage.jsp").forward(request, response);
			}
		}
	}

}
