package com.hjnerp.model;

/**
 * Created by zy on 2016/12/1.
 */

public class Ctlm7502Json {
    private String id_proj;
    private String name_proj;
    private String id_projcate;
    private String var_conno;
    private String id_corr;
    private String name_corr;

    public String getId_proj() {
        return id_proj;
    }

    public void setId_proj(String id_proj) {
        this.id_proj = id_proj;
    }

    public String getName_proj() {
        return name_proj;
    }

    public void setName_proj(String name_proj) {
        this.name_proj = name_proj;
    }

    public String getId_projcate() {
        return id_projcate;
    }

    public void setId_projcate(String id_projcate) {
        this.id_projcate = id_projcate;
    }

    public String getVar_conno() {
        return var_conno;
    }

    public void setVar_conno(String var_conno) {
        this.var_conno = var_conno;
    }

    public String getId_corr() {
        return id_corr;
    }

    public void setId_corr(String id_corr) {
        this.id_corr = id_corr;
    }

    public String getName_corr() {
        return name_corr;
    }

    public void setName_corr(String name_corr) {
        this.name_corr = name_corr;
    }

    @Override
    public String toString() {
        return "Ctlm7502Json{" +
                "id_proj='" + id_proj + '\'' +
                ", name_proj='" + name_proj + '\'' +
                ", id_projcate='" + id_projcate + '\'' +
                ", var_conno='" + var_conno + '\'' +
                ", id_corr='" + id_corr + '\'' +
                ", name_corr='" + name_corr + '\'' +
                '}';
    }
}
