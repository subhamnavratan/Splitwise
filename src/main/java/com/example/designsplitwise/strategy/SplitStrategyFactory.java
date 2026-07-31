package com.example.designsplitwise.strategy;

import com.example.designsplitwise.model.SplitType;

public class SplitStrategyFactory {
    public static SplitStrategy getSplitStrategy(SplitType splitType) {
        return switch (splitType) {
            case EQUAL -> new EqualSplitStrategy();
            case UNEQUAL -> new UnequalSplitStrategy();
            case PERCENTAGE -> new PercentageSplitStrategy();
            default -> throw new IllegalArgumentException("Unknown split type: " + splitType);
        };
    }
}
