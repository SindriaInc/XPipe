package org.sindria.nanoREST.requests.validators;

public class Validator extends BaseValidator implements ValidatorInterface {

    /**
     * Validator sigleton
     */
    private static Validator validator;

    /**
     * Validator construct
     */
    public Validator() {
        super();
    }

    /**
     * Validator instance
     */
    public static Validator getInstance() {
        if (validator == null) {
            validator = new Validator();
        }
        return validator;
    }


    public void validate() {

    }

    public void validated() {

    }

    public void fails() {

    }

    public void failed() {

    }

    public void errors() {

    }


}
