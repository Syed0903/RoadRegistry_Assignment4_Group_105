package au.edu.rmit.sct.roadregistry;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Person {
 //TODO: implement the Person Class
// TODO: Implement addPersonackage au.edu.rmit.sct.roadregistry;
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
    private int demeritPoints;
    private boolean isSuspended;

    public Person(String personID, String firstName, String lastName, String address, String birthdate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthdate = birthdate;
    }

    public boolean addPerson() {
        //TODO: This method adds information about a person to a TXT file.
        if (!isValidPersonID(personID) || !isValidAddress(address) || !isValidBirthdate(birthdate)) {
            return false;
        }

        try (FileWriter writer = new FileWriter("persons.txt", true)) {
            writer.write(String.join(",", personID, firstName, lastName, address, birthdate,
                    String.valueOf(demeritPoints), String.valueOf(isSuspended)) + "\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private boolean isValidPersonID(String id) { 
        // Exactly 10 characters: 2 digits (2–9), at least 2 special characters (3-8), 2 uppercase letters
        String pattern = "^[2-9][0-9].{1,6}[A-Z]{2}$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(id);

        // Count special characters between positions 3 and 8
        int specialCount = 0;
        for (int i = 2; i < 8 && i < id.length(); i++) {
            if (!Character.isLetterOrDigit(id.charAt(i))) {
                specialCount++;
            }
        }
        return matcher.matches() && specialCount >= 2;
    }

    private boolean isValidAddress(String addr) {
        // Format: StreetNumber|Street|City|State|Country
        String[] parts = addr.split("\\|");
        return parts.length == 5 && parts[3].equalsIgnoreCase("Victoria");
    }

    private boolean isValidBirthdate(String date) {
        return date.matches("^\\d{2}-\\d{2}-\\d{4}$");
    } 
 // TODO: Implement updatePersonalDetails 
// TODO: Implement addDemeritPoints 

 
}


