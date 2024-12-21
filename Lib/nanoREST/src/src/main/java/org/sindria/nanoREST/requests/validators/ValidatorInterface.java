package org.sindria.nanoREST.requests.validators;

public interface ValidatorInterface {

    /**
     * Run the validator's rules against its data.
     */
    public void validate();

    /**
     * Get the attributes and values that were validated.
     */
    public void validated();

    /**
     * Determine if the data fails the validation rules.
     */
    public void fails();

    /**
     * Get the failed validation rules.
     */
    public void failed();

    /**
     * Get all of the validation error messages.
     */
    public void errors();

}
