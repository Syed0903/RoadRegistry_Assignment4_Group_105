package au.edu.rmit.sct.roadregistry;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class PersonTest {

    @BeforeEach
    void resetFiles() throws IOException {
        Files.write(Paths.get("persons.txt"), new byte[0]);   // clear file
        Files.write(Paths.get("offenses.txt"), new byte[0]);  // clear file
    }
    // test to see if the addPerson() returns true when given valid inputs
    @Test
    void addPerson_ValidDetails_ReturnsTrue() {
        Person p = new Person("23@#$abCD", "John", "Doe",
                "10|Main St|Melbourne|Victoria|Australia", "15-05-1980");
        assertTrue(p.addPerson());
    }
    // test to see if addPerson() returns false when given an invalid ID format
    @Test
    void addPerson_InvalidIDFormat_ReturnsFalse() {
        // First digit '1' is invalid
        Person p = new Person("12!@#abCD", "Jane", "Doe",
                "20|Park Ave|Melbourne|Victoria|Australia", "01-01-2000");
        assertFalse(p.addPerson());
    }
    // test to see if addPerson() returns false when the address state is not Victoria
    @Test
    void addPerson_AddressNotVictoria_ReturnsFalse() {
        Person p = new Person("56#@$!ABX", "Mike", "Smith",
                "100|King St|Sydney|NSW|Australia", "20-05-1985");
        assertFalse(p.addPerson());
    }
    // test to see if addPerson() returns false when the birthdate format is invalid
    @Test
    void addPerson_InvalidBirthdate_ReturnsFalse() {
        Person p = new Person("78$%^&CDE", "Ella", "Brown",
                "12|Queen St|Melbourne|Victoria|Australia", "1990-11-15");
        assertFalse(p.addPerson());
    }
    // test to see if addPerson() returns false when ID has insufficient special characters in positions 3-8
    @Test
    void addPerson_InsufficientSpecialChars_ReturnsFalse() {
        // Only 1 special char (@) in positions 3-8
        Person p = new Person("34a@bcDEF", "Alex", "Taylor",
                "50|Main St|Melbourne|Victoria|Australia", "01-01-2000");
        assertFalse(p.addPerson());
    }
    // test to see if updatePersonalDetails() returns true when provided with valid updated details
    @Test
    void updateDetails_ValidUpdate_ReturnsTrue() {
        Person p = new Person("57a$#c&DXY", "Sam", "Lee",
                "10|George St|Melbourne|Victoria|Australia", "01-01-1990");
        p.addPerson();
        assertTrue(p.updatePersonalDetails("57a$#c&DXY", "Samuel", "Lee",
                "10|George St|Melbourne|Victoria|Australia", "01-01-1990"));
    }
    // test to see if updatePersonalDetails() returns false when a person under 18 attempts to change their address
    @Test
    void updateDetails_Under18AddressChange_ReturnsFalse() {
        // Age: 15 years (if current year is 2025)
        Person p = new Person("59a#@d$XYQ", "Ali", "Zed",
                "12|King St|Melbourne|Victoria|Australia", "01-01-2010");
        p.addPerson();
        assertFalse(p.updatePersonalDetails("59a#@d$XYQ", "Ali", "Zed",
                "99|Fake St|Melbourne|Victoria|Australia", "01-01-2010"));
    }
    // test to see if updatePersonalDetails() returns false when birthdate is changed along with the other fields
    @Test
    void updateDetails_BirthdayChangeWithOtherFields_ReturnsFalse() {
        Person p = new Person("58b%@h&JYU", "Sara", "Wong",
                "22|Market St|Melbourne|Victoria|Australia", "01-01-1995");
        p.addPerson();
        assertFalse(p.updatePersonalDetails("58b%@h&JYU", "Sarah", "Wong",
                "22|Market St|Melbourne|Victoria|Australia", "02-02-1995"));
    }

    @Test
    void addDemeritPoints_ValidInput_ReturnsSuccess() {
        Person p = new Person("58a#@d%XYQ", "Ali", "Zed",
                "12|King St|Melbourne|Victoria|Australia", "01-01-2000");
        p.addPerson();
        assertEquals("Success", p.addDemeritPoints("15-03-2024", 3));
    }
    //test to see if code fails upon wrong inputs
    @Test
    void addDemeritPoints_InvalidDateFormat_ReturnsFailed() {
        Person p = new Person("57b#@g$TRU", "Liam", "West",
                "10|George St|Melbourne|Victoria|Australia", "01-01-1990");
        p.addPerson();
        assertEquals("Failed", p.addDemeritPoints("2024-03-15", 3)); // YYYY-MM-DD format
    }
    @Test
    void addDemeritPoints_Under21Suspension_UpdatesStatus() {
        Person p = new Person("56s@d#&RT", "Tom", "Smith",
                "45|Park St|Melbourne|Victoria|Australia", "01-01-2007");
        p.addPerson();
        p.addDemeritPoints("01-01-2024", 3);
        p.addDemeritPoints("01-02-2024", 4); // Total = 7 (>6)
        assertTrue(p.isSuspended());
    }
    @Test 
    void addDemeritPoints_ExactlyTwoYearsAgo_CountsPoints() {
        Person p = new Person("89*&^cFG", "Lucy", "Chen",
                "33|Elm St|Melbourne|Victoria|Australia", "05-05-1985");
        assertTrue(p.addPerson());

        LocalDate offenseDate = LocalDate.now().minusYears(2).plusDays(1);  // ensures it's just inside 2 years

        p.addDemeritPoints(
                offenseDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                2
        );

        assertEquals(2, p.getDemeritPoints());
    }

}
