package rw.ac.auca.drivingschool.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.time.LocalDate;
import java.time.Period;

/**
 * VALIDATION TYPE 2 of 3 - a custom validator.
 *
 * Rwandan law sets the minimum age for a provisional driving licence at 18, so
 * the school cannot enrol anyone younger. No built-in JSF tag can express this:
 * f:validateLongRange works on numbers, and the rule here is a calculation
 * across two dates that also has to reject dates in the future.
 *
 * Implementing javax.faces.validator.Validator and annotating the class with
 * @FacesValidator registers it under an id, which any page can then attach to
 * any input with <f:validator validatorId="legalAgeValidator"/>. That
 * reusability is the point of a custom validator: the rule lives in one class
 * instead of being copied into every form that needs it.
 *
 * Throwing ValidatorException tells JSF the value failed. JSF then skips the
 * Update Model and Invoke Application phases, so an invalid date can never
 * reach the entity or the database.
 *
 * @author  Student Name
 * @version 1.0
 */
@FacesValidator("legalAgeValidator")
public class LegalAgeValidator implements Validator {

    private static final int MINIMUM_AGE = 18;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return;
        }

        LocalDate dateOfBirth = (LocalDate) value;
        LocalDate today = LocalDate.now();

        if (dateOfBirth.isAfter(today)) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Invalid date of birth",
                    "A date of birth cannot be in the future."));
        }

        int age = Period.between(dateOfBirth, today).getYears();

        if (age < MINIMUM_AGE) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Student is under age",
                    "A student must be at least " + MINIMUM_AGE
                            + " years old to enrol. This date of birth gives an age of "
                            + age + "."));
        }
    }
}
