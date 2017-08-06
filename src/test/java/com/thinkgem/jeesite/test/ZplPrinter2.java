package com.thinkgem.jeesite.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterName;

public class ZplPrinter2 {
	private String printerURI = null;//打印机完整路径
	private PrintService printService = null;//打印机服务
	private byte[] dotFont;
	private String begin = "^XA^SEE:GB18030.DAT^CW1,E:SIMSUN.FNT^PON^LH0,0"; //标签格式以^XA开始
	private String end = "^XZ"; //标签格式以^XZ结束
	private static String content = "";
	private static int cnCharSize = 25;
	private static int charSize = 20;
	
//	^XA
//
//	^CI26  //ASCII Transparency和多字节亚洲编码
//
//	^SEE:GB18030.DAT  //码表
//
//	^CW1,E:SIMSUN.FNT  //字体（宋体）
//
//	^FO200,200^A1N,48,48^FD中文^FS //打印文字
//
//	^FT448,288^BQ2,2,10^A1N,48,48^FD中文^FS  //打印二维码
//
//	^XZ

	public static void main(String[] args) throws IOException {
		ZplPrinter2 p = new ZplPrinter2("ZDesigner GK888t_ol");
		String content_str = "##|200050|25100400001|100|20161019|201101-03|820005016101900393##";
//		//F0 x坐标，y坐标
		String qrcode_t = "^FO530,20^BQ,2,3^FDQA,${data}^FS";
		p.setBarcode(content_str, qrcode_t);
		
		content+="^FWR";
		int lableLength = 5* cnCharSize;
		int labelx = 530;
		int labely = 180;
		
		p.setCharR("批次号：", labelx, labely,true);
		p.setCharR("10003", labelx, labely+lableLength,false);
		labelx-=cnCharSize*2;
		p.setCharR("采购订单：", labelx, labely,true);
		p.setCharR("4000300088/0010", labelx, labely+lableLength,false);
		content += "^CI0^PQ1";//打印1张
		
//		content = "^XA^FO150,100^BY3^B4N,20,A,A^FD12345ABCDE^FS^XZ";
		String zpl2 = p.getZpl();
		System.out.println("zpl:"+zpl2);
		p.print(zpl2);
	}

	 /** 
     * 构造方法 
     * @param printerURI 打印机路径 
     */  
    public ZplPrinter2(String printerURI){  
        this.printerURI = printerURI;  
        //加载字体  
        File file = new File("C://ts24.lib");  
        if(file.exists()){  
            FileInputStream fis;  
            try {  
                fis = new FileInputStream(file);  
                dotFont = new byte[fis.available()];  
                fis.read(dotFont);  
                fis.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }else{  
            System.out.println("C://ts24.lib文件不存在");  
        }  
        //初始化打印机  
//        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;  
//        DocAttributeSet attrs = new HashDocAttributeSet(); 
//        attrs.add(OrientationRequested.PORTRAIT); 
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null,null);  
        if (services != null && services.length > 0) {  
            for (PrintService service : services) {  
                if (printerURI.equals(service.getName())) {  
                    printService = service;  
                    break;  
                }  
            }  
        }  
        if (printService == null) {  
            System.out.println("没有找到打印机：["+printerURI+"]");  
            //循环出所有的打印机  
            if (services != null && services.length > 0) {  
                System.out.println("可用的打印机列表：");  
                for (PrintService service : services) {  
                    System.out.println("["+service.getName()+"]");  
                }  
            }  
        }else{  
            System.out.println("找到打印机：["+printerURI+"]");  
            System.out.println("打印机名称：["+printService.getAttribute(PrinterName.class).getValue()+"]");  
        }  
    }  

	/**
	* 设置条形码
	* @param barcode 条码字符
	* @param zpl 条码样式模板
	*/
	public void setBarcode(String barcode, String zpl) {
		content += zpl.replace("${data}", barcode);
	}


	/**
	*字符串(包含数字)
	* @param str 字符串
	* @param x x坐标
	* @param y y坐标
	* @param h 高度
	* @param w 宽度
	*/
	public void setChar(String str, int x, int y, int h, int w) {
		content += "^FO" + x + "," + y + "^A0," + h + "," + w + "^FD" + str + "^FS";
	}

	/**
	* 字符(包含数字)顺时针旋转90度
	* @param str 字符串
	* @param x x坐标
	* @param y y坐标
	* @param h 高度
	* @param w 宽度
	* @param cn 是否为中文
	*/
	public void setCharR(String str, int x, int y,boolean cn) {
		if(cn){
			content += "^CI14";
			content += "^FO" + x + "," + y + "^A1R," + cnCharSize + "," + cnCharSize + "^FD" + str + "^FS";
		}else{
			content += "^CI0";
			content += "^FO" + x + "," + y + "^A0R," + charSize + "," + charSize + "^FD" + str + "^FS";
		}
		
	}

	/**
	* 获取完整的ZPL
	* @return
	*/
	public String getZpl() {
		return begin + content + end;
	}

	/**
	* 重置ZPL指令，当需要打印多张纸的时候需要调用。
	*/
	public void resetZpl() {
		begin = "^XA";
		end = "^XZ";
		content = "";
	}

	/**
	* 打印
	* @param zpl 完整的ZPL
	*/
	public boolean print(String zpl) {
		if (printService == null) {
			System.out.println("打印出错：没有找到打印机：[" + printerURI + "]");
			return false;
		}
		DocPrintJob job = printService.createPrintJob();
		byte[] by =null;
		try {
			by = zpl.getBytes("GB18030");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		DocAttributeSet das = new HashDocAttributeSet();
		das.add(OrientationRequested.LANDSCAPE);
		das.add(new MediaPrintableArea(0, 0, 50, 70, MediaPrintableArea.MM));
		Doc doc = new SimpleDoc(by, flavor, das);
		
		try {
//			PrintRequestAttributeSet rpa =new HashPrintRequestAttributeSet();
//			rpa.add(OrientationRequested.LANDSCAPE);
//			rpa.add(MediaSize.findMedia(2, 1, Size2DSyntax.INCH)); 
			job.print(doc, null);
			
			System.out.println("已打印");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	* 字符串转byte[]
	* @param s
	* @return
	*/
	private byte[] str2bytes(String s) {
		if (null == s || "".equals(s)) {
			return null;
		}
		byte[] abytes = null;
		try {
			abytes = s.getBytes("gb2312");
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return abytes;
	}

	/**
	* 过滤特殊字符
	* @param content
	* @return
	*/
	public static String filterSpecialChar(String content) {
		StringBuffer sb = new StringBuffer();
		String[] array = content.split("");
		for (String str : array) {
			if (str.matches("\\w|[\u4e00-\u9fa5]|\\*|\\.")) {
				sb.append(str);
			}
		}
		return sb.toString();
	}
}