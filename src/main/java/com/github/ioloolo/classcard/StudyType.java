package com.github.ioloolo.classcard;

public enum StudyType {
    MEMORIZATION("암기"),
    RECALL("리콜"),
    MATCH("매칭"),
    TEST("테스트");

    private final String name;
    private int process = 0;

    StudyType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int getProcess() {
        return this.process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public static StudyType from(String name) {
        return switch (name) {
            case "리콜" -> StudyType.RECALL;
            case "테스트" -> StudyType.TEST;
            case "매칭" -> StudyType.MATCH;
            case "암기" -> StudyType.MEMORIZATION;
            default -> null;
        };
    }
}
