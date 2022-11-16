/*
 * Copyright (c) www.spyatsea.com  2014 
 */
package com.cox.android.utils;

import java.io.IOException;
import java.io.InputStream;
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
public class XmlParser {
	private static final String ns = null;

	/**
	 * 读取 XML 的内容
	 * */
	public Map<String, Object> parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readDataset(parser);
		} finally {
			in.close();
		}
	}

	/**
	 * 读取 dataset 节点的内容
	 * */
	private Map<String, Object> readDataset(XmlPullParser parser) throws XmlPullParserException, IOException {
		Map<String, Object> dataset = new HashMap<String, Object>();
		List<HashMap<String, Object>> records = null;
		Map<String, Object> config = null;

		parser.require(XmlPullParser.START_TAG, ns, "dataset");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("record")) {
				records = readRecords(parser);
			} else if (name.equals("config")) {
				config = readTag(parser, "config");
			} else {
				skip(parser);
			}
		}
		dataset.put("record", records);
		dataset.put("config", config);
		return dataset;
	}

	/**
	 * 读取 records 节点的内容
	 * */
	private List<HashMap<String, Object>> readRecords(XmlPullParser parser) throws XmlPullParserException, IOException {
		List<HashMap<String, Object>> records = new ArrayList<HashMap<String, Object>>();

		parser.require(XmlPullParser.START_TAG, ns, "record");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("record")) {
				records.add(readTag(parser, "record"));
			} else {
				skip(parser);
			}
		}
		return records;
	}

	/**
	 * 读取节点的内容
	 * */
	private HashMap<String, Object> readTag(XmlPullParser parser, String tagName) throws XmlPullParserException,
			IOException {
		parser.require(XmlPullParser.START_TAG, ns, tagName);
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

	// Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
	// if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
	// finds the matching END_TAG (as indicated by the value of "depth" being 0).
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
