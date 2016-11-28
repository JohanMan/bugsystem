package com.dc.ddureportreceiver;

public class Util {

	public static byte[] intToByte(int number) {
		byte[] abyte = new byte[4];
		// "&" �루AND�������������Ͳ������ж�Ӧλִ�в�������������λ��Ϊ1ʱ���1������0��
		abyte[0] = (byte) (0xff & number);
		// ">>"����λ����Ϊ�������λ��0����Ϊ�������λ��1
		abyte[1] = (byte) ((0xff00 & number) >> 8);
		abyte[2] = (byte) ((0xff0000 & number) >> 16);
		abyte[3] = (byte) ((0xff000000 & number) >> 24);
		return abyte;
	}

	public static int bytesToInt(byte[] bytes) {
		int number = bytes[0] & 0xFF;
		// "|="��λ��ֵ��
		number |= ((bytes[1] << 8) & 0xFF00);
		number |= ((bytes[2] << 16) & 0xFF0000);
		number |= ((bytes[3] << 24) & 0xFF000000);
		return number;
	}

	public byte[] combineTowBytes(byte[] bytes1, byte[] bytes2) {
		byte[] bytes3 = new byte[bytes1.length + bytes2.length];
		System.arraycopy(bytes1, 0, bytes3, 0, bytes1.length);
		System.arraycopy(bytes2, 0, bytes3, bytes1.length, bytes2.length);
		return bytes3;
	}

}
