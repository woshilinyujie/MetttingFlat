package com.example.meettingflat.Utils;

public class Instruct {
      public static String DOOR="AT+CDOOR=";//接收开门结果
      public static String SENDDOOR="+CPWOPEN:";//发送 密码 开门指令
      public static String OPENDOOR="+CNOPEN";//无密码开门指令
      public static String OPENDOOR1="+CZOPEN";//服务器通知开门指令


      public static final String NORMALLYOPEN="+CAWAYOPEN:1";//常开指令
      public static final String NORMALLYOPEN1="+CCAWAY:1";//务器通知常开指令

      public static final String CANCELNORMALLYOPEN="+CAWAYOPEN:0";//取消常开
      public static final String CANCELNORMALLYOPEN1="+CCAWAY:0";//务器通知取消常开

      public static final String DATA="+DATATOPAD";//硬件数据


      public static final int SHOWTOAST=1;//
      public static final int PUSHLINK=2;//
      public static final int REFRESHTIME=4;//
      public static final int DIALOG=3;//
      public static String SENDBULECARD="+CPWBLEADD:";//发送添加蓝牙指令
      public static String DELETEBULECARD="+CPWBLEDEL:";//发送添加蓝牙指令
      public static int SENDDELAY=2;//延迟发送
}
