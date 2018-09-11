package ua.sam.ignite.shared.data;

import java.util.Date;

public class CustomInfo {

    private Date updateTime;
    private boolean deleted;

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

}
