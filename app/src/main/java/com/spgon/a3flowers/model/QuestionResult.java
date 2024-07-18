package com.spgon.a3flowers.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionResult {
    @SerializedName("qas_data")
    private List<Question> qasData;

    public List<Question> getQasData() {
        return qasData;
    }

    public void setQasData(List<Question> qasData) {
        this.qasData = qasData;
    }
}
