package com.xg.mynotes.txt_xml;


import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.xg.mynotes.log.Logs;

import android.text.TextUtils;

public class MyDefaultHandler extends DefaultHandler {
	
	private List<Note> mRecords;
	private Note mNote;
	private String preTag = null;
	private String strElementData = "";
	
	private static final String TAG = "MyDefaultHandler";
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		if (localName.equals("record")) {
			mNote = new Note();
		}
		if (!TextUtils.isEmpty(strElementData)) {
			strElementData = "";
		}
		preTag = localName;
		
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		mRecords = new ArrayList<Note>();
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		Logs.e(TAG, "== > 当节点中有换行符时，characters()函数不止执行一次 " + System.currentTimeMillis());
		if (mNote != null) {
			String strContent = new String(ch, start, length);
			if (preTag.equals("id")) {
				mNote.setId(Integer.parseInt(strContent));
			} else if (preTag.equals("content")) {
				strElementData += strContent;
				Logs.d(TAG, "读取xml文件时＝＝ >characters()函数中的数据：" + strContent);
			} else if (preTag.equals("update_date")) {
				mNote.setUpdate_date(strContent);
			} else if (preTag.equals("update_time")) {
				mNote.setUpdate_time(strContent);
			} else if (preTag.equals("alarm_time")) {
				mNote.setAlarm_time(strContent);
			} else if (preTag.equals("background_color")) {
				mNote.setBackground_color(Integer.valueOf(strContent));
			} else if (preTag.equals("is_folder")) {
				mNote.setIs_folder(strContent);
			} else if (preTag.equals("parent_folder")) {
				mNote.setParent_folder(Integer.valueOf(strContent));
			}
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) {
		if (!TextUtils.isEmpty(strElementData)) {
			Logs.d(TAG, "从xml文件中读取到的完整的content节点的内容：" + strElementData);
			Logs.d(TAG, "从XML文件中读取到到完整的content节点的内容打印完毕！");
			mNote.setContent(strElementData);
		}
		if (localName.equals("record") && mNote != null) {
			mRecords.add(mNote);
			mNote = null;
		}
		preTag = null;
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
	public List<Note> getNotes() {
		
		return mRecords;
		
	}
	
}
