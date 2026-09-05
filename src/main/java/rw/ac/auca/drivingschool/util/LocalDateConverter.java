package rw.ac.auca.drivingschool.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Converts between the text a user types and java.time.LocalDate.
 *
 * JSF ships converters for java.util.Date but not for the java.time classes,
 * so this one has to be written by hand. Note the difference from a validator:
 * a converter changes the TYPE of a value, a validator checks whether a value
 * is acceptable. Conversion always runs first.
 *
 * @author  Student Name
 * @version 1.0
 */
@FacesConverter("localDateConverter")
public class LocalDateConverter implements Converter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim(), FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new ConverterException("Enter a date as yyyy-MM-dd, for example 2005-04-17.");
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        return FORMATTER.format((LocalDate) value);
    }
}
