/*
 * Copyright (c) www.spyatsea.com  2014 
 */
package com.cox.android.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.cox.utils.CommonUtil;

/**
 * 用于解析xml流。
 * 
 * @author 乔勇(Jacky Qiao)
 * */
// @SuppressWarnings({ "unchecked", "rawtypes" })
public class XmlTableParser {
	private static final String ns = null;

	/**
	 * 读取 XML 的内容
	 * 
	 * @return xmlStr {@code String} xml文本
	 * */
	public Map<String, Object> parse(String xmlStr) throws XmlPullParserException, IOException {
		StringReader reader = new StringReader(xmlStr);
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(reader);
			parser.nextTag();
			return readDataset(parser);
		} catch (XmlPullParserException e1) {
			throw e1;
		} catch (IOException e2) {
			throw e2;
		} finally {
			reader.close();
		}
	}

	/**
	 * 读取 NewDataSet 节点的内容
	 * */
	private Map<String, Object> readDataset(XmlPullParser parser) throws XmlPullParserException, IOException {
		// 结果集
		Map<String, Object> dataset = new HashMap<String, Object>();
		// 表内容List
		List<HashMap<String, Object>> records = new ArrayList<HashMap<String, Object>>();
		// 表头List
		List<String> columns = null;
		// Map<String, Object> config = null;
		// 根节点
		parser.require(XmlPullParser.START_TAG, ns, "NewDataSet");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			// Table节点
			if (name.equals("Table")) {
				// 数据表内容
				records.add(readTag(parser, ns, "Table"));
			} else if (name.equals("Columns")) {
				// 数据表列定义
				columns = readColumns(parser, ns, "Columns");
			} else {
				skip(parser);
			}
		}
		if (columns == null) {
			columns = new ArrayList<String>();
		}
		dataset.put("record", records);
		dataset.put("columns", columns);
		return dataset;
	}

	/**
	 * 读取数据表列节点的内容
	 * */
	private List<String> readColumns(XmlPullParser parser, String namespace, String tagName)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, namespace, tagName);
		List<String> columns = new ArrayList<String>();
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();

			if (name.equals("Column")) {
				columns.add(parser.getAttributeValue(ns, "name"));
				skip(parser);
			}
		}

		return columns;
	}

	/**
	 * 读取节点的内容
	 * */
	private HashMap<String, Object> readTag(XmlPullParser parser, String namespace, String tagName)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, namespace, tagName);
		HashMap<String, Object> record = new HashMap<String, Object>();
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (CommonUtil.checkNB(name)) {
				record.put(name, readText(parser));
			} else {
				skip(parser);
			}
		}
		parser.require(XmlPullParser.END_TAG, namespace, tagName);
		return record;
	}

	/**
	 * 读取 TEXT 节点的内容
	 * */
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	// Skips tags the parser isn't interested in. Uses depth to handle nested
	// tags. i.e.,
	// if the next tag after a START_TAG isn't a matching END_TAG, it keeps
	// going until it
	// finds the matching END_TAG (as indicated by the value of "depth" being
	// 0).
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
}
