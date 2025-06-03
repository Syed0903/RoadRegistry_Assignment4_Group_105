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
    //testing to see if valid input  returns successfully
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

    void addDemeritPoints_Under21Suspension_UpdatesStatus() {
        Person p = new Person("56s@d#&RT", "Tom", "Smith",
                "45|Park St|Melbourne|Victoria|Australia", "01-01-2007");
        p.addPerson();
        p.addDemeritPoints("01-01-2024", 3);
        p.addDemeritPoints("01-02-2024", 4); // Total = 7 (>6)
        assertTrue(p.isSuspended());
    }

}
