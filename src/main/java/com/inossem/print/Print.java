package com.inossem.print;

import java.util.ArrayList;
import java.util.List;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;

import org.apache.commons.lang3.StringUtils;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * **************************** CreateTime Dec 30, 2013 11:54:15 AM Author
 * MaBingYang FileName Print.java FilePath com.common Explain 热敏打印机
 * ******************************
 */
public class Print {

	/**
	 * 顶部字段区域开始Y值
	 */
	private static final int TOP_FIELD_START_Y = 450;

	/**
	 * 顶部字段值区域开始Y值
	 */
	private static final int TOP_FIELD_VALUE_START_Y = 320;

	/**
	 * 底部字段区域开始Y值
	 */
	private static final int BOT_FIELD_START_Y = 700;

	/**
	 * 底部字段值区域开始Y值
	 */
	private static final int BOT_FIELD_VALUE_START_Y = 570;

	/**
	 * 字符高度
	 */
	private static final int FONT_HEIGHT = 28;

	/**
	 * 位移调整
	 */
	private static int moveAdjust = 0;

	public interface TSCLIB_DLL_GP3150TN extends Library {
		String path = Print.class.getResource("/").getPath().substring(1) + "TSCLIB";
		TSCLIB_DLL_GP3150TN INSTANCE = (TSCLIB_DLL_GP3150TN) Native.loadLibrary(path, TSCLIB_DLL_GP3150TN.class);

		int about();

		int openport(String pirnterName);

		int closeport();

		int sendcommand(String printerCommand);

		int setup(String width, String height, String speed, String density, String sensor, String vertical,
				String offset);

		int downloadpcx(String filename, String image_name);

		int barcode(String x, String y, String type, String height, String readable, String rotation, String narrow,
				String wide, String code);

		int printerfont(String x, String y, String fonttype, String rotation, String xmul, String ymul, String text);

		int clearbuffer();

		int printlabel(String set, String copy);

		int formfeed();

		int nobackfeed();

		int windowsfont(int x, int y, int fontheight, int rotation, int fontstyle, int fontunderline,
				String szFaceName, String content);
	}

	/**
	 * 绘制二维码指令 功能：繪製QRCODE二維條碼 語法： QRCODE X, Y, ECC Level, cell width, mode,
	 * rotation, [model, mask,]"Data string” 參數說明 X QRCODE條碼左上角X座標 Y
	 * QRCODE條碼左上角Y座標 ECC level 錯誤糾正能力等級 L 7% M 15% Q 25% H 30% cell width 1~10
	 * mode 自動生成編碼/手動生成編碼 A Auto M Manual rotation 順時針旋轉角度 0 不旋轉 90 順時針旋轉90度 180
	 * 順時針旋轉180度 270 順時針旋轉270度 model 條碼生成樣式 1 (預設), 原始版本 2 擴大版本 mask 範圍：0~8，預設7
	 * Data string 條碼資料內容 Author MaBingYang
	 * 
	 * @date Dec 30, 2013 12:42:48 PM
	 * @param barCode
	 * @return
	 */
	public static String CMD_QRCODE_FROMT_GP3150TN(String barCode, int index) {
		int y = 470 + (moveAdjust * index);
		StringBuffer sb = new StringBuffer("QRCODE");
		sb.append(" ");
		sb.append("40,");// X QRCODE條碼左上角X座標
		//		sb.append("690,");// Y QRCODE條碼左上角Y座標
		sb.append(y + ",");// Y QRCODE條碼左上角Y座標
		sb.append("M,");// ECC level 錯誤糾正能力等級 L 7% M 15% Q 25% H 30%
		sb.append("9,");// cell width 1~10
		sb.append("A,");// mode 自動生成編碼/手動生成編碼 A Auto M Manual
		sb.append("0,");// rotation 順時針旋轉角度 0 不旋轉 90 順時針旋轉90度 180 順時針旋轉180度
						// 270 順時針旋轉270度
		sb.append("2,");// model 條碼生成樣式 1 (預設), 原始版本 2 擴大版本
		sb.append("7,");// mask 範圍：0~8，預設7
		sb.append("\"");
		sb.append(barCode);// Data string 二维码內容
		sb.append("\"");
		return sb.toString();
	}

	/**
	 * 调用GP-3150TN打印二维码 Author MaBingYang
	 * 
	 * @date Dec 30, 2013 11:58:29 AM
	 * @param barCode
	 *            二维码内容
	 * @param index 打印第几张数  解决位移问题
	 */
	public static void Print_Qrcode(String barCode, String spcode, String order, String line, String des,
			String supplier, String fourPos, String speStore, String machineName, int index) {
		TSCLIB_DLL_GP3150TN.INSTANCE.openport(machineName);// 打开 打印机
															// 端口.
		TSCLIB_DLL_GP3150TN.INSTANCE.setup("60", "90", "3", "10", "0", "3", "0");
		TSCLIB_DLL_GP3150TN.INSTANCE.clearbuffer();// 清除缓冲信息
		TSCLIB_DLL_GP3150TN.INSTANCE.sendcommand("GAP 2 mm,0");// 设置 打印的方向.
		TSCLIB_DLL_GP3150TN.INSTANCE.sendcommand("DIRECTION 1");// 设置 打印的方向.
		TSCLIB_DLL_GP3150TN.INSTANCE.sendcommand(CMD_QRCODE_FROMT_GP3150TN(barCode, index));

		String fieldName = "物料编码:";
		int fx = 40;
		renderTopFieldLabel(fieldName, fx, index);

		renderTopFieldValue(spcode, fx, index);

		if (fourPos == null) {
			fourPos = "";
		}

		int x = 80;

		renderTopFieldLabel("物料描述:", x, index);

		int finalx = renderLine(des, 10, x, TOP_FIELD_VALUE_START_Y, index);

		finalx = finalx + 40;

		renderTopFieldLabel("仓    位:", finalx, index);

		renderTopFieldValue(fourPos, finalx, index);

		if (speStore == null) {
			speStore = "";
		}
		finalx = finalx + 40;

		renderTopFieldLabel("特殊标识:", finalx, index);

		renderTopFieldValue(speStore, finalx, index);

		finalx = finalx + 40;
		finalx = finalx > 280 ? finalx : 280;

		renderBotFieldLabel("采购订单:", finalx, index);
		renderBotFieldValue(order + "/" + line, finalx, index);

		finalx = renderSupplier(supplier, finalx, index);

		finalx += 40;
		renderBotFieldLabel("数量:", finalx, index);

		renderBotFieldValue("_____  _____  _____  _____ _____", finalx, index);

		// 份数，张数
		TSCLIB_DLL_GP3150TN.INSTANCE.printlabel("1", "1");
		TSCLIB_DLL_GP3150TN.INSTANCE.clearbuffer();// 清除缓冲信息
		TSCLIB_DLL_GP3150TN.INSTANCE.closeport();
	}

	private static void renderTopFieldValue(String txt, int fx, int index) {
		int y = TOP_FIELD_VALUE_START_Y + (index * moveAdjust);
		int fontstyle = 0;
		renderTxt(txt, fx, y, fontstyle);
	}

	private static void renderTopFieldLabel(String txt, int fx, int index) {
		int y = TOP_FIELD_START_Y + (index * moveAdjust);
		int fontstyle = 2;

		renderTxt(txt, fx, y, fontstyle);
	}

	private static void renderBotFieldValue(String txt, int fx, int index) {
		int y = BOT_FIELD_VALUE_START_Y + (index * moveAdjust);
		int fontstyle = 0;

		renderTxt(txt, fx, y, fontstyle);
	}

	private static void renderBotFieldLabel(String txt, int fx, int index) {
		int y = BOT_FIELD_START_Y + (index * moveAdjust);
		int fontstyle = 2;

		renderTxt(txt, fx, y, fontstyle);
	}

	private static void renderTxt(String txt, int fx, int y, int fontstyle) {
		TSCLIB_DLL_GP3150TN.INSTANCE.windowsfont(fx, y, FONT_HEIGHT, 90, fontstyle, 0, "simsun", txt);
	}

	private static int renderSupplier(String des, int finalx, int index) {
		int x = finalx + 40, fontNum = 16;
		if (des == null) {
			des = "";
		}
		renderBotFieldLabel("供应商名:", x, index);
		if (des.length() > fontNum) {
			for (int i = 0; i <= (des.length() / fontNum); i++) {
				if (i == 0) {
					renderBotFieldValue(des.substring(0, fontNum), x, index);
				} else {
					x = x + 40;
					if ((i + 1) * fontNum <= des.length()) {
						renderBotFieldValue(des.substring(i * fontNum, (i + 1) * fontNum), x, index);

					} else {
						renderBotFieldValue(des.substring(i * fontNum), x, index);

					}

				}
			}

		} else {
			renderBotFieldValue(des, x, index);

		}

		return x;
	}

	/**
	 * 渲染物料描述
	 * @param des
	 * @param fontNum 行字数
	 * @param y 
	 * @param x 
	 */
	private static int renderMDesc(String des, int fontNum, int x, int y) {
		if (des.length() > fontNum) {
			for (int i = 0; i <= (des.length() / fontNum); i++) {
				if (i == 0) {
					TSCLIB_DLL_GP3150TN.INSTANCE.windowsfont(x, y, FONT_HEIGHT, 90, 0, 0, "simsun",
							des.substring(0, fontNum));
				} else {
					x = x + 40;
					int y2 = y;
					if ((i + 1) * fontNum <= des.length()) {
						TSCLIB_DLL_GP3150TN.INSTANCE.windowsfont(x, y2, FONT_HEIGHT, 90, 0, 0, "simsun",
								des.substring(i * fontNum, (i + 1) * fontNum));
					} else {
						TSCLIB_DLL_GP3150TN.INSTANCE.windowsfont(x, y2, FONT_HEIGHT, 90, 0, 0, "simsun",
								des.substring(i * fontNum));
					}

				}
			}

		} else {
			TSCLIB_DLL_GP3150TN.INSTANCE.windowsfont(x, y, FONT_HEIGHT, 90, 0, 0, "simsun", des);
		}

		return x;
	}

	public static List<String> getPrintList() {
		List<String> pl = new ArrayList<String>();
		HashPrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE; // 查找所有的可用的打印服务
		PrintService[] printService = PrintServiceLookup.lookupPrintServices(flavor, pras);
		for (int i = 0; i < printService.length; i++) {
			pl.add(printService[i].getName());
			System.out.println(printService[i].getName());
		}
		return pl;
	}

	public static void main(String[] args) {
		//		英文和标点符号算半个字符
		//		String des ="我试试走@￥sdfasdfsdsdfasfsdtes我试试走@￥sdfasdfsdsdfasfsdtest";
		String des = "@￥";
		int fontNum = 13;
		renderLine(des, fontNum, 1, 1, 1);

		getPrintList();
		execute("000107020080001655,,,150500001A", "2311040086875394", "23000039130", "01", "包装材料、标识-包装材料-胶纸",
				"中国石油化工股份有限公司物资装备部中国石油化工股份有限公司物资装备部", "", "", "Microsoft XPS Document Writer", 1);

	}

	private static int renderLine(String des, int fontNum, int x, int y, int index) {
		y += index * moveAdjust;
		char[] charArray = des.toCharArray();
		int fn = fontNum * 2;
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotBlank(des)) {
			x = x - 40;
			for (int i = 0; i < charArray.length; i++) {
				sb.append(charArray[i]);
				boolean matches = String.valueOf(charArray[i]).matches("[\\w\\pP]");
				if (matches) {
					fn--;
				} else {
					fn -= 2;
				}
				if (fn == 0) {
					x = x + 40;
					TSCLIB_DLL_GP3150TN.INSTANCE.windowsfont(x, y, FONT_HEIGHT, 90, 0, 0, "simsun", sb.toString());
					System.out.println(sb.toString());
					sb.delete(0, sb.length());
					fn = fontNum * 2;
				}
				if (fn < 0) {
					x = x + 40;
					TSCLIB_DLL_GP3150TN.INSTANCE.windowsfont(x, y, FONT_HEIGHT, 90, 0, 0, "simsun",
							sb.substring(0, sb.length() - 1));
					System.out.println(sb.substring(0, sb.length() - 1));
					sb.delete(0, sb.length() - 1);
					fn = (fontNum - 1) * 2;
				}

			}
			x = x + 40;
			TSCLIB_DLL_GP3150TN.INSTANCE.windowsfont(x, y, FONT_HEIGHT, 90, 0, 0, "simsun", sb.toString());
			System.out.println(sb.toString());
		}

		return x;
	}

	/**
	 * 
	 * @param mCode
	 * @param orderCode
	 * @param lineNo
	 * @param desc
	 * @param index 
	 */
	public static void execute(String barCode, String mCode, String orderCode, String lineNo, String desc,
			String supplier, String fourPos, String speStore, String machineName, int index) {
		System.setProperty("jna.encoding", "GBK");
		Print_Qrcode(barCode, mCode, orderCode, lineNo, desc, supplier, fourPos, speStore, machineName, index);
	}

	public static void execute(Order o) {
		System.setProperty("jna.encoding", "GBK");
		int index = 0;
		TSCLIB_DLL_GP3150TN.INSTANCE.openport(o.getMachineName());// 打开 打印机
		String barCode = o.getBatchCode();
		// 端口.
		TSCLIB_DLL_GP3150TN.INSTANCE.setup("60", "90", "3", "10", "0", "3", "0");
		TSCLIB_DLL_GP3150TN.INSTANCE.clearbuffer();// 清除缓冲信息
		TSCLIB_DLL_GP3150TN.INSTANCE.sendcommand("GAP 2 mm,0");// 设置 打印的方向.
		TSCLIB_DLL_GP3150TN.INSTANCE.sendcommand("DIRECTION 1");// 设置 打印的方向.
		TSCLIB_DLL_GP3150TN.INSTANCE.sendcommand(CMD_QRCODE_FROMT_GP3150TN(barCode, 0));

		String fieldName = "批次号:";
		int fx = 40;
		renderTopFieldLabel(fieldName, fx, 0);

		renderTopFieldValue(barCode, fx, 0);


		int x = 80;

		renderTopFieldLabel("采购订单:", x, index);

		int finalx = renderLine(o.getPurNo(), 10, x, TOP_FIELD_VALUE_START_Y, index);

		finalx = finalx + 40;

		renderTopFieldLabel("供应商:", finalx, index);

		renderTopFieldValue("供应商", finalx, index);

		finalx = finalx + 40;

		renderTopFieldLabel("合同号:", finalx, index);

		renderTopFieldValue("HT-2017", finalx, index);
		
		finalx = finalx + 40;

		renderTopFieldLabel("需求部门:", finalx, index);

		renderTopFieldValue("实验室", finalx, index);

		finalx = finalx + 40;
		finalx = finalx > 280 ? finalx : 280;

		
		
		
		renderBotFieldLabel("物料编号:", finalx, index);
		renderBotFieldValue("物料编号", finalx, index);


		finalx += 40;
		renderBotFieldLabel("物料描述:", finalx, index);

		renderBotFieldValue("物料描述", finalx, index);
		
		finalx += 40;
		renderBotFieldLabel("入库时间:", finalx, index);

		renderBotFieldValue("2017-07-15", finalx, index);

		// 份数，张数
		TSCLIB_DLL_GP3150TN.INSTANCE.printlabel("1", "1");
		TSCLIB_DLL_GP3150TN.INSTANCE.clearbuffer();// 清除缓冲信息
		TSCLIB_DLL_GP3150TN.INSTANCE.closeport();

	}
}
