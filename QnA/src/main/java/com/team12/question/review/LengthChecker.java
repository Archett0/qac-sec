package com.team12.question.review;

public class LengthChecker implements ContentReviewHandler{
    private static final int MAX_LENGTH = 2000;
    private static final int MIN_LENGTH = 2;

    @Override
    public boolean handle(String content) {
        return content.length() <= MAX_LENGTH && content.length() >= MIN_LENGTH;
    }
}
