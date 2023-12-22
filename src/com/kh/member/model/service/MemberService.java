package com.kh.member.model.service;

import static com.kh.common.JDBCTemplate.*;

import java.sql.Connection;

import com.kh.member.model.dao.MemberDao;
import com.kh.member.model.vo.Member;

public class MemberService {

	/**
	 * 로그인 요청 서비스
	 * 
	 * @param userId  => 사용자가 입력한 아이디값.
	 * @param userPwd => 사용자가 입력했던 비밀번호값.
	 * @return
	 */
	public Member loginMember(String userId, String userPwd) {
		// 1) 커넥션 생성
		Connection conn = getConnection();
		// 2) 커넥션 전달
		Member m = new MemberDao().loginMember(conn, userId, userPwd);
		// 3) 커낵션 반납
		close(conn);
		// 4) 결과값 반납
		return m;

	}

	/**
	 * 회원가입서비스
	 * 
	 * @param m => 회원가입할 회원의 정보를 담은 객체
	 * @return => 처리된 행의 갯수
	 */
	public int insertMember(Member m) {

		Connection conn = getConnection();

		int result = new MemberDao().insertMember(conn, m);

		if (result > 0) {
			commit(conn);
		} else {
			rollback(conn);
		}

		close(conn);

		return result;
	}

	public Member updateMember(Member m) {

		// 1) 내 정보수정
		Connection conn = getConnection();
		int result = new MemberDao().updateMember(conn, m);
		// 2) 정보변경 성공시 commit+변경된 회원정보 조회
		// 실패시 rollback
		Member updateMem = null;
		if (result > 0) { // 내 정보 변경 성공
			commit(conn);
			// 갱신된 회원객체 다시 조회해오기
			updateMem = new MemberDao().selectMember(conn, m.getUserId());
		} else { // 실패
			rollback(conn);
		}

		close(conn);
		return updateMem;
	}
	


	public int deleteMember(String userId, String userPwd) {
		
		Connection conn=getConnection();
		
		int result=new MemberDao().deleteMember(conn,userId,userPwd);
		
		if(result>0) {
			commit(conn);
		}else {
			rollback(conn);
			
		}
		

		close(conn);
		
		return result;
	}

	public Member updatePwdMember(String userId, String userPwd, String updatePwd) {
		
		Connection conn=getConnection();
		
		int result=new MemberDao().updatePwdMember(conn,userId,userPwd,updatePwd);
		
		Member updateMem=null;
		
		if(result >0) {
			commit(conn);
			
			// 갱신된 회원정보 조회
			updateMem=new MemberDao().selectMember(conn,userId);
		}else {
			rollback(conn);
		}
		close(conn);
		
		return updateMem;
		
		
	}

}
