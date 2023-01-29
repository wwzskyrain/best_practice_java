package yueyi.easy.rule.study.air;

import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;

public class HighTemperatureCondition implements Condition {

    @Override
    public boolean evaluate(Facts facts) {
        Integer temperature = facts.get("temperature");
        return temperature > 25;
    }

    static HighTemperatureCondition itIsHot() {
        return new HighTemperatureCondition();
    }

}