package ua.sam.ignite.shared.data;

import java.util.Date;

public class GroupInfo  {

    public static final String FIELD_UPDATE_TIME = "updateTime";
    public static final String FIELD_DELETED = "deleted";

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
