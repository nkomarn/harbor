package xyz.nkomarn.harbor.api;

import java.util.function.BiFunction;

public enum LogicType implements BiFunction<Boolean, Boolean, Boolean> {
    AND, OR, XOR;

    @Override
    public Boolean apply(Boolean a, Boolean b) {
        if(this.equals(AND))
            return a && b;
        else if(this.equals(OR))
            return a || b;
        else if(this.equals(XOR))
            return a ^ b;
        else
            throw new UnsupportedOperationException("Unsupported logic type");
    }
}
