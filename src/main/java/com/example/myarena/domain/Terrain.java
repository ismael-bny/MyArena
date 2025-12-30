package com.example.myarena.domain;

public class Terrain {

    private Long id;
    private String name;
    private TerrainType type;
    private String description;
    private String location;
    private double pricePerHour;
    private int capacity;
    private boolean available;
    private Long ownerId; // User.id (OWNER)

    public Terrain() {
    }

    public Terrain(Long id, String name, TerrainType type, String description, String location, double pricePerHour, int capacity, boolean available, Long ownerId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.location = location;
        this.pricePerHour = pricePerHour;
        this.capacity = capacity;
        this.available = available;
        this.ownerId = ownerId;
    }

    // Pour la création (sans id)
    public Terrain(String name,
                   TerrainType type,
                   String description,
                   String location,
                   double pricePerHour,
                   int capacity,
                   boolean available,
                   Long ownerId) {
        this(null, name, type, description, location, pricePerHour, capacity, available, ownerId);
    }

    // ===== Getters =====
    public Long getId() { return id; }
    public String getName() { return name; }
    public TerrainType getType() { return type; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public double getPricePerHour() { return pricePerHour; }
    public int getCapacity() { return capacity; }
    public boolean isAvailable() { return available; }
    public Long getOwnerId() { return ownerId; }

    // ===== Setters =====
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(TerrainType type) { this.type = type; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setPricePerHour(double pricePerHour) { this.pricePerHour = pricePerHour; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    @Override
    public String toString() {
        return "Terrain{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", pricePerHour=" + pricePerHour +
                ", capacity=" + capacity +
                ", available=" + available +
                ", ownerId=" + ownerId +
                '}';
    }
}
