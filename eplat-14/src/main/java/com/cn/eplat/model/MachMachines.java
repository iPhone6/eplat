package com.cn.eplat.model;

/**
 * 打卡机信息实体类
 * 
 * @author Administrator
 *
 */
public class MachMachines {
	private Long id;	// 打卡机信息表的主键id
	private Integer MachinesId;
	private String MachineAlias;
	private Integer ConnectType;
	private String IP;
	private Integer SerialPort;
	private Integer Port;
	private Integer Baudrate;
	private Integer MachineNumber;
	private Boolean IsHost;
	private Boolean Enabled;
	private String CommPassword;
	private Integer UILanguage;
	private Integer DateFormat;
	private Integer InOutRecordWarn;
	private Integer Idle;
	private Integer Voice;
	private Integer managercount;
	private Integer usercount;
	private Integer fingercount;
	private Integer SecretCount;
	private String FirmwareVersion;
	private String ProductType;
	private Integer LockControl;
	private Integer Purpose;
	private Integer ProduceKind;
	private String sn;
	private String PhotoStamp;
	private Integer IsIfChangeConfigServer2;
	private Integer pushver;
	private String IsAndroid;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getMachinesId() {
		return MachinesId;
	}
	public void setMachinesId(Integer machinesId) {
		MachinesId = machinesId;
	}
	public String getMachineAlias() {
		return MachineAlias;
	}
	public void setMachineAlias(String machineAlias) {
		MachineAlias = machineAlias;
	}
	public Integer getConnectType() {
		return ConnectType;
	}
	public void setConnectType(Integer connectType) {
		ConnectType = connectType;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	public Integer getSerialPort() {
		return SerialPort;
	}
	public void setSerialPort(Integer serialPort) {
		SerialPort = serialPort;
	}
	public Integer getPort() {
		return Port;
	}
	public void setPort(Integer port) {
		Port = port;
	}
	public Integer getBaudrate() {
		return Baudrate;
	}
	public void setBaudrate(Integer baudrate) {
		Baudrate = baudrate;
	}
	public Integer getMachineNumber() {
		return MachineNumber;
	}
	public void setMachineNumber(Integer machineNumber) {
		MachineNumber = machineNumber;
	}
	public Boolean getIsHost() {
		return IsHost;
	}
	public void setIsHost(Boolean isHost) {
		IsHost = isHost;
	}
	public Boolean getEnabled() {
		return Enabled;
	}
	public void setEnabled(Boolean enabled) {
		Enabled = enabled;
	}
	public String getCommPassword() {
		return CommPassword;
	}
	public void setCommPassword(String commPassword) {
		CommPassword = commPassword;
	}
	public Integer getUILanguage() {
		return UILanguage;
	}
	public void setUILanguage(Integer uILanguage) {
		UILanguage = uILanguage;
	}
	public Integer getDateFormat() {
		return DateFormat;
	}
	public void setDateFormat(Integer dateFormat) {
		DateFormat = dateFormat;
	}
	public Integer getInOutRecordWarn() {
		return InOutRecordWarn;
	}
	public void setInOutRecordWarn(Integer inOutRecordWarn) {
		InOutRecordWarn = inOutRecordWarn;
	}
	public Integer getIdle() {
		return Idle;
	}
	public void setIdle(Integer idle) {
		Idle = idle;
	}
	public Integer getVoice() {
		return Voice;
	}
	public void setVoice(Integer voice) {
		Voice = voice;
	}
	public Integer getManagercount() {
		return managercount;
	}
	public void setManagercount(Integer managercount) {
		this.managercount = managercount;
	}
	public Integer getUsercount() {
		return usercount;
	}
	public void setUsercount(Integer usercount) {
		this.usercount = usercount;
	}
	public Integer getFingercount() {
		return fingercount;
	}
	public void setFingercount(Integer fingercount) {
		this.fingercount = fingercount;
	}
	public Integer getSecretCount() {
		return SecretCount;
	}
	public void setSecretCount(Integer secretCount) {
		SecretCount = secretCount;
	}
	public String getFirmwareVersion() {
		return FirmwareVersion;
	}
	public void setFirmwareVersion(String firmwareVersion) {
		FirmwareVersion = firmwareVersion;
	}
	public String getProductType() {
		return ProductType;
	}
	public void setProductType(String productType) {
		ProductType = productType;
	}
	public Integer getLockControl() {
		return LockControl;
	}
	public void setLockControl(Integer lockControl) {
		LockControl = lockControl;
	}
	public Integer getPurpose() {
		return Purpose;
	}
	public void setPurpose(Integer purpose) {
		Purpose = purpose;
	}
	public Integer getProduceKind() {
		return ProduceKind;
	}
	public void setProduceKind(Integer produceKind) {
		ProduceKind = produceKind;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getPhotoStamp() {
		return PhotoStamp;
	}
	public void setPhotoStamp(String photoStamp) {
		PhotoStamp = photoStamp;
	}
	public Integer getIsIfChangeConfigServer2() {
		return IsIfChangeConfigServer2;
	}
	public void setIsIfChangeConfigServer2(Integer isIfChangeConfigServer2) {
		IsIfChangeConfigServer2 = isIfChangeConfigServer2;
	}
	public Integer getPushver() {
		return pushver;
	}
	public void setPushver(Integer pushver) {
		this.pushver = pushver;
	}
	public String getIsAndroid() {
		return IsAndroid;
	}
	public void setIsAndroid(String isAndroid) {
		IsAndroid = isAndroid;
	}
	
	@Override
	public String toString() {
		return "MachMachines [id=" + id + ", MachinesId=" + MachinesId + ", MachineAlias=" + MachineAlias + ", ConnectType=" + ConnectType + ", IP=" + IP + ", SerialPort="
				+ SerialPort + ", Port=" + Port + ", Baudrate=" + Baudrate + ", MachineNumber=" + MachineNumber + ", IsHost=" + IsHost + ", Enabled=" + Enabled + ", CommPassword="
				+ CommPassword + ", UILanguage=" + UILanguage + ", DateFormat=" + DateFormat + ", InOutRecordWarn=" + InOutRecordWarn + ", Idle=" + Idle + ", Voice=" + Voice
				+ ", managercount=" + managercount + ", usercount=" + usercount + ", fingercount=" + fingercount + ", SecretCount=" + SecretCount + ", FirmwareVersion="
				+ FirmwareVersion + ", ProductType=" + ProductType + ", LockControl=" + LockControl + ", Purpose=" + Purpose + ", ProduceKind=" + ProduceKind + ", sn=" + sn
				+ ", PhotoStamp=" + PhotoStamp + ", IsIfChangeConfigServer2=" + IsIfChangeConfigServer2 + ", pushver=" + pushver + ", IsAndroid=" + IsAndroid + "]";
	}
	
}
