package org.blogsite.youngsoft.piggybank.setting;

import org.blogsite.youngsoft.piggybank.crypt.RSAUtils;
import org.blogsite.youngsoft.piggybank.crypt.SecureHash;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 환경 설정을 위한 클래스로 클래스 전체를 직렬화하여 데이터베이스로 저장한다.
 */
public class PBSettings implements Serializable {

    private static final long serialVersionUID = SmsUtils.superSerialVersionUID;

    private BigInteger modulus;
    private BigInteger publicExponent;
    private BigInteger privateExponent;
    private String remittance_pwd;
    private String photoUrl;
    private String userName;
    private String userEmail;
    private int threshold;
    private int percent;
    private boolean thresholdOverall;
    private int donationDay;
    private boolean debug = false;

    private transient DonationList donationList;
    private Donations donations;

    public PBSettings(){
        photoUrl = "";
        userName = "";
        userEmail = "";
        threshold = 100000; //- Integer.MAX_VALUE;
        percent = 50; //- Integer.MAX_VALUE;
        thresholdOverall = false;
        donationDay = -1;
        debug = false;
        remittance_pwd = "";
    }

    /**
     * 사용자 이름
     * @return
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 사용자 이름
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 사용자 이메일
     * @return
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * 사용자 이메일
     * @param userEmail
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * 기부를 위한 임계값. 이 값을 넘기는 경우에만 기부로 설정되며
     * thresholdOverall = true인 경우에는 사용금액 전부를 대상으로 하며
     * thresholdOverall의 기본값은 false로 threshold를 넘는 금액을 대상으로 한다.
     * @return
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     * 기부를 위한 임계값. 이 값을 넘기는 경우에만 기부로 설정되며
     * thresholdOverall = true인 경우에는 사용금액 전부를 대상으로 하며
     * thresholdOverall의 기본값은 false로 threshold를 넘는 금액을 대상으로 한다.
     * @param threshold
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    /**
     * 사용금액을 기부할 때의 백분률
     * @return
     */
    public int getPercent() {
        return percent;
    }

    /**
     * * 사용금액을 기부할 때의 백분률
     * @param percent
     */
    public void setPercent(int percent) {
        this.percent = percent;
    }

    /**
     * threshold 전체를 백분률로 설정된 금액 만큼 기부할지를 설정하며
     * 기본값은 false로 threshold를 넘는 금액에 대해 백분율로 기부 금액을 산출한다.
     * @return
     */
    public boolean isThresholdOverall() {
        return thresholdOverall;
    }

    /**
     * threshold 전체를 백분률로 설정된 금액 만큼 기부할지를 설정하며
     * 기본값을 false로 threshold를 넘는 금액에 대해 백분율로 기부 금액을 산출한다.
     * @param thresholdOverall
     */
    public void setThresholdOverall(boolean thresholdOverall) {
        this.thresholdOverall = thresholdOverall;
    }

    /**
     * 기부일자를 설정한다. 기부일자가 음수인 경우 매월 말일로 설정된다.
     * @return
     */
    public int getDonationDay() {
        return donationDay;
    }

    /**
     * 기부일자를 설정한다. 기부일자가 음수인 경우 매월 말일로 설정된다.
     * @param donationDay
     */
    public void setDonationDay(int donationDay) {
        this.donationDay = donationDay;
    }

    public BigInteger getModulus() {
        return modulus;
    }

    public void setModulus(BigInteger modulus) {
        this.modulus = modulus;
    }

    public BigInteger getPublicExponent() {
        return publicExponent;
    }

    public void setPublicExponent(BigInteger publicExponent) {
        this.publicExponent = publicExponent;
    }

    public BigInteger getPrivateExponent() {
        return privateExponent;
    }

    public void setPrivateExponent(BigInteger privateExponent) {
        this.privateExponent = privateExponent;
    }

    public PublicKey getPublicKey(){
        return RSAUtils.getPublicKey(modulus, publicExponent);
    }

    public PrivateKey getPrivateKey(){
        return RSAUtils.getPrivateKey(modulus, privateExponent);
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public DonationList getDonationList() {
        return donationList;
    }

    public void setDonationList(DonationList donationList) {
        this.donationList = donationList;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Donations getDonations() {
        return donations;
    }

    public void setDonations(Donations donations) {
        this.donations = donations;
    }

    public String getRemittance_pwd() {
        return remittance_pwd;
    }

    public void setRemittance_pwd(String remittance_pwd) {
        this.remittance_pwd = remittance_pwd;
    }

}
