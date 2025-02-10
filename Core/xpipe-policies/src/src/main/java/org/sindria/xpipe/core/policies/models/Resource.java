package org.sindria.xpipe.core.policies.models;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="resources")
public class Resource {

    @Id
    @GeneratedValue
    private Long id;

    @Column()
    private String name;

    @Column()
    private String product;

    @Column()
    private String service;

    @Column()
    private String region;

    @Column()
    private Long accountId;

    @Column()
    private String resourceType;

    @Column()
    private String resourceId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }


    public Resource() {}

    /**
     * Resource constructor
     */
    public Resource(Long id, String name, String product, String service, String region, Long accountId, String resourceType, String resourceId) {
        super();
        this.id = id;
        this.name = name;
        this.product = product;
        this.service = service;
        this.region = region;
        this.accountId = accountId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
}
