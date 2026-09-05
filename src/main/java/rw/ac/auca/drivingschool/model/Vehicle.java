package rw.ac.auca.drivingschool.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A training vehicle. The number plate is the natural primary key.
 *
 * @author  Student Name
 * @version 1.0
 */
@Entity
@Table(name = "vehicle")
public class Vehicle extends Audit {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "plate_number", length = 10)
    private String plateNumber;

    @Column(name = "make", nullable = false, length = 40)
    private String make;

    @Column(name = "model", nullable = false, length = 40)
    private String model;

    /** MANUAL or AUTOMATIC. */
    @Column(name = "transmission", nullable = false, length = 10)
    private String transmission;

    /** Licence category this vehicle is used to train for. */
    @Column(name = "category", nullable = false, length = 1)
    private String category;

    @Column(name = "available", nullable = false)
    private boolean available;

    public Vehicle() {
        this.transmission = "MANUAL";
        this.category = "B";
        this.available = true;
    }

    /** Label shown in the lesson booking drop-down. */
    public String getLabel() {
        return plateNumber + " - " + make + " " + model;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
