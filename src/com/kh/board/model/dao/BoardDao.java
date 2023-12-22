package com.kh.board.model.dao;
import static com.kh.common.JDBCTemplate.close;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import com.kh.board.model.vo.Attachment;
import com.kh.board.model.vo.Board;
import com.kh.board.model.vo.Category;
import com.kh.common.JDBCTemplate;
import com.kh.common.model.vo.PageInfo;

public class BoardDao {
	private Properties prop = new Properties();

	public BoardDao() {
		try {
			prop.loadFromXML(new FileInputStream(BoardDao.class.getResource("/sql/board/board-mapper.xml").getPath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int selectListCount(Connection conn) {
		// select문 => ResultSet => int
		
		int listCount = 0;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = prop.getProperty("selectListCount");

		try {
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();

			if (rset.next()) {
				listCount = rset.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(rset);
			close(pstmt);
		}
		return listCount;
	}

	public ArrayList<Board> selectListCount(Connection conn, PageInfo pi) {
		ArrayList<Board> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		String sql = prop.getProperty("selectList");

		try {
			pstmt = conn.prepareStatement(sql);

			/*
			 * boardLimit =10 currentPage=1 => 1~10 currentPage=3 => 21~30
			 * 
			 * 시작값 = (currentpage-1)*boardLimit+1 끝값 = 시작값+boardLimit-1
			 * 
			 */

			int startRow = (pi.getCurrentPage() - 1) * pi.getBoardLimit() + 1;
			int endRow = startRow + pi.getBoardLimit() - 1;

			pstmt.setInt(1, startRow);
			pstmt.setInt(2, endRow);
			//
			rset = pstmt.executeQuery();
			
			while (rset.next()) {
				Board b = new Board.Builder().boardNo(rset.getInt("BOARD_NO"))
						.categoryName(rset.getNString("CATEGORY_NAME")).boardTitle(rset.getNString("BOARD_TITLE"))
						.boardWriter(rset.getString("USER_ID")).count(rset.getInt("COUNT"))
						.createDate(rset.getDate("CREATE_DATE")).build();
				list.add(b);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCTemplate.close(rset);
			JDBCTemplate.close(pstmt);
		}

		return list;
	}
	
	public int increaseCount(Connection conn,int boardNo) {
		//UPDATE문
		int result=0;
		PreparedStatement pstmt=null;
		String sql=prop.getProperty("increaseCount");
		
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, boardNo);
			
			result=pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public Board selectBoard(Connection conn, int boardNo) {
		
		// Select => ResultSet=> Board
		Board b=null;
		PreparedStatement pstmt=null;
		ResultSet rset=null;
		
		String sql=prop.getProperty("selectBoard");
		
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, boardNo);
			
			rset=pstmt.executeQuery();
			
			if(rset.next()) {
				b=new Board.Builder()
						.categoryName(rset.getString(1))
						.boardTitle(rset.getString(2))
						.boardWriter(rset.getString(3))
						.boardContent(rset.getString(4))
						.createDate(rset.getDate(5))
						.boardNo(rset.getInt(6))
						.build();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			close(rset);
			close(pstmt);
		}
		
		
		return b;
	}
	
	public Attachment selectAttachment(Connection conn, int boardNo) {
		Attachment at = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = prop.getProperty("selectAttachment");
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, boardNo);
			
			rset = pstmt.executeQuery();
			
			if(rset.next()) {
				at = new Attachment();
				
				at.setFileNo(rset.getInt("FILE_NO"));
				at.setOriginName(rset.getString("ORIGIN_NAME"));
				at.setChangeName(rset.getString("CHANGE_NAME"));
				at.setFilePath(rset.getString("FILE_PATH"));
						
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rset);
			close(pstmt);
		}
		
		return at;
	}

	public ArrayList<Category> selectCategoryList(Connection conn) {
		ArrayList<Category> list=new ArrayList<>();
		PreparedStatement pstmt=null;
		ResultSet rset=null;
		String sql=prop.getProperty("selectCategoryList");
		
		try {
			pstmt=conn.prepareStatement(sql);
			rset=pstmt.executeQuery();
			
			while(rset.next()) {
				Category c = new Category(
						rset.getInt(1),
						rset.getString(2)
						);
				list.add(c);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			JDBCTemplate.close(rset);
			JDBCTemplate.close(pstmt);
		}
		return list;
	}

	public int insertBoard(Connection conn, Board b) {
		int result=0;
		PreparedStatement pstmt=null;
		String sql=prop.getProperty("insertBoard");
		
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, b.getCategoryNo());
			pstmt.setString(2, b.getBoardTitle());
			pstmt.setString(3, b.getBoardContent());
			pstmt.setString(4, b.getBoardWriter());
			
			result=pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(pstmt);
		}
		
		
		return result;
		
		
		
	}

	public int insertAttachment(Connection conn, Attachment at) {
		
		int result=0;
		PreparedStatement pstmt=null;
		String sql=prop.getProperty("insertAttachment");
		
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1,at.getOriginName());
			pstmt.setString(2, at.getChangeName());
			pstmt.setString(3, at.getFilePath());
			result=pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			JDBCTemplate.close(pstmt);
		}
		
		return result;
	}

	public int updateBoard(Connection conn, Board b) {
		int result=0;
		PreparedStatement pstmt=null;
		String sql=prop.getProperty("updateBoard");
				
				try {
					pstmt=conn.prepareStatement(sql);
					pstmt.setInt(1, b.getCategoryNo());
					pstmt.setString(2, b.getBoardTitle());
					pstmt.setString(3, b.getBoardContent());
					pstmt.setInt(4, b.getBoardNo());
					
					result=pstmt.executeUpdate();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					JDBCTemplate.close(pstmt);
				}
		return result;
	}

	public int updateAttachment(Connection conn, Attachment at) {
		int result=0;
		PreparedStatement pstmt=null;
		String sql=prop.getProperty("updateAttachment");
		
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, at.getOriginName());
			pstmt.setString(2, at.getChangeName());
			pstmt.setNString(3, at.getFilePath());
			pstmt.setInt(4, at.getFileNo());
			result=pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			JDBCTemplate.close(pstmt);
		}
		return result;
	}

	public int insertNewAttachment(Connection conn, Attachment at) {
		int result=0;
		PreparedStatement pstmt=null;
		String sql=prop.getProperty("insertNewAttachment");
		
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, at.getRefBno());
			pstmt.setString(2, at.getOriginName());
			pstmt.setString(3, at.getChangeName());
			pstmt.setString(4, at.getFilePath());
			result=pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			JDBCTemplate.close(pstmt);
		}
		return result;
	}

	public int deleteBoard(Connection conn, int boardNo) {
		int result=0;
		PreparedStatement pstmt=null;
		String sql=prop.getProperty("deleteBoard");
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, boardNo);
			result=pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			close(pstmt);
		}
		return result;
	}

	public void deleteAttachment(Connection conn, int boardNo) {
		PreparedStatement pstmt=null;
		String sql=prop.getProperty("deleteAttachment");
		
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, boardNo);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			close(pstmt);
		}
	}

	public ArrayList<Board> selectThumnailList(Connection conn) {
		
		ArrayList<Board> list=new ArrayList();
		PreparedStatement pstmt=null;
		ResultSet rset=null;
		String sql=prop.getProperty("selectThumnailList");
		
		try {
			pstmt=conn.prepareStatement(sql);
			rset=pstmt.executeQuery();
			
			while(rset.next()) {
				Attachment at=new Attachment();
				at.setFilePath(rset.getNString("FILE_PATH"));
				at.setChangeName(rset.getString("CHANGE_NAME"));
				Board b=new Board.Builder()
						.boardNo(rset.getInt("BOARD_NO"))
						.boardTitle(rset.getString("BOARD_TITLE"))
						.count(rset.getInt("COUNT"))
						.attachment(at)
						.build();
				list.add(b);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			JDBCTemplate.close(rset);
			JDBCTemplate.close(pstmt);
		}
		
		return list;
	}

	public int insertThumbnailBoard(Connection conn, Board b) {
		
		int result=0;
		PreparedStatement pstmt=null;
		
		String sql=prop.getProperty("insertThumbnailBoard");
		
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, b.getBoardContent());
			pstmt.setString(2, b.getBoardContent());
			pstmt.setString(3, b.getBoardWriter());
			
			result=pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCTemplate.close(pstmt);
		}
		return result;
		
	}
	
	public int insertAttachmentList(Connection conn, ArrayList<Attachment> list) {
		int result=1; // insert 여러번호줄
		PreparedStatement pstmt=null;
		String sql=prop.getProperty("insertAttachmentList");
		
		try {
			for(Attachment at : list) {
				// 반복문이 돌때 마다 미완성된 sql문을 담은 pstmt객체 생성
				pstmt=conn.prepareStatement(sql);
				
				// 완성형태로 만들기
				pstmt.setString(1, at.getOriginName());
				pstmt.setString(2, at.getChangeName());
				pstmt.setNString(3, at.getFilePath());
				pstmt.setInt(4, at.getFileLevel());
				
				// 실행
				result*=pstmt.executeUpdate();
			}
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			JDBCTemplate.close(pstmt);
		}
		return result;
	}

	public ArrayList<Attachment> selectAttachmentList(Connection conn, int boardNo) {
		ArrayList<Attachment> list=new ArrayList();
		PreparedStatement pstmt=null;
		
		ResultSet rset=null;
		
		String sql=prop.getProperty("selectAttachment");
		
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, boardNo);
			
			rset=pstmt.executeQuery();
			
			while(rset.next()) {
				Attachment at=new Attachment();
				
				at.setChangeName(rset.getString("CHANGE_NAME"));
				at.setFilePath(rset.getString("FILE_PATH"));
				
				list.add(at);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCTemplate.close(rset);
			JDBCTemplate.close(pstmt);
		}
		return list;	
	}
	
}
