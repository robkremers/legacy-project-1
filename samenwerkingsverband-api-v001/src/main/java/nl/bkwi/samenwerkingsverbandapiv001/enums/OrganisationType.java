package nl.bkwi.samenwerkingsverbandapiv001.enums;

public enum OrganisationType {
    GEMEENTE("GEMEENTE"),
    SAMENWERKINGSVERBAND("SAMENWERKINGSVERBAND");

    public final String type;

    private OrganisationType(String type) {
        this.type = type;
    }
}
