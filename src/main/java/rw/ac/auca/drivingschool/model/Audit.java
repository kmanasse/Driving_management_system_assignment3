package rw.ac.auca.drivingschool.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base class holding audit timestamps for every entity in the system.
 *
 * Marked @MappedSuperclass, so Hibernate does NOT create a table for it.
 * Instead its two columns are copied into the table of every subclass.
 *
 * @PrePersist and @PreUpdate are lifecycle callbacks: Hibernate invokes them
 * automatically just before an INSERT or an UPDATE, so no DAO or bean ever
 * has to remember to set these fields by hand.
 *
 * @author  Student Name
 * @version 1.0
 */
@MappedSuperclass
public abstract class Audit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
