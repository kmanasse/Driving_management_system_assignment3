package rw.ac.auca.drivingschool.util;

import rw.ac.auca.drivingschool.dao.VehicleDao;
import rw.ac.auca.drivingschool.model.Vehicle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Turns the value chosen in a vehicle drop-down back into a Vehicle entity.
 * See StudentConverter for the explanation of why this is needed.
 *
 * @author  Student Name
 * @version 1.0
 */
@FacesConverter("vehicleConverter")
public class VehicleConverter implements Converter {

    private final VehicleDao vehicleDao = new VehicleDao();

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return vehicleDao.findById(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        return ((Vehicle) value).getPlateNumber();
    }
}
