package org.luubstar.lsdatabase.Utils.Validation;

public class ValidationMessage implements Comparable<ValidationMessage>{
    private final String message;
    private final ValidationType type;
    public ValidationMessage(String m, ValidationType t){
        message = m;
        type = t;
    }

    public String getMessage() {
        return message;
    }

    public ValidationType getType() {
        return type;
    }

    @Override
    public int compareTo(ValidationMessage o) {
        return type.compareTo(o.type);
    }
}
