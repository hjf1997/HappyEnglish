package com.example.happyenglish;

import java.io.Serializable;
import java.util.List;

public class Records implements Serializable {

    /**
     * name : Name2
     * difficulty : Easy
     * times : ["11","22","33"]
     * answers : ["aaa","sss","sss"]
     * myAnswers : ["s","a","w"]
     * progress : 3/4
     * accuracy : 2/4
     * onProgress : false
     */

    private String name;
    private String difficulty;
    private String progress;
    private String accuracy;
    private Boolean onProgress;
    private List<String> times;
    private List<String> answers;
    private List<String> myAnswers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public Boolean isOnProgress() {
        return onProgress;
    }

    public void setOnProgress(Boolean onProgress) {
        this.onProgress = onProgress;
    }

    public List<String> getTimes() {
        return times;
    }

    public void setTimes(List<String> times) {
        this.times = times;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public List<String> getMyAnswers() {
        return myAnswers;
    }

    public void setMyAnswers(List<String> myAnswers) {
        this.myAnswers = myAnswers;
    }
}
