package yueyi.easy.rule.study.air;

import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Facts;

/**
 * As you can see, the InferenceRulesEngine will continuously select and fire candidate rules
 * until no more rules are applicable. If we used a DefaultRulesEngine instead,
 * only the first run would have been executed and the temperature would have remained at 29.
 */
public class DecreaseTemperatureAction implements Action {

    @Override
    public void execute(Facts facts) throws Exception {
        System.out.println("It is hot! cooling air..");
        Integer temperature = facts.get("temperature");
        facts.put("temperature", temperature - 1);
    }

    static DecreaseTemperatureAction decreaseTemperature() {
        return new DecreaseTemperatureAction();
    }
}