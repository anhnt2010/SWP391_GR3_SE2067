package model;

/** Danh mục Cơ sở / Chi nhánh. */
public class Store {
    private int storeId;
    private String storeCode;
    private String storeName;
    private String address;
    private String phone;
    private boolean status;

    public Store() {}

    public Store(int storeId, String storeCode, String storeName, String address, String phone, boolean status) {
        this.storeId = storeId;
        this.storeCode = storeCode;
        this.storeName = storeName;
        this.address = address;
        this.phone = phone;
        this.status = status;
    }

    public int getStoreId() { return storeId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }

    public String getStoreCode() { return storeCode; }
    public void setStoreCode(String storeCode) { this.storeCode = storeCode; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
}
