package rw.ac.auca.drivingschool.util;

import rw.ac.auca.drivingschool.dao.InstructorDao;
import rw.ac.auca.drivingschool.model.Instructor;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Turns the value chosen in an instructor drop-down back into an Instructor
 * entity. See StudentConverter for the explanation of why this is needed.
 *
 * @author  Student Name
 * @version 1.0
 */
@FacesConverter("instructorConverter")
public class InstructorConverter implements Converter {

    private final InstructorDao instructorDao = new InstructorDao();

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return instructorDao.findById(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        return ((Instructor) value).getInstructorId();
    }
}
