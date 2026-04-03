package com.thiennguyen.demo.dto;

public class PayOSWebhookRequest {
    private String code;
    private String desc;
    private PayOSData data;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public PayOSData getData() { return data; }
    public void setData(PayOSData data) { this.data = data; }

    public static class PayOSData {
        private Long orderCode;
        private String description;
        private Double amount;

        public Long getOrderCode() { return orderCode; }
        public void setOrderCode(Long orderCode) { this.orderCode = orderCode; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
    }
}