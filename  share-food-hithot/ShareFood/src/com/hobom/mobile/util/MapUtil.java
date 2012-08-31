package com.hobom.mobile.util;


/**
 * ��ͼ������
 * @author mingxunzh
 *
 */
public class MapUtil {

	public static final double PI = 3.1415926535897931;

	public static final int TILE_WIDTH = 256;
	public static final int TILE_HEIGHT = 256;

	private static int WIN_WIDTH = 480;
	private static int WIN_HEIGHT = 800;
	private static final double EARTH_R = 6371004.00; // ����ƽ���뾶, ��λ: ��

	
	public static void setScreenSize(int width, int height) {
		WIN_WIDTH = width;
		WIN_HEIGHT = height;
	}

	public static int getScreenWidth() {
		return WIN_WIDTH;
	}

	public static int getScreenHeight() {
		return WIN_HEIGHT;
	}

	
}
