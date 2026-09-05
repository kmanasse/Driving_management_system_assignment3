package rw.ac.auca.drivingschool.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Converts between the text a user types and java.time.LocalTime, used for
 * lesson start and end times.
 *
 * @author  Student Name
 * @version 1.0
 */
@FacesConverter("localTimeConverter")
public class LocalTimeConverter implements Converter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(value.trim(), FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new ConverterException("Enter a time as HH:mm on the 24 hour clock, for example 14:30.");
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        return FORMATTER.format((LocalTime) value);
    }
}
