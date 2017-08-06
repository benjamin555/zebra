package com.thinkgem.jeesite.test;

public class TestNative {
	private native static int GETFONTHEX(String str,String font,int i,int j,int k,int l,int m);


	public static void main(String[] args) {
		System.out.println(GETFONTHEX("中文","宋体",0,14,0,1,0));
	}

	static {
		System.loadLibrary("Fnthex32");
	}
}