package com.inossem.print;

import java.io.UnsupportedEncodingException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.standard.PrinterName;

public class ZplPrint {
	private String printerURI = null;//打印机完整路径
	private PrintService printService = null;//打印机服务
	private String begin = "^XA^SEE:GB18030.DAT^CW1,E:SIMSUN.FNT"; //标签格式以^XA开始
	private String end = "^XZ"; //标签格式以^XZ结束
	private static String content = "";
	private static int cnCharSize = 25;
	private static int charSize = 20;
	private static int charSep = 10;
	private static int lineSep = 20;
	private static int height = 385;
	private static int lableLength = 5 * cnCharSize;
	private static int labelx = 530;
	private static int labely = 170;

	//二维码起始的x
	private static int bqx = 350;
	//二维码起始的y
	private static int bqy = 20;
	//底部内容起始的x
	private static int bottomx = 170;
	//底部内容起始的y
	private static int bottomy = 20;

	public static void execute(Order o) {
		ZplPrint p = new ZplPrint(o.getMachineName());
		String content_str = o.getBatchCode();
		//		//F0 x坐标，y坐标
		String qrcode_t = "^FO%s,%s^BQ,2,4^FDQA,${data}^FS";
		qrcode_t = String.format(qrcode_t, bqx, bqy);
		p.setBarcode(content_str, qrcode_t);
		content += "^FWR";
		int[] xy = new int[] { labelx, labely };
		String label1 = "批次号：";
		String value1 = o.getBatchCode();

		xy = setLabelValue(p, xy, label1, value1);

		xy = setLabelValue(p, xy, "采购订单：", o.getPurNo());

		xy = setLabelValue(p, xy, "供应商：", o.getSupplierDesc());
		xy = setLabelValue(p, xy, "合同号：", o.getContractNo());
		xy = setLabelValue(p, xy, "需求部门：", o.getReqDept());

		xy[0] = bottomx;
		xy[1] = bottomy;
		xy = setBottomLabelValue(p, xy, "物料编号：", o.getMaterielCode());
		xy = setBottomLabelValue(p, xy, "物料描述：", o.getMaterielDesc());
		xy = setBottomLabelValue(p, xy, "入库时间：", o.getInStorageDate());

		content += "^CI0^PQ1";//打印1张

		String zpl2 = p.getZpl();
		System.out.println("zpl:" + zpl2);
		p.print(zpl2);
	}

	private static int[] setLabelValue(ZplPrint p, int[] xy, String label1, String value1) {
		xy[1] = labely;
		xy = p.setText(label1, xy);
		xy[1] = labely + lableLength;
		xy = p.setText(value1, xy);
		xy[0] -= charSize + lineSep;
		return xy;
	}

	private static int[] setBottomLabelValue(ZplPrint p, int[] xy, String label1, String value1) {
		xy[1] = bottomy;
		xy = p.setText(label1, xy);
		xy[1] = bottomy + lableLength;
		xy = p.setText(value1, xy);
		xy[0] -= charSize + lineSep;
		return xy;
	}

	/** 
	* 构造方法 
	* @param printerURI 打印机路径 
	*/
	public ZplPrint(String printerURI) {
		this.printerURI = printerURI;
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		if (services != null && services.length > 0) {
			for (PrintService service : services) {
				if (printerURI.equals(service.getName())) {
					printService = service;
					break;
				}
			}
		}
		if (printService == null) {
			System.out.println("没有找到打印机：[" + printerURI + "]");
			//循环出所有的打印机  
			if (services != null && services.length > 0) {
				System.out.println("可用的打印机列表：");
				for (PrintService service : services) {
					System.out.println("[" + service.getName() + "]");
				}
			}
		} else {
			System.out.println("找到打印机：[" + printerURI + "]");
			System.out.println("打印机名称：[" + printService.getAttribute(PrinterName.class).getValue() + "]");
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

	public static boolean checkChar(char ch) {
		if ((ch + "").getBytes().length == 1) {
			return true;//英文
		} else {
			return false;//中文
		}
	}

	public int[] setText(String str, int[] xy) {
		int x = xy[0];
		int y = xy[1];
		if(str!=null){
			char[] charArray = str.toCharArray();
			int initY = y;
			for (int off = 0; off < charArray.length;) {
				char c = charArray[off];
				if (!checkChar(c)) {
					setCharR(String.valueOf(c), x, y, true);
					y = y + cnCharSize;
				} else {
					setCharR(String.valueOf(c), x, y, false);
					y = y + charSep;
				}

				if (y >= height) {
					y = initY;
					x -= charSize + lineSep;
				}
				off++;
			}
		}
		
		return new int[] { x, y };
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
	public void setCharR(String str, int x, int y, boolean cn) {
		if (cn) {
			content += "^CI14";
			content += "^FO" + x + "," + y + "^A1R," + cnCharSize + "," + cnCharSize + "^FD" + str + "^FS";
		} else {
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
		byte[] by = null;
		try {
			by = zpl.getBytes("GB18030");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		Doc doc = new SimpleDoc(by, flavor, null);
		try {
			job.print(doc, null);
			System.out.println("已打印");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
