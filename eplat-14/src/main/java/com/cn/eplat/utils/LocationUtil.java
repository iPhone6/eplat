package com.cn.eplat.utils;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.cn.eplat.controller.EpDataController;
import com.cn.eplat.utils.gps.BDToGPS;

/**
 * Created by yuliang on 2015/3/20.
 * 
 * REF: http://blog.csdn.net/woaixinxin123/article/details/45935439
 */
public class LocationUtil {
	private static double EARTH_RADIUS = 6378.137;
//	private static final double EP_COMPANY_LATITUDE = 22.641793860000000;	// 公司所在地地理位置之纬度值
//	private static final double EP_COMPANY_LONGTITUDE = 114.055380340000000;	// 公司所在地地理位置之经度值
	// 22.6391143100,114.0606038169（iOS和安卓平台共用的火星坐标中心点）
	private static final double EP_COMPANY_LATITUDE_COMMON = 22.6391143100;	// 公司所在地地理位置之纬度值（iOS和安卓平台共用）
	private static final double EP_COMPANY_LONGTITUDE_COMMON = 114.0606038169;	// 公司所在地地理位置之经度值（iOS和安卓平台共用）
	
	private static final double[] EP_COMPANY_LATITUDE_COMMONS = {22.6391143100, 22.6447740174, 22.6431570174, 22.6440990335, 22.6465420335, 22.6483600335,
		22.6501920957, 22.6532690957, 22.6565960957, 22.6567420712, 22.6545780712, 22.6533400712, 22.6497040712, 22.6479890174, 22.6535190712, 22.6501760712, 22.6482390174,
		34.2081126228, 30.7315428557, 30.7659890716, 30.7625560716, 30.7341818557, 30.7349158557};	// 多地区中心点地理位置纬度值数组
	private static final double[] EP_COMPANY_LONGTITUDE_COMMONS = {114.0606038169, 114.0605468967, 114.0607718967, 114.0575979613, 114.0567129613, 114.0566729613,
		114.0563431741, 114.0568331741, 114.0562221741, 114.0595081007, 114.0596291007, 114.0595751007, 114.0601861007, 114.0602508967, 114.0630111007, 114.0628401007, 114.0629728967,
		108.8419103853, 103.9710245586, 103.8950237569, 103.8975517569, 103.9699685586, 103.9705695586};	// 多地区中心点地理位置纬度值数组
	
	private static final double EP_COMPANY_LATITUDE = 22.640853000000000;	// 公司所在地地理位置之纬度值（iOS平台）
	private static final double EP_COMPANY_LONGTITUDE = 114.058098000000000;	// 公司所在地地理位置之经度值（iOS平台）
	// 22.644631, 114.0672
	// 22.644644, 114.067206
	private static final double EP_COMPANY_LATITUDE_ADR = 22.644644;	// 公司所在地地理位置之纬度值（Android平台）
	private static final double EP_COMPANY_LONGTITUDE_ADR = 114.067206;	// 公司所在地地理位置之经度值（Android平台）
	
	private static final double EP_RANGE_RADIUS = 80.0;	// 地理位置有效范围半径50米
	private static String EP_WIFI_MAC = "80:89:17:33:9f:18";	// EleadWifi无线路由器的有效WiFi MAC地址
	private static String EP_WIFI_ADDR = "深圳市龙岗区坂田街道坂兴路8号附近";	// WiFi MAC地址对应的地址信息
	private static String EP_GPS_ADDR = "深圳市龙岗区坂田街道坂兴路8号附近";	// GPS 经纬度对应的地址信息
	private static String[] EP_GPS_ADDRS = {"深圳市龙岗区坂田街道坂兴路8号附近", "深圳市龙岗区坂田黄君山区111附近", "深圳市龙岗区坂田街道坂兴路8号", 
		"深圳市龙岗坂田街道黄君山111号", "深圳市龙岗坂田街道黄君山维也纳酒店", "深圳市龙岗坂田街道黄君山山海商业广场"};	// GPS 经纬度对应的候选地理位置信息
	private static String[][] EP_GPS_ADDRSS = {	// 与每个地区的中心点对应的一组有效的备选地理位置信息
		{"深圳市龙岗区坂田街道坂兴路8号附近", "深圳市龙岗区坂田黄君山区111附近", "深圳市龙岗区坂田街道坂兴路8号", "深圳市龙岗坂田街道黄君山111号", "深圳市龙岗坂田街道黄君山维也纳酒店", "深圳市龙岗坂田街道黄君山山海商业广场"},
		{"深圳华为坂田园区H区-H3座"},
		{"深圳华为坂田园区H区-H2座"},
		{"深圳华为坂田园区D区-D1座"},
		{"深圳华为坂田园区C区"},
		{"深圳华为坂田园区B区"},
		{"深圳华为坂田园区B区-B1座"},
		{"深圳华为坂田园区A区-A8座"},
		{"深圳华为坂田园区A区"},
		{"深圳华为坂田园区E区-E1座"},
		{"深圳华为坂田园区F区-F2座"},
		{"深圳华为坂田园区F区"},
		{"特发物业公司"},
		{"深圳华为坂田园区G区-G1座"},
		{"深圳华为坂田园区J区"},
		{"深圳华为坂田园区K区"},
		{"深圳华为坂田园区K区"},
		{"陕西西安高新区天谷八路环普产业园C301"},
		{"西芯大道3号国腾科技园1号楼3层"},
		{"高新西区西源大道1899号华为成都研究所U1"},
		{"高新西区西源大道1899号华为成都研究所U8"},
		{"西芯大道6号博力科技园"},
		{"西芯大道6号博力科技园"},
	};
	private static final String MAC_PATTERN_COLON = "^([0-9a-fA-F]{2})(([/\\s:][0-9a-fA-F]{2}){5})$";	// 判断是否是MAC地址的正则表达式（中间以冒号(:)连接的MAC地址）
	private static final String MAC_PATTERN_DASH = "^([0-9a-fA-F]{2})(([/\\s-][0-9a-fA-F]{2}){5})$";	// 判断是否是MAC地址的正则表达式（中间以短横线(-)连接的MAC地址）
	
	private static Random EP_RAND = new Random();
	
	private static Logger logger = Logger.getLogger(LocationUtil.class);
	
	public static double getEARTH_RADIUS() {
		return EARTH_RADIUS;
	}

	public static void setEARTH_RADIUS(double eARTH_RADIUS) {
		EARTH_RADIUS = eARTH_RADIUS;
	}

	public static String getEP_WIFI_MAC() {
		return EP_WIFI_MAC;
	}

	public static void setEP_WIFI_MAC(String eP_WIFI_MAC) {
		EP_WIFI_MAC = eP_WIFI_MAC;
	}

	public static String getEP_WIFI_ADDR() {
		return EP_WIFI_ADDR;
	}

	public static String getEP_GPS_ADDR() {
		return EP_GPS_ADDR;
	}

	public static void setEP_GPS_ADDR(String eP_GPS_ADDR) {
		EP_GPS_ADDR = eP_GPS_ADDR;
	}

	public static String[] getEP_GPS_ADDRS() {
		return EP_GPS_ADDRS;
	}

	public static void setEP_GPS_ADDRS(String[] eP_GPS_ADDRS) {
		EP_GPS_ADDRS = eP_GPS_ADDRS;
	}

	public static void setEP_WIFI_ADDR(String eP_WIFI_ADDR) {
		EP_WIFI_ADDR = eP_WIFI_ADDR;
	}

	public static double getEpCompanyLatitude() {
		return EP_COMPANY_LATITUDE;
	}

	public static double getEpCompanyLongtitude() {
		return EP_COMPANY_LONGTITUDE;
	}

	public static double getEpRangeRadius() {
		return EP_RANGE_RADIUS;
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 通过经纬度获取距离(单位：米)
	 * 
	 * @param lat1	第一个点的纬度值
	 * @param lng1	第一个点的经度值
	 * @param lat2	第二个点的纬度值
	 * @param lng2	第二个点的经度值
	 * @return 两个点之间的距离（地球上两点之间的球面距离）
	 */
	public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000d) / 10000d;
		s = s * 1000;
		return s;
	}
	
	/**
	 * 判断给定经纬度的地点是否在有效范围内（iOS和安卓平台共用）
	 * 
	 * @param lat  纬度值
	 * @param lng  经度值
	 * @return
	 */
	public static boolean isGPSLocationValidCommon(double lat, double lng, String platform) {
		double[] real_coords_ep = BDToGPS.gcj2WGSExactly(EP_COMPANY_LATITUDE_COMMON, EP_COMPANY_LONGTITUDE_COMMON);
		double[] real_coords = BDToGPS.gcj2WGSExactly(lat, lng);
		
//		double distance = getDistance(EP_COMPANY_LATITUDE_COMMON, EP_COMPANY_LONGTITUDE_COMMON, real_coords[0], real_coords[1]);
		double distance = BDToGPS.distance(real_coords_ep[0], real_coords_ep[1], real_coords[0], real_coords[1]);	// 采用更精确的距离算法
		logger.info("[共用 - " + platform +  " ] 当前位置距离中心标准点的距离：" + distance + "（米）");
		return distance <= EP_RANGE_RADIUS;
	}
	
	/**
	 * 判断给定经纬度的地点是否在有效范围内（iOS和安卓平台共用，多地区多中心点）
	 * 
	 * @param lat  纬度值
	 * @param lng  经度值
	 * @return
	 */
	public static boolean isGPSLocationValidCommons(double lat, double lng, String platform, StringBuffer valid_addr) {
		int centers_count = EP_COMPANY_LATITUDE_COMMONS.length;	// 获取到中心点的个数
		
		for(int i=0; i<centers_count; i++) {	// 循环遍历所有地区的中心点坐标计算得到距离最近的一个中心点，如果距离所有中心点都没有在有效范围内时，才判定当前GPS打卡失败
			double[] real_coords_ep = BDToGPS.gcj2WGSExactly(EP_COMPANY_LATITUDE_COMMONS[i], EP_COMPANY_LONGTITUDE_COMMONS[i]);
			double[] real_coords = BDToGPS.gcj2WGSExactly(lat, lng);
			
			double distance = BDToGPS.distance(real_coords_ep[0], real_coords_ep[1], real_coords[0], real_coords[1]);	// 采用更精确的距离算法
			logger.info("[共用，第（" + (i+1) + "） - " + platform +  " ] 距离中心标准点的距离：" + distance + "（米）");
			
			if(distance <= EP_RANGE_RADIUS) {
				String[] nearby_addrs = EP_GPS_ADDRSS[i];	// 取出与当前中心点对应的有效地理位置数组
				int len = nearby_addrs.length;	// 有效的GPS地理位置信息字符串的个数
				
				int ni = EP_RAND.nextInt(len);	// 生成一个大于等于0，小于len的整数
				
				logger.info("，第（" + (i+1) + "）产生的随机数是：" + ni);
				valid_addr.append(nearby_addrs[ni]);
				
				return true;
			}
		}
		
		// 如果所有中心点都遍历计算完后，都不在有效范围内，则返回false
		return false;
		
		/*
		double[] real_coords_ep = BDToGPS.gcj2WGSExactly(EP_COMPANY_LATITUDE_COMMON, EP_COMPANY_LONGTITUDE_COMMON);
		double[] real_coords = BDToGPS.gcj2WGSExactly(lat, lng);
		
//		double distance = getDistance(EP_COMPANY_LATITUDE_COMMON, EP_COMPANY_LONGTITUDE_COMMON, real_coords[0], real_coords[1]);
		double distance = BDToGPS.distance(real_coords_ep[0], real_coords_ep[1], real_coords[0], real_coords[1]);	// 采用更精确的距离算法
		logger.info("[共用 - " + platform +  " ] 当前位置距离中心标准点的距离：" + distance + "（米）");
		return distance <= EP_RANGE_RADIUS;
		*/
	}
	
	/**
	 * 判断给定经纬度的地点是否在有效范围内（iOS和安卓平台共用，多地区多中心点），并返回计算得到的离所有中心点最近的距离
	 * 
	 * @param lat  纬度值
	 * @param lng  经度值
	 * @return
	 */
	public static JSONObject isGPSLocationValidCommonsWithDistance(double lat, double lng, String platform, StringBuffer valid_addr) {
		JSONObject json = new JSONObject();
		double nearest_distance = -1.0;	// 初值设为-1，表示一个无效的距离，只有在计算得到真正的距离值之后，才为其赋上正数值
		int nearest_i = 0;	// 用来记录当距离某个中心点距离最近时的for循环数组下标i的值
		
		int centers_count = EP_COMPANY_LATITUDE_COMMONS.length;	// 获取到中心点的个数
		
		for(int i=0; i<centers_count; i++) {	// 循环遍历所有地区的中心点坐标计算得到距离最近的一个中心点，如果距离所有中心点都没有在有效范围内时，才判定当前GPS打卡失败
			double[] real_coords_ep = BDToGPS.gcj2WGSExactly(EP_COMPANY_LATITUDE_COMMONS[i], EP_COMPANY_LONGTITUDE_COMMONS[i]);
			double[] real_coords = BDToGPS.gcj2WGSExactly(lat, lng);
			
			double distance = BDToGPS.distance(real_coords_ep[0], real_coords_ep[1], real_coords[0], real_coords[1]);	// 采用更精确的距离算法
			if(nearest_distance < 0) {
				nearest_distance = distance;
			} else {
				if(nearest_distance > distance) {
					nearest_i = i;
					nearest_distance = distance;
				} else {
					
				}
			}
			
			logger.info("[共用，第（" + (i+1) + "） - " + platform +  " ] 距离中心标准点的距离：" + distance + "（米）");
			/*
			if(distance <= EP_RANGE_RADIUS) {
				String[] nearby_addrs = EP_GPS_ADDRSS[i];	// 取出与当前中心点对应的有效地理位置数组
				int len = nearby_addrs.length;	// 有效的GPS地理位置信息字符串的个数
				
				int ni = EP_RAND.nextInt(len);	// 生成一个大于等于0，小于len的整数
				
				logger.info("，第（" + (i+1) + "）产生的随机数是：" + ni);
				valid_addr.append(nearby_addrs[ni]);
				
				return true;
			}
			*/
		}
		
		json.put("nearest_distance", nearest_distance);
		
		if(nearest_distance <= EP_RANGE_RADIUS) {
			String[] nearby_addrs = EP_GPS_ADDRSS[nearest_i];	// 取出与当前中心点对应的有效地理位置数组
			int len = nearby_addrs.length;	// 有效的GPS地理位置信息字符串的个数
			
			int ni = EP_RAND.nextInt(len);	// 生成一个大于等于0，小于len的整数
			
			logger.info("，第（" + (nearest_i+1) + "）产生的随机数是：" + ni);
			valid_addr.append(nearby_addrs[ni]);
			
			json.put("is_gps_location_valid", true);
			
//			return true;
			return json;
		}
		
		// 如果所有中心点都遍历计算完后，都不在有效范围内，则返回false
//		return false;
		json.put("is_gps_location_valid", false);
		return json;
		
	}
	
	/**
	 * 判断给定经纬度的地点是否在有效范围内
	 * 
	 * @param lat  纬度值
	 * @param lng  经度值
	 * @return
	 */
	public static boolean isGPSLocationValid(double lat, double lng) {
		double distance = getDistance(EP_COMPANY_LATITUDE, EP_COMPANY_LONGTITUDE, lat, lng);
		logger.info("[iOS] 当前位置距离目标位置：" + distance + "（米）");
		return distance <= EP_RANGE_RADIUS;
	}
	
	/**
	 * 判断给定经纬度的地点是否在有效范围内（安卓平台）
	 * 
	 * @param lat  纬度值
	 * @param lng  经度值
	 * @return
	 */
	public static boolean isGPSLocationValidAndroid(double lat, double lng) {
		double distance = getDistance(EP_COMPANY_LATITUDE_ADR, EP_COMPANY_LONGTITUDE_ADR, lat, lng);
		logger.info("[Android] 当前位置距离标准中心点目标位置：" + distance + "（米）");
		return distance <= EP_RANGE_RADIUS;
	}
	
	/**
	 * 判断给定的WiFi MAC地址是否有效
	 * 
	 * @param wifi_mac
	 * @return
	 */
	public static boolean isWiFiMACValid(String wifi_mac) {
		/*
		if(StringUtils.isBlank(wifi_mac)) {	// 如果传入的WiFi MAC地址参数为空白字符串，则直接返回false
			return false;
		}
		if(wifi_mac.length() == 12) {	// 如果WiFi MAC地址字符串的长度为12个字符，则说明该MAC地址中间没有分隔符连接
			String ep_wifi_no_sep = EP_WIFI_MAC.replace(":", "");
			return ep_wifi_no_sep.equalsIgnoreCase(wifi_mac);
		}
		if(wifi_mac.length() == 17) {	// 如果WiFi MAC地址字符串的长度为17个字符，则说明该MAC地址中间有分隔符连接
			if(wifi_mac.contains(":") && !wifi_mac.contains("-")) {	// 如果WiFi MAC地址中间是以冒号（:）分隔的，则无须处理，可直接进行比较
				return EP_WIFI_MAC.equalsIgnoreCase(wifi_mac);
			}
			if(wifi_mac.contains("-") && !wifi_mac.contains(":")) {	// 如果WiFi MAC地址中间是以短横线（-）分隔的，则将短横线替换成冒号（:）
				String wifi_mac_colon = wifi_mac.replace('-', ':');
				return EP_WIFI_MAC.equalsIgnoreCase(wifi_mac_colon);
			}
		}
		*/
		
		
		if(StringUtils.isBlank(wifi_mac)) {	// 如果传入的WiFi MAC地址参数为空白字符串，则直接返回false
			return false;
		}
		
		String[] wifi_mac_splits = macSplitter(wifi_mac);
		if(wifi_mac_splits == null) {	// 如果分割得到的MAC地址片段数组为空，则直接返回false
			return false;
		}
		String[] ep_mac_splits = macSplitter(EP_WIFI_MAC);	// 分割公司WiFi MAC地址，得到MAC地址片段数组
		if(ep_mac_splits == null) {	// 如果分割得到的MAC地址片段数组为空，则直接返回false
			return false;
		}
		
		int len1 = wifi_mac_splits.length;
		int len2 = ep_mac_splits.length;
		
		if(len1 != len2) {	// 如果分割得到的两个MAC地址片段数组的长度不同，则直接返回false
			return false;
		}
		
		if(len1 != 6) {	// 如果分割得到的MAC地址片段的个数不是6，则直接返回false
			return false;
		}
		
		// 开始循环遍历每个MAC地址片段，与标准MAC地址（这里一般指的是公司WiFi的MAC地址）对比，看是否一致，如果有一个片段不一致就说明传入的MAC地址是无效的
		for(int i=0; i<6; i++) {
			if(ep_mac_splits[i] == null || wifi_mac_splits[i] == null) {
				return false;
			}
			if(!ep_mac_splits[i].equalsIgnoreCase(wifi_mac_splits[i])) {
				return false;
			}
		}
		
		
		/*
		if(EP_WIFI_MAC.contains(":")) {
			int colon_count = 0;	// 用来存放冒号分隔符的个数
			int[] colon_indexs = new int[5];	// 用来存放冒号分隔符在MAC地址字符串中出现的位置索引值的数组
			int[] split_points = new int[7];	// 用来存放分割MAC地址片段的分割点的索引下标位置（包含首尾两个字符所在的位置下标）
			String[] mac_splits = new String[6];	// 用来存放以冒号分隔符分割后的MAC地址片段（按MAC地址的标准写法来看，MAC地址片段的个数只能是6）
			
			int mac_str_len = EP_WIFI_MAC.length();
			for(int i=0; i<mac_str_len; i++) {
				char ch = EP_WIFI_MAC.charAt(i);
				if(ch == ':') {
					colon_count++;	// 对分隔符冒号计数
					if(colon_count > 5) {	// 如果冒号分隔符的个数大于5，说明这个MAC地址不符合标准，则直接返回false
						return false;
					}
					colon_indexs[colon_count-1] = i;	// 在冒号分隔符所在位置下标的数组中放进去对应第几个冒号在MAC地址字符串中出现的位置索引值
				}
			}
			if(colon_count < 5) {	// 如果冒号分隔符的个数小于5个，则说明所传入的MAC地址不符合要求（因为一定要5个才能分隔成6段MAC地址片段），即不是有效的MAC地址
				return false;
			}
			
//			split_points[0] = 0;	// 首个字符的位置索引（不用赋值为0，因为整型int的默认初值就是0）
			for(int i=1; i<6; i++) {
				split_points[i] = colon_indexs[i-1];
			}
			split_points[6] = mac_str_len -1;	// 末位字符的位置索引
			
			// 开始手动按冒号截取MAC地址片段（每个片段最多2个字符，如果片段中包含0则可能会被省略掉）
			int start_index;	// 用于存放MAC地址片段的开始索引值
			int end_index;	// 用于存放MAC地址片段的结束索引值
			for(int i=0; i<6; i++) {	// 总共要截取6段MAC地址片段
//				if(i==0) {	// 如果是截取第一个MAC地址片段
//					start_index = 0;
//					end_index = colon_indexs[0];
//				}
				start_index = split_points[i];
				end_index = split_points[i+1];
				mac_splits[i] = EP_WIFI_MAC.substring(start_index, end_index+1);	// 截取第(i+1)个MAC地址片段，放进前面声明好的MAC地址片段字符串数组mac_splits中
				if(mac_splits[i] != null) {
					if(mac_splits[i].length() == 0) {	// 如果MAC地址片段中有长度小于2个字符的（即可分为0个或1个字符两种情况），则认为其是自动省略掉了靠左边的0（或者说是十六进制数中高位的0），那么就手动填补上缺省的0
						mac_splits[i] = "00" + mac_splits[i];
					} else if(mac_splits[i].length() == 1) {
						mac_splits[i] = "0" + mac_splits[i];
					} else if(mac_splits[i].length() > 2) {	// 如果MAC地址片段中有长度超过2个字符的，则认为是不符合MAC地址标准的，即不是有效的MAC地址
						return false;
					}
				} else {
					mac_splits[i] = "00";
				}
				System.out.println("第 " + (i+1) + " 个 mac split = " + mac_splits[i]);
			}
			
//			String[] ep_wifi_split = EP_WIFI_MAC.split(":");
//			if(ep_wifi_split.length == 6) {	// 如果可以拆分成6组十六进制数
//				
//			}
			
			
		}
		
		if(EP_WIFI_MAC.contains("-")) {
			String[] ep_wifi_split = EP_WIFI_MAC.split("-");
			
		}
		*/
		
		return true;	// 如果所有MAC地址片段都通过验证，都是一致的，才返回true
	}
	
	/**
	 * MAC地址分割器
	 * @param mac_str MAC地址字符串
	 * @return 分割后的MAC地址片段数组（共6个片段），如果分割失败，则返回null
	 */
	private static String[] macSplitter(String mac_str_in) {
		if(StringUtils.isBlank(mac_str_in)) {	// 如果是空白字符串则直接返回null
			return null;
		}
		
		String mac_str = mac_str_in.trim();	// 去除MAC地址字符串首尾的空白字符
		
//		String[] mac_split_strs = new String[6];	// 用来存放分割成功后的MAC地址片段
		char seperator = ':';	// 用来表示MAC地址中的分隔字符
		
		if(mac_str.contains("-")) {	// 如果分隔符是短横线，则将分隔符替换成“-”
			seperator = '-';
		}
		
		int colon_count = 0;	// 用来存放分隔符的个数
		int[] colon_indexs = new int[5];	// 用来存放分隔符在MAC地址字符串中出现的位置索引值的数组
		int[] split_points = new int[7];	// 用来存放分割MAC地址片段的分割点的索引下标位置（包含首尾两个字符所在的位置下标）
		String[] mac_splits = new String[6];	// 用来存放以分隔符分割后的MAC地址片段（按MAC地址的标准写法来看，MAC地址片段的个数只能是6）
		
		int mac_str_len = mac_str.length();
		for(int i=0; i<mac_str_len; i++) {
			char ch = mac_str.charAt(i);
			if(ch == seperator) {
				colon_count++;	// 对分隔符冒号计数
				if(colon_count > 5) {	// 如果分隔符的个数大于5，说明这个MAC地址不符合标准，则直接返回false
					return null;
				}
				colon_indexs[colon_count-1] = i;	// 在分隔符所在位置下标的数组中放进去对应第几个冒号在MAC地址字符串中出现的位置索引值
			}
		}
		if(colon_count < 5) {	// 如果分隔符的个数小于5个，则说明所传入的MAC地址不符合要求（因为一定要5个才能分隔成6段MAC地址片段），即不是有效的MAC地址
			return null;
		}
		
//			split_points[0] = 0;	// 首个字符的位置索引（不用赋值为0，因为整型int的默认初值就是0）
		for(int i=1; i<6; i++) {
			split_points[i] = colon_indexs[i-1];
		}
		split_points[6] = mac_str_len;	// 末位字符的位置索引
		
		// 开始手动按冒号截取MAC地址片段（每个片段最多2个字符，如果片段中包含0则可能会被省略掉）
		int start_index;	// 用于存放MAC地址片段的开始索引值
		int end_index;	// 用于存放MAC地址片段的结束索引值
		for(int i=0; i<6; i++) {	// 总共要截取6段MAC地址片段
//				if(i==0) {	// 如果是截取第一个MAC地址片段
//					start_index = 0;
//					end_index = colon_indexs[0];
//				}
			if(i == 0) {
				start_index = split_points[i];
			} else {
				start_index = split_points[i] + 1;
			}
			
			end_index = split_points[i+1];
			mac_splits[i] = mac_str.substring(start_index, end_index);	// 截取第(i+1)个MAC地址片段，放进前面声明好的MAC地址片段字符串数组mac_splits中
			if(mac_splits[i] != null) {
				if(mac_splits[i].length() == 0) {	// 如果MAC地址片段中有长度小于2个字符的（即可分为0个或1个字符两种情况），则认为其是自动省略掉了靠左边的0（或者说是十六进制数中高位的0），那么就手动填补上缺省的0
					mac_splits[i] = "00" + mac_splits[i];
				} else if(mac_splits[i].length() == 1) {
					mac_splits[i] = "0" + mac_splits[i];
				} else if(mac_splits[i].length() > 2) {	// 如果MAC地址片段中有长度超过2个字符的，则认为是不符合MAC地址标准的，即不是有效的MAC地址
					return null;
				}
			} else {
				mac_splits[i] = "00";
			}
			System.out.println("第 " + (i+1) + " 个 mac split = " + mac_splits[i]);
		}
		
//			String[] ep_wifi_split = mac_str.split(":");
//			if(ep_wifi_split.length == 6) {	// 如果可以拆分成6组十六进制数
//				
//			}
		
		return mac_splits;
	}
	
	
	/**
	 * 随机获取有效的GPS经纬度对应的地理位置信息
	 * 
	 * @return 地理位置信息字符串
	 */
	public static String getRandomGPSAddr() {
		
		int len = EP_GPS_ADDRS.length;	// 有效的GPS地理位置信息字符串的个数
		
//		double rand = Math.random();
		int ni = EP_RAND.nextInt(len);	// 生成一个大于等于0，小于len的整数
		
		logger.info("产生的随机数是：" + ni);
		
		return EP_GPS_ADDRS[ni];
	}
	
	
	public static void main(String[] args) {
		
		
		double lat = 22.638588900000000;
		double lng = 114.060011700000000;

		lat = 22.641553300000000;
		lng = 114.059814700000000;
		
		lat = 22.635995600000000;
		lng = 114.059956300000000;
		
		lat = 22.638588900000000;
		lng = 114.060011700000000;
		
		lat = 22.639078800000000;
		lng = 114.060695300000000;
		
		lat = 22.639028300000000;
		lng = 114.060589500000000;
		
		lat = 20.0001;
		lng = 100.0001;
		
		lat = 22;
		lng = 102;
		
		lat = 22.639214000000000;
		lng = 114.060536000000000;
		
//		lat = 22.639376400000000;
//		lng = 114.060082700000000;
		
		lat = 22.6545881702;
		lng = 114.0596291117;
		
		lat = 30.7625470516;
		lng = 103.89754589869;
		
		lat = 22.643106000000000;
		lng = 114.059519000000000;
		
//		lat = 22.643145000000000;
//		lng = 114.059510000000000;
//		
//		lat = 22.644158000000000;
//		lng = 114.060144000000000;
		
		// 30.7348168557,103.9704305586
		lat = 30.7348178537;
		lng = 103.9704315580;
		
		lat = 30.733852000000000;
		lng = 103.970854000000000;
		
		
		StringBuffer valid_addr = new StringBuffer();
		
		boolean is_valid = isGPSLocationValidCommons(lat, lng, "iOS", valid_addr);
		
		System.out.println("is_valid = " + is_valid);
		System.out.println("valid_addr = " + valid_addr);
		
		
		
		
//		double res = LocationUtil.getDistance(22.6417938600000, 114.0553803400000, 23.0, 115.0);
//		res = LocationUtil.getDistance(22.6417938600000, 114.0553803400000, 31.219624, 121.496559);
//		res = LocationUtil.getDistance(22.6417938600000, 114.0553803400000, 22.6406760000, 114.0711900000);
//		
//		System.out.println("res = " + res);
		
		/*
		double lat1 = 22.6474510000;
		double lng1 = 114.0660700000;
		double lat2 = 22.6472350000;
		double lng2 = 114.0689260000;
		
		double res2 = LocationUtil.getDistance(lat1, lng1, lat2, lng2);
		
		System.out.println("res2 = " + res2);
		*/
		
		
		/*
//		EP_WIFI_MAC = "";
		String my_wifi_mac = "0c1DAFC91Cd5";
		System.out.println("wifi地址是否有效：" + isWiFiMACValid(my_wifi_mac));
		
		
		String mac_pattern = "^([0-9a-fA-F]{2})(([/\\s:][0-9a-fA-F]{2}){5})$";
		mac_pattern = "^([0-9a-fA-F]{2})(([/\\s-][0-9a-fA-F]{2}){5})$";
//		mac_pattern = "^([0-9a-fA-F]{2})(([/\\s][0-9a-fA-F]{2}){5})$";
		String mac1 = "0C1dAFC91Cc5";
		System.out.println("mac地址正则表达式测试结果：" + mac1.matches(mac_pattern));
		*/
		
		/*
		double latitude_pos = 22.63927186070816;
		double longtitude_pos = 114.0604499512401;
		latitude_pos = 22.6417938600000;
		longtitude_pos = 114.0553803400000;
		System.out.println("我的打卡位置是否在有效范围内：" + isGPSLocationValid(latitude_pos, longtitude_pos));
		*/
		
		/*
		String str = " :";
		String[] str_split = str.split(":");
		for(String stri:str_split) {
			System.out.println(stri);
		}
		System.out.println("str_split size = " + str_split.length);
		*/
		
		/*
		int[] colon_indexs = new int[5];
		for(int ci:colon_indexs) {
			System.out.println("ci = " + ci);
		}
		*/
		
		/*
		String mac_str = "-0-3-53--D";
		String[] mac_splits = macSplitter(mac_str);
		if(mac_splits != null) {
			System.out.println(mac_splits.length);
		} else {
			System.out.println("分割结果为空");
		}
		*/
		
		/*
		String mac_str = "-0-3-53--D";
		mac_str = "80:89:17:33:9F:18";
		boolean is_valid = isWiFiMACValid(mac_str);
		if(is_valid) {
			System.out.println("该MAC地址是有效的");
		} else {
			System.out.println("该MAC地址无效");
		}
		*/
		
		/*
		double latA = 22.64356599421788;
		double logA = 114.05298370153714;
		double latB = 22.641822877380903;
		double logB = 114.05548632541633;
		
		double res_dist = getDistance(latA, logA, latB, logB);
		System.out.println("the distance = " + res_dist);
		*/
		
		/*
		// 22.644631, 114.0672
		double lat = 22.644631;
		double lng = 114.0672;
		
		lat = 22.638221;
		lng = 114.062929;
		
		lat = 22.637925000000000;
		lng = 114.063731000000000;
		
		lat = 22.638221000000000;
		lng = 114.062929000000000;
		
		lat = 22.642350000000000;
		lng = 114.055537000000000;
		
		lat = 22.63913167;
		lng = 114.06052277;
		
		lat = 22.63888;
		lng = 114.06011;
		
		String platform = "iOS";
		boolean ret = isGPSLocationValidCommon(lat, lng, platform);
		System.out.println("位置是否为有效：" + ret);
		*/
		
		/*
//		Random rand = new Random();
		for(int i=0; i<20; i++) {
//			int ni = rand.nextInt(100);
//			System.out.println("第 " + (i+1) + " 个随机数：" + ni);
			
			System.out.println("第 " + (i+1) + " 个随机地址：" + getRandomGPSAddr());
			
		}
		*/
		
		/*
		double lat = 22.638588900000000;
		double lng = 114.060011700000000;

		lat = 22.641553300000000;
		lng = 114.059814700000000;
		
		lat = 22.635995600000000;
		lng = 114.059956300000000;
		
		lat = 22.638588900000000;
		lng = 114.060011700000000;
		
		lat = 22.639078800000000;
		lng = 114.060695300000000;
		
		lat = 22.639028300000000;
		lng = 114.060589500000000;
		
		isGPSLocationValidCommon(lat, lng, "android");
		*/
	}
	
}
