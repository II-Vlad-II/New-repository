package com.vlad.healthbeauty.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Supplier name is required")
    @Size(max = 200, message = "Supplier name must be at most 200 characters")
    @Column(nullable = false, length = 200)
    private String name;

    @Size(max = 100, message = "Contact name must be at most 100 characters")
    @Column(name = "contact_name", length = 100)
    private String contactName;

    @Email(message = "Email format is invalid")
    @Size(max = 100, message = "Email must be at most 100 characters")
    @Column(length = 100)
    private String email;

    @Size(max = 50, message = "Phone must be at most 50 characters")
    @Column(length = 50)
    private String phone;

    @JsonIgnore
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private List<Product> products;

    public Supplier() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}
