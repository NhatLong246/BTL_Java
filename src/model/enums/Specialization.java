package model.enums;

import java.util.HashMap;
import java.util.Map;

public enum Specialization {
    CARDIOLOGY("SPC-001", "Tim mạch"),
    GENERAL("SPC-002", "Nội tổng quát"),
    PEDIATRICS("SPC-003", "Nhi khoa"),
    GYNECOLOGY("SPC-004", "Sản phụ khoa"),
    SURGERY("SPC-005", "Ngoại khoa");

    private final String id;
    private final String name;
    private static final Map<String, Specialization> idToSpecialization = new HashMap<>();

    static {
        for (Specialization spec : values()) {
            idToSpecialization.put(spec.getId(), spec);
        }
    }

    Specialization(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Specialization fromId(String id) {
        return idToSpecialization.getOrDefault(id, GENERAL);
    }
}
