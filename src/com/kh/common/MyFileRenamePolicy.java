package com.kh.common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.oreilly.servlet.multipart.FileRenamePolicy;

public class MyFileRenamePolicy implements FileRenamePolicy{
	public File rename(File originFile) {
		// 기존의 파일을 매개변수로 전달받아서 (originFile)파일명 수정작업 후에 수정된 파일을 반환해주는 메서드
		
		// 원본파일명("aaa;.jpg")
		String originName=originFile.getName();
		
		// 수정파일명 : 파일업로드된 시간(년 월일 시분초) + 5자리 랜덤값 = > 최대한 한겹치게함
		// 확장자 : 원본파일의 확장자 글대로 사용
		
		// aaa.jpg ----> 2023121114374512345.ㅓㅔㅎ
		// 1. 파일로드된 시간(년월일시분초)
		String currentTime=new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		
		// 2. 5자리 랜덤값 => int ranNum;
		int ranNum=(int)((Math.random()*90000)+10000); // 10000~99999
		
		// 3. 원본파일 확장자 (String ext)
		String ext= originName.substring(originName.lastIndexOf(".")); // .jpg
		
		String changeName=currentTime+ranNum+ext;
		
		
		return new File(originFile.getParent(),changeName);
		
	}
}
