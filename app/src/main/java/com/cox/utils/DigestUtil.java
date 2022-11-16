/*
 * Copyright (c) www.spyatsea.com  2011
 */
package com.cox.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

//import org.apache.commons.codec.digest.DigestUtils;

/**
 * 进行加密的工具类
 *
 * @author 乔勇(Jacky Qiao)
 */
public class DigestUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str = "a";// 0cc175b9c0f1b6a831c399e269772661
		// System.out.println(apacheMD5(str));
		// System.out.println(netMD5(str));
		// System.out.println(DigestUtils.md5Hex(str));
	}

	/**
	 * md5检验
	 *
	 * @param inputText {@code String} 要校验的字符串
	 * @return {@code String} 校验生成的md5字符串
	 */
	public static String md5(String inputText) {
		return apacheMD5(inputText);
		// return netMD5(inputText);
		// return DigestUtils.md5Hex(inputText);
	}

	/**
	 * sha-1检验
	 *
	 * @param inputText {@code String} 要校验的字符串
	 * @return {@code String} 校验生成的sha-1字符串
	 */
	public static String sha(String inputText) {
		return encrypt(inputText, "sha-1");
	}

	/**
	 * md5检验
	 * <p>
	 * 本方法根据网上的方法改写而成。
	 *
	 * @param inputText {@code String} 要校验的字符串
	 * @return {@code String} 校验生成的md5字符串
	 */
	public static String netMD5(String inputText) {
		return encrypt(inputText, "md5");
	}

	/**
	 * md5检验
	 * <p>
	 * 本方法根据org.apache.commons.codec.digest.DigestUtils类中的方法改写而成。
	 *
	 * @param inputText {@code String} 要校验的字符串
	 * @return {@code String} 校验生成的md5字符串
	 */
	public static String apacheMD5(String inputText) {
		String encryptStr = null;
		try {
			byte s[] = MessageDigest.getInstance("md5").digest(inputText.getBytes("UTF-8"));
			encryptStr = new String(encodeHex(s, true));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encryptStr;
	}

	/**
	 * md5或sha-1校验
	 *
	 * @param inputText     {@code String} 要校验的字符串
	 * @param algorithmName {@code String} 检验算法
	 * @return {@code String} 校验生成的字符串
	 */
	public static String encrypt(String inputText, String algorithmName) {
		if (inputText == null || "".equals(inputText.trim())) {
			return null;
		}
		if (algorithmName == null || "".equals(algorithmName.trim())) {
			algorithmName = "md5";
		} else {
			algorithmName.toLowerCase(Locale.ENGLISH);
		}
		String encryptStr = null;
		try {
			byte s[] = MessageDigest.getInstance(algorithmName).digest(inputText.getBytes("UTF-8"));
			return hex(s);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encryptStr;
	}

	/**
	 * 将byte数组转变为十六进制字符串
	 *
	 * @param arr {@code byte[]} 待转换的byte数组
	 * @return {@code} 转换生成的十六进制字符串
	 */
	private static String hex(byte[] arr) {
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < arr.length; i++) {
			sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString();
	}

	/**
	 * Used to build output as Hex
	 */
	private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f'};

	/**
	 * Used to build output as Hex
	 */
	private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F'};

	/**
	 * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
	 * The returned array will be double the length of the passed array, as it takes two characters to represent any
	 * given byte.
	 *
	 * @param data        a byte[] to convert to Hex characters
	 * @param toLowerCase <code>true</code> converts to lowercase, <code>false</code> to uppercase
	 * @return A char[] containing hexadecimal characters
	 * @since 1.4
	 */
	public static char[] encodeHex(byte[] data, boolean toLowerCase) {
		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	/**
	 * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
	 * The returned array will be double the length of the passed array, as it takes two characters to represent any
	 * given byte.
	 *
	 * @param data     a byte[] to convert to Hex characters
	 * @param toDigits the output alphabet
	 * @return A char[] containing hexadecimal characters
	 */
	protected static char[] encodeHex(byte[] data, char[] toDigits) {
		int l = data.length;
		char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
		}
		return out;
	}

}
