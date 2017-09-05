package com.inossem.print;

import java.io.UnsupportedEncodingException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.standard.PrinterName;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZplPrint {
	private String printerURI = null;//打印机完整路径
	private PrintService printService = null;//打印机服务
	private String begin = "^XA^SEE:GB18030.DAT^CW1,E:SIMSUN.FNT"; //标签格式以^XA开始，字符集设置为GB18030，字体为宋体
	private String end = "^XZ"; //标签格式以^XZ结束
	private String content = "";
	//中文字符尺寸
	private int cnCharSize = 25;
//	英文字符尺寸
	private int charSize = 20;
	private int charSep = 10;
	private int lineSep = 20;
	//打印纸宽度 x
	private int width =500;
	//打印纸高度 y
	//小纸张 
	private  int height = 385;
//	private int height = 750;
	private int lableLength = 5 * cnCharSize;
	private int labelx = width - 20;
	private int labely = height / 12 * 5;

	//二维码起始的x
	private int bqx = width / 12 * 5 + 20;
	//二维码起始的y
	private int bqy = 20;
	//底部内容起始的x
	private int bottomx = bqx - 30;
	//底部内容起始的y
	private int bottomy = 20;

	public void execute(Order o) {
		this.init(o.getMachineName());
		String content_str =o.getMaterielCode()+","+o.getBatchCode();
		//		//F0 x坐标，y坐标
		String qrcode_t = "^FO%s,%s^BQ,2,4^FDQA,${data}^FS";
		qrcode_t = String.format(qrcode_t, bqx, bqy);
		this.setBarcode(content_str, qrcode_t);
		content += "^FWR";
		int[] xy = new int[] { labelx, labely };
		String label1 = "批次号：";
		String value1 = o.getBatchCode();

		xy = setLabelValue(this, xy, label1, value1);

		xy = setLabelValue(this, xy, "采购订单：", o.getPurNo());

		xy = setLabelValue(this, xy, "供应商：", o.getSupplierDesc());
		xy = setLabelValue(this, xy, "合同号：", o.getContractNo());
		xy = setLabelValue(this, xy, "需求部门：", o.getReqDept());

		xy[0] = bottomx;
		xy[1] = bottomy;
		xy = setBottomLabelValue(this, xy, "物料编号：", o.getMaterielCode());
		xy = setBottomLabelValue(this, xy, "物料描述：", o.getMaterielDesc());
		xy = setBottomLabelValue(this, xy, "入库时间：", o.getInStorageDate());

		content += "^CI0^PQ1";//打印1张

		String zpl2 = this.getZpl();
		System.out.println("zpl:" + zpl2);
		this.print(zpl2);
	}

	/**
	 * 设置标签值
	 * @param p
	 * @param xy
	 * @param label1
	 * @param value1
	 * @return
	 */
	private int[] setLabelValue(ZplPrint p, int[] xy, String label1, String value1) {
		xy[1] = labely;
		xy = p.setText(label1, xy);
		xy[1] = labely + lableLength;
		xy = p.setText(value1, xy);
		xy[0] -= charSize + lineSep;
		return xy;
	}

	/**
	 * 设置底部标签值
	 * @param p
	 * @param xy
	 * @param label1
	 * @param value1
	 * @return
	 */
	private int[] setBottomLabelValue(ZplPrint p, int[] xy, String label1, String value1) {
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
	private void init(String printerURI) {
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
	private void setBarcode(String barcode, String zpl) {
		content += zpl.replace("${data}", barcode);
	}

	private boolean checkChar(char ch) {
		if ((ch + "").getBytes().length == 1) {
			return true;//英文
		} else {
			return false;//中文
		}
	}

	private int[] setText(String str, int[] xy) {
		int x = xy[0];
		int y = xy[1];
		if (str != null) {
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
	private void setChar(String str, int x, int y, int h, int w) {
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
	private void setCharR(String str, int x, int y, boolean cn) {
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
	private String getZpl() {
		return begin + content + end;
	}

	/**
	* 重置ZPL指令，当需要打印多张纸的时候需要调用。
	*/
	private void resetZpl() {
		begin = "^XA";
		end = "^XZ";
		content = "";
	}

	/**
	* 打印
	* @param zpl 完整的ZPL
	*/
	private boolean print(String zpl) {
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
	
	public void setWidth(int width) {
		this.width = width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	

}
