package com.khaolitti.khao;

public class CategoryModel {

    private String categoryiconlink;
    private String categoryname;

    public String getCategoryiconlink() {
        return categoryiconlink;
    }

    public void setCategoryiconlink(String categoryiconlink) {
        this.categoryiconlink = categoryiconlink;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public CategoryModel(String categoryiconlink, String categoryname) {
        this.categoryiconlink = categoryiconlink;
        this.categoryname = categoryname;
    }
}
