package nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SamenwerkingsverbandTest {

    @Test
    void testEquals() {
        Samenwerkingsverband s1 = new Samenwerkingsverband();
        Samenwerkingsverband s2 = new Samenwerkingsverband();

        assertEquals(s1, s2);
        LocalDate now = LocalDate.now();
        s1 = new Samenwerkingsverband(1, "name", now, now, "onderdeel");
        s2 = new Samenwerkingsverband(1, "name", now, now, "onderdeel");

        assertEquals(s1, s2);
    }

}