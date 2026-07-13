package epam.course.appliance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "appliance_units", schema = "appliance_assistant")
public class Appliance {

    @Id
    @Column(name = "serial_number")
    private String serialNumber;

    @Column(nullable = false)
    private String category;

    @Column(name = "model_number", nullable = false)
    private String modelNumber;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "warranty_expiry_date")
    private LocalDate warrantyExpiryDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_username", nullable = false)
    private User owner;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(LocalDate manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public LocalDate getWarrantyExpiryDate() {
        return warrantyExpiryDate;
    }

    public void setWarrantyExpiryDate(LocalDate warrantyExpiryDate) {
        this.warrantyExpiryDate = warrantyExpiryDate;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return String.format("""
                Serial Number: %s
                Category: %s
                Model Number: %s
                Model Name: %s
                Manufacture Date: %s
                Warranty Expiry Date: %s
                """,
                serialNumber, category, modelNumber, modelName, manufactureDate, warrantyExpiryDate);
    }
}