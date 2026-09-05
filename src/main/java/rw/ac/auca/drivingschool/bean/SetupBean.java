package rw.ac.auca.drivingschool.bean;

import rw.ac.auca.drivingschool.dao.InstructorDao;
import rw.ac.auca.drivingschool.dao.VehicleDao;
import rw.ac.auca.drivingschool.model.Instructor;
import rw.ac.auca.drivingschool.model.Vehicle;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Backing bean for the setup screen, where instructors and vehicles are
 * registered. A lesson cannot be scheduled until at least one of each exists,
 * so this page is the starting point for a demonstration.
 *
 * @author  Student Name
 * @version 1.0
 */
@Named
@ViewScoped
public class SetupBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final InstructorDao instructorDao = new InstructorDao();
    private final VehicleDao vehicleDao = new VehicleDao();

    private List<Instructor> instructors = new ArrayList<>();
    private List<Vehicle> vehicles = new ArrayList<>();

    private Instructor instructor = new Instructor();
    private Vehicle vehicle = new Vehicle();

    @PostConstruct
    public void init() {
        reload();
    }

    private void reload() {
        this.instructors = instructorDao.findAll();
        this.vehicles = vehicleDao.findAll();
    }

    public void saveInstructor() {
        try {
            instructorDao.save(instructor);
            addMessage(FacesMessage.SEVERITY_INFO, "Instructor saved",
                    instructor.getFullName() + " is now available for scheduling.");
            this.instructor = new Instructor();
            reload();
        } catch (RuntimeException ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Could not save instructor", ex.getMessage());
        }
    }

    public void saveVehicle() {
        try {
            vehicleDao.save(vehicle);
            addMessage(FacesMessage.SEVERITY_INFO, "Vehicle saved",
                    vehicle.getLabel() + " is now available for scheduling.");
            this.vehicle = new Vehicle();
            reload();
        } catch (RuntimeException ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Could not save vehicle", ex.getMessage());
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public String[] getLicenseCategories() {
        return new String[]{"A", "B", "C"};
    }

    public String[] getTransmissions() {
        return new String[]{"MANUAL", "AUTOMATIC"};
    }

    public List<Instructor> getInstructors() {
        return instructors;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
