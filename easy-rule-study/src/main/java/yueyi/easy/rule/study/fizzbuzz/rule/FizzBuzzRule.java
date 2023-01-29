package yueyi.easy.rule.study.fizzbuzz.rule;

import org.jeasy.rules.support.composite.UnitRuleGroup;

public class FizzBuzzRule extends UnitRuleGroup {

    public FizzBuzzRule(Object... rules) {
        for (Object rule : rules) {
            super.addRule(rule);
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}