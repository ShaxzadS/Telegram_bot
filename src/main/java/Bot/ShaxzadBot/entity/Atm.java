package Bot.ShaxzadBot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "atm")
public class Atm {

    @Id
    @Column(name = "atm_number", nullable = false, length = 32)
    private String number;

    @Column(name = "model", nullable = false, length = 255)
    private String model;

    @Column(name = "organization", nullable = false, length = 255)
    private String organization;

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Column(name = "sector", nullable = false, length = 255)
    private String sector;

    protected Atm() {
    }

    public Atm(String number, String model, String organization, String address, String sector) {
        this.number = number;
        this.model = model;
        this.organization = organization;
        this.address = address;
        this.sector = sector;
    }

    public String getNumber() {
        return number;
    }

    public String getModel() {
        return model;
    }

    public String getOrganization() {
        return organization;
    }

    public String getAddress() {
        return address;
    }

    public String getSector() {
        return sector;
    }
}
