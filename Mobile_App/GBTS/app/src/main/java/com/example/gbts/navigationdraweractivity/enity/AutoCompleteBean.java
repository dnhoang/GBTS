package com.example.gbts.navigationdraweractivity.enity;

/**
 * Created by 석원 on 3/27/2015.
 */
public class AutoCompleteBean {
    String description;
    String reference;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public AutoCompleteBean(String description, String reference){
        this.description = description;
        this.reference = reference;
    }
}
