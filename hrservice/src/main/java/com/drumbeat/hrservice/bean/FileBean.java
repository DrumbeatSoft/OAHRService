package com.drumbeat.hrservice.bean;

/**
 * @author ZhangYuhang
 * @describe 上传文件返回的实体类
 * @date 2020/11/3
 * @updatelog
 */
public class FileBean {


    /**
     * createTime :
     * createUserId :
     * filePath :
     * fileSize : 0
     * fileUrl :
     * filename :
     * id :
     */

    private String createTime;
    private String createUserId;
    private String filePath;
    private int fileSize;
    private String fileUrl;
    private String filename;
    private String id;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
