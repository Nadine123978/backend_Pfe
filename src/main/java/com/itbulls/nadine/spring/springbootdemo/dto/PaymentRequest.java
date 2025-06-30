package com.itbulls.nadine.spring.springbootdemo.dto;

import org.springframework.web.multipart.MultipartFile;

public class PaymentRequest {
    private String paymentMethod;
    private String fullName;
    private String phoneNumber;
    private String receiptNumber;
    private String orderNumber;
    private Double amount;
    private MultipartFile receiptImage;

    // Getters and Setters
    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getReceiptNumber() {
        return receiptNumber;
    }
    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
    public String getOrderNumber() {
        return orderNumber;
    }
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    public MultipartFile getReceiptImage() {
        return receiptImage;
    }
    public void setReceiptImage(MultipartFile receiptImage) {
        this.receiptImage = receiptImage;
    }
}
