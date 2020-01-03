package com.lushihao.myutils.file.vo;

import java.util.Date;

/**
 * 文件属性
 */
public class FileAttr {
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 修改时间
     */
    private Date ModifyTime;
    /**
     * 大小
     */
    private Long size;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getModifyTime() {
        return new Date(ModifyTime.getTime());
    }

    public void setModifyTime(Date modifyTime) {
        ModifyTime = modifyTime;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
