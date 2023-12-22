package com.kh.board.model.vo;

import java.sql.Date;

public class Board {
	private int boardNo;
	
	private int boardType;
	
	private int categoryNo;
	
	private String boardTitle;
	
	private String boardContent;
	
	private String boardWriter;
	// 쿼리문으로 join시에는 회원번호를 활용하여 join할것이고, 조회결과 작성한 사용자의 이름orid를 저장하기위한 필드
	
	private int count;
	
	private Date createDate;
	
	private String status;
	
	private String categoryName; // board테이블에는 존재하지 않는 칼럼
	
	private Attachment attachment;
	
	public Board() {
		
	}

	public static class Builder {
		private int boardNo;
		private int boardType;
		private int categoryNo;
		private String boardTitle;
		private String boardContent;
		private String boardWriter;
		private int count;
		private Date createDate;
		private String status;
		private String categoryName;
		private Attachment attachment;
		
		public Board build() {
			Board b = new Board();
			b.boardNo = boardNo;
			b.boardType = boardType;
			b.categoryNo = categoryNo;
			b.boardTitle = boardTitle;
			b.boardContent = boardContent;
			b.boardWriter = boardWriter;
			b.count = count;
			b.createDate = createDate;
			b.status = status;
			b.categoryName = categoryName;
			b.attachment = attachment;
			return b;
		}
		
		public Builder boardNo(int boardNo) {
			this.boardNo = boardNo;
			return this;
		}
		
		public Builder boardType(int boardType) {
			this.boardType = boardType;
			return this;
		}
		
		public Builder categoryNo(int categoryNo) {
			this.categoryNo = categoryNo;
			return this;
		}
		
		public Builder boardTitle(String boardTitle) {
			this.boardTitle = boardTitle;
			return this;
		}
		
		public Builder boardContent(String boardContent) {
			this.boardContent = boardContent;
			return this;
		}
		
		public Builder boardWriter(String boardWriter) {
			this.boardWriter = boardWriter;
			return this;
		}
		
		public Builder count(int count) {
			this.count = count;
			return this;
		}
		
		public Builder createDate(Date createDate) {
			this.createDate = createDate;
			return this;
		}
		
		public Builder status(String status) {
			this.status = status;
			return this;
		}
		
		public Builder categoryName(String categoryName) {
			this.categoryName = categoryName;
			return this;
		}
		
		public Builder attachment(Attachment attachment) {
			this.attachment = attachment;
			return this;
		}
	}
	
	@Override
	public String toString() {
		return "Board [boardNo=" + boardNo + ", boardType=" + boardType + ", categoryNo=" + categoryNo + ", boardTitle="
				+ boardTitle + ", boardContent=" + boardContent + ", boardWriter=" + boardWriter + ", count=" + count
				+ ", createDate=" + createDate + ", status=" + status + ", categoryName=" + categoryName
				+ ", attachment=" + attachment + "]";
	}

	public int getBoardNo() {
		return boardNo;
	}

	public void setBoardNo(int boardNo) {
		this.boardNo = boardNo;
	}

	public int getBoradType() {
		return boardType;
	}

	public void setBoradType(int boradType) {
		this.boardType = boradType;
	}

	public int getCategoryNo() {
		return categoryNo;
	}

	public void setCategoryNo(int categoryNo) {
		this.categoryNo = categoryNo;
	}

	public String getBoardTitle() {
		return boardTitle;
	}

	public void setBoardTitle(String boardTitle) {
		this.boardTitle = boardTitle;
	}

	public String getBoardContent() {
		return boardContent;
	}

	public void setBoardContent(String boardContent) {
		this.boardContent = boardContent;
	}

	public String getBoardWriter() {
		return boardWriter;
	}

	public void setBoardWriter(String boardWriter) {
		this.boardWriter = boardWriter;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Attachment getAttachment() {
		return attachment;
	}

	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}
	
	

}