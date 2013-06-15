package com.sbstudio.kan.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 文章
 * @author water3
 * @date 2013-6-9
 */
public class Article implements Serializable{
    /**  **/
	private static final long serialVersionUID = 1L;
	private String aid;
    /**
     * 封面图
     */
    private String coverimg;
    /**
     * 内容图
     */
    private List<String> content;
    private String title;
    private String kan_id;
    public String getAid() {
        return aid;
    }
    public void setAid(String aid) {
        this.aid = aid;
    }
    public String getCoverimg() {
        return coverimg;
    }
    public void setCoverimg(String coverimg) {
        this.coverimg = coverimg;
    }
    public List<String> getContent() {
        return content;
    }
    public void setContent(List<String> content) {
        this.content = content;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getKan_id() {
        return kan_id;
    }
    public void setKan_id(String kan_id) {
        this.kan_id = kan_id;
    }
    
    
}
