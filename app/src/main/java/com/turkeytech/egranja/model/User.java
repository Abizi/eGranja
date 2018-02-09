package com.turkeytech.egranja.model;

public class User {

    private String name;
    private String number;
    private String question;
    private String answer;

    public User() {
    }

    public User(String number) {
        this.number = number;
    }

    public User(String name, String number, String question, String answer) {
        this.name = name;
        this.number = number;
        this.question = question;
        this.answer = answer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
