package rw.ac.auca.drivingschool.util;

import rw.ac.auca.drivingschool.dao.StudentDao;
import rw.ac.auca.drivingschool.model.Student;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Turns the value chosen in a student drop-down back into a Student entity.
 *
 * An h:selectOneMenu can only ever submit a String. Without this converter the
 * lesson form would hand the text "STD001" to a setter that expects a Student
 * object, and JSF would report a conversion error. getAsString writes the
 * primary key into the page; getAsObject reads it back through the DAO.
 *
 * @author  Student Name
 * @version 1.0
 */
@FacesConverter("studentConverter")
public class StudentConverter implements Converter {

    private final StudentDao studentDao = new StudentDao();

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return studentDao.findById(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        return ((Student) value).getStudentId();
    }
}
