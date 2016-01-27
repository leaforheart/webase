/**
*@author jw233233 下午05:14:38
*/
package com.inveno.cps.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Random;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class AuthCode {
	
	public enum DiscuzAuthcodeMode {
		Encode, Decode
	};

	public static String CutString(String str, int startIndex, int length) {
		if (startIndex >= 0) {
			if (length < 0) {
				length = length * -1;
				if (startIndex - length < 0) {
					length = startIndex;
					startIndex = 0;
				} else {
					startIndex = startIndex - length;
				}
			}
			if (startIndex > str.length()) {
				return "";
			}
		} else {
			if (length < 0) {
				return "";
			} else {
				if (length + startIndex > 0) {
					length = length + startIndex;
					startIndex = 0;
				} else {
					return "";
				}
			}
		}
		if (str.length() - startIndex < length) {
			length = str.length() - startIndex;
		}
		return str.substring(startIndex, startIndex + length);
	}

	public static String CutString(String str, int startIndex) {
		return CutString(str, startIndex, str.length());
	}

	public static String MD5(String pass) {
		byte[] defaultBytes = pass.getBytes();
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(defaultBytes);
			byte messageDigest[] = algorithm.digest();
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String hex = Integer.toHexString(0xFF & messageDigest[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException nsae) {

		}
		return "";
	}

	public static boolean StrIsNullOrEmpty(String str) {
		// #if NET1
		if (str == null || str.trim().equals("")) {
			return true;
		}
		return false;
	}

	static private byte[] GetKey(byte[] pass, int kLen) {
		byte[] mBox = new byte[kLen];
		for (int i = 0; i < kLen; i++) {
			mBox[i] = (byte) i;
		}
		int j = 0;
		for (int i = 0; i < kLen; i++) {
			j = (j + (int) ((mBox[i] + 256) % 256) + pass[i % pass.length])
					% kLen;
			byte temp = mBox[i];
			mBox[i] = mBox[j];
			mBox[j] = temp;
		}
		return mBox;
	}

	public static String RandomString(int lens) {
		char[] CharArray = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k',
				'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
				'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		int clens = CharArray.length;
		String sCode = "";
		Random random = new Random();
		for (int i = 0; i < lens; i++) {
			sCode += CharArray[Math.abs(random.nextInt(clens))];
		}
		return sCode;
	}

	public static String authcodeEncode(String source, String key, int expiry) {
		return authcode(source, key, DiscuzAuthcodeMode.Encode, expiry);
	}

	public static String authcodeEncode(String source, String key) {
		return authcode(source, key, DiscuzAuthcodeMode.Encode, 0);
	}

	public static String authcodeDecode(String source, String key) {
		return authcode(source, key, DiscuzAuthcodeMode.Decode, 0);
	}

	private static String authcode(String source, String key,
			DiscuzAuthcodeMode operation, int expiry) {
		try {
			if (source == null || key == null) {
				return "";
			}
			int ckey_length = 4;
			String keya, keyb, keyc, cryptkey, result;

			key = MD5(key);
			keya = MD5(CutString(key, 0, 16));
			keyb = MD5(CutString(key, 16, 16));
			keyc = ckey_length > 0 ? (operation == DiscuzAuthcodeMode.Decode ? CutString(
					source, 0, ckey_length)
					: RandomString(ckey_length))
					: "";

			cryptkey = keya + MD5(keya + keyc);
			if (operation == DiscuzAuthcodeMode.Decode) {
				byte[] temp;

				temp = Base64.decode(CutString(source, ckey_length));
				result = new String(RC4(temp, cryptkey));
				if (CutString(result, 10, 16).equals(
						CutString(MD5(CutString(result, 26) + keyb), 0, 16))) {
					return CutString(result, 26);
				} else {
					temp = Base64.decode(CutString(source + "=", ckey_length));
					result = new String(RC4(temp, cryptkey));
					if (CutString(result, 10, 16)
							.equals(
									CutString(
											MD5(CutString(result, 26) + keyb),
											0, 16))) {
						return CutString(result, 26);
					} else {
						temp = Base64.decode(CutString(source + "==",
								ckey_length));
						result = new String(RC4(temp, cryptkey));
						if (CutString(result, 10, 16).equals(
								CutString(MD5(CutString(result, 26) + keyb), 0,
										16))) {
							return CutString(result, 26);
						} else {
							return "2";
						}
					}
				}
			} else {
				source = "0000000000" + CutString(MD5(source + keyb), 0, 16)
						+ source;
				byte[] temp = RC4(source.getBytes("GBK"), cryptkey);
				return keyc + Base64.encode(temp);
			}
		} catch (Exception e) {
			return "";
		}
	}

	private static byte[] RC4(byte[] input, String pass) {
		if (input == null || pass == null)
			return null;

		byte[] output = new byte[input.length];
		byte[] mBox = GetKey(pass.getBytes(), 256);

		int i = 0;
		int j = 0;
		for (int offset = 0; offset < input.length; offset++) {
			i = (i + 1) % mBox.length;
			j = (j + (int) ((mBox[i] + 256) % 256)) % mBox.length;
			byte temp = mBox[i];
			mBox[i] = mBox[j];
			mBox[j] = temp;
			byte a = input[offset];

			byte b = mBox[(toInt(mBox[i]) + toInt(mBox[j])) % mBox.length];
			output[offset] = (byte) ((int) a ^ (int) toInt(b));
		}
		return output;
	}

	public static int toInt(byte b) {
		return (int) ((b + 256) % 256);
	}

	public long getUnixTimestamp() {
		Calendar cal = Calendar.getInstance();
		return cal.getTimeInMillis() / 1000;
	}

	public static void main(String[] args) {
		String test = "xjr1107";
		String key = "inveno";
		System.out.println(AuthCode.authcodeEncode(test, key));
		System.out.println(AuthCode.authcodeDecode("4qlnXjCGyyXDq4J9NsEIH4DelM09yTZqp8V2XWYROlnQGBem", key));
		
		//System.out.println(AuthCode.authcodeDecode("gyn8gdwo0qAi4GRoGiZQi0Mgx3oAk9p2E6qioTOyY8446uE/SAtmaNgeTGLMiwet2Uu", key));
		//String deStr = AuthCode.authcodeDecode("0084tuF6jOu8bVvO//fcV6fXL/CCcUYVJby2nQOofjRasbvrqYNupR6eQJ2rDnhh1XvxWTft4Ub5TSdZA2Y3Ts0yhH8UrziYy5dXl3MHC5freHTOdAfgfFofcnQvLwo+BvD1hT7J9qw57Ral4NC+KNTc/Vj1CzPpftA5P6qUO3KB",key);
		//System.out.println("--------decode:" + deStr);
	}

}