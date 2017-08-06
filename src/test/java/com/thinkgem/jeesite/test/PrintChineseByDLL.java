package com.thinkgem.jeesite.test;

import org.xvolks.jnative.JNative;
import org.xvolks.jnative.Type;
import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;
import org.xvolks.jnative.pointers.memory.HeapMemoryBlock;

//[DllImport("fnthex32.dll")]
//public static extern int GETFONTHEX(
// string ChineseText,
// string FontName,
// string DataName,
// int Orient,
// int Height,  // font height
// int Width,   // font width
// byte IsBold,
// byte IsItalic,
// System.Text.StringBuilder ReturnPicData);

public class PrintChineseByDLL {

       
       public static String printChinese(String sChn,String fontName,String rotate,String fontHeight,String fontWidth,String bold,String italic) {
            JNative n = null;
             try {
          	   	String path = PrintChineseByDLL.class.getResource("/").getPath().substring(1) + "Fnthex32.dll";
          	   	System.out.println(path);
                  n = new JNative(path , "GETFONTHEX");
                  n.setRetVal(Type. INT);
                  n.setParameter(0, Type. STRING, sChn);//中文
                  n.setParameter(1, Type. STRING, fontName);//字体名称
                  n.setParameter(2, Type. INT, rotate);//旋转角度0,90,180,270
                  n.setParameter(3, Type. INT, fontHeight);//字体高度
                  n.setParameter(4, Type. INT, fontWidth);//字体宽度，通常是0
                  n.setParameter(5, Type. INT, bold);//1变粗，0正常
                  n.setParameter(6, Type. INT, italic);//1斜体，0正常
                  Pointer p2 = new Pointer(new HeapMemoryBlock(22 * 1024));
                  p2.setMemory( new byte [21 * 1024]);
                  n.setParameter(7, p2); //返回的图片字符
                  
                  n.invoke();
                  System. out.println(p2.getAsString());//返回的图片字符
                   return p2.getAsString();
            }
             catch (NativeException e)
            {
            	 	e.printStackTrace();
                   return null ;
            }
             catch (IllegalAccessException e)
            {
            	 e.printStackTrace();
                   return null ;
            }
             finally
            {
                   try
                  {
                        n. dispose(); // 释放内存
                  } catch (NativeException e)
                  {
                        e.printStackTrace();
                  } catch (IllegalAccessException e)
                  {
                        e.printStackTrace();
                  }
            }

      }
      
       private static void TestPrintChinese(String sChn) {
             try {
            	 	String path = PrintChineseByDLL.class.getResource("/").getPath().substring(1) + "Fnthex32.dll";
              	   	System.out.println(path);
              		System.out.println(System.getProperty("java.library.path")+","+System.getenv("java.library.path"));
                  JNative n = new JNative("Fnthex32" , "GETFONTHEX" );
                  n. setRetVal(Type.INT);
                  n. setParameter(0, Type.STRING, sChn); //中文
                  n. setParameter(1, Type.STRING, "宋体");//字体名称
                  n. setParameter(2, Type.INT, "0");//旋转角度0,90,180,270
                  n. setParameter(3, Type.INT, "14");//字体高度
                  n. setParameter(4, Type.INT, "0");//字体宽度，通常是0
                  n. setParameter(5, Type.INT, "1");//1变粗，0正常
                  n. setParameter(6, Type.INT, "0");//1斜体，0正常
                  Pointer p2 = new Pointer(new HeapMemoryBlock(22* 1024));
                  p2.setMemory( new byte [21 * 1024]);
                  n. setParameter(7, p2);//返回的图片字符
                  
                  n. invoke();
                  System. out.println(p2.getAsString());//返回的图片字符
     
//返回的字符串如下所示：             
//~DGOUTSTR01,00224,016,3180398063H018J06H063H018H0C301981E3H0C3H018J06H0H3H018H0C
//3H01803HF19FE018I0IFC0303IF7HFEHF80DEH363H018J0C01HFH03I0C3H0780763063H018I01HFH
//0FH07FC0C3H0D81E7F0IF018I038301BH0E0CJ019803631C0C03CI07830H301E0C3HFC3D807EH3C0
//C03CI0DHF07B037FC030C7F81DHF0IF03CI01830HFH060C030CDF803B60C0C0H6I01831BFH060C03
//0C19807B60D8C0H60701HF0H3H07FC030C198HDB60IC0C30701830H31860C0H38198C1E78C0C1818
//301830H31860C03H018FC7C38C3C7H0HE018F031F863C03H0  
//然后使用^XG调用已存储图像的源设备
//注意：不同的图像用不同的名字。如果多个图像用了同一名送到打印机，其他图像将被最后一个图像所代替。

                 //      ^XG 调用图象    
        //   格式 ^XGd:o.x,mx,my
        //    d ---已存储图像的源设备
        //    mx = x 轴的放大系数接受的值：1 至 10     默认值：1
        //    my = y 轴的放大系数接受的值：1 至 10  默认值：1
         
         // ~DG 命令用于下载以 ASCII 十六进制表示的图形图像
         //  格式 ~DGd:o.x,t,w,data
         //   d = 存储图像的设备
         //   t = 图形中的总字节数
         //   w = 每行的字节
         //   data = 定义图像的 ASCII 十六进制字符串
            }
             catch (NativeException e)
            {
            	 e.printStackTrace();
            }
             catch (IllegalAccessException e)
            {
            	 e.printStackTrace();
            }

      }
      
       public static void main(String[] args) {
             //byte[] bytes = "放慢节奏 幸福就在停歇的脚边".getBytes();
            
             TestPrintChinese("礼貌待人,有礼有节");

      }
}