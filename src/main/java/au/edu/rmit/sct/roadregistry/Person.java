package au.edu.rmit.sct.roadregistry;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Triggering C from video demo

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

     public boolean updatePersonalDetails(String newID, String newFirstName, String newLastName, String newAddress, String newBirthdate) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("persons.txt"));
            List<String> updatedLines = new ArrayList<>();
            boolean updated = false;

            for (String line : lines) {
                String[] parts = line.split(",", -1);
                if (parts.length < 7) continue;

                if (parts[0].equals(this.personID)) {
                    // Preserve unchanged fields
                    int oldAge = getAge(this.birthdate);
                    int newAge = getAge(newBirthdate);

                    // Rule 1: Address can only change if the person is 18 or older
                    if (oldAge < 18 && !this.address.equals(newAddress)) return false;

                    // Rule 2: if birthdate changes, ID and other details must remain the same
                    boolean birthdayChanged = !this.birthdate.equals(newBirthdate);
                    boolean otherChanged = !this.personID.equals(newID) || !this.firstName.equals(newFirstName)
                            || !this.lastName.equals(newLastName) || !this.address.equals(newAddress);
                    if (birthdayChanged && otherChanged) return false;

                    // Rule 3: ID cannot change if the first digit is even and not the same as the new ID
                    char firstDigit = this.personID.charAt(0);
                    if (Character.isDigit(firstDigit) && (firstDigit - '0') % 2 == 0 && !this.personID.equals(newID)) {
                        return false;
                    }

                    // details validation
                    if (!isValidPersonID(newID) || !isValidAddress(newAddress) || !isValidBirthdate(newBirthdate)) {
                        return false;
                    }

                    // Update local object
                    this.personID = newID;
                    this.firstName = newFirstName;
                    this.lastName = newLastName;
                    this.address = newAddress;
                    this.birthdate = newBirthdate;

                    // Rebuild the line
                    String updatedLine = String.join(",", newID, newFirstName, newLastName, newAddress, newBirthdate, parts[5], parts[6]);
                    updatedLines.add(updatedLine);
                    updated = true;
                } else {
                    updatedLines.add(line); // no change
                }
            }

            if (updated) {
                Files.write(Paths.get("persons.txt"), updatedLines);
            }

            return updated;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getAge(String dob) {
        try {
            LocalDate birth = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return Period.between(birth, LocalDate.now()).getYears();
        } catch (DateTimeParseException e) {
            return -1; // invalid
        }
    }
// TODO: Implement addDemeritPoints 
 public String addDemeritPoints(String offenseDate, int points){
    if(!offenseDate.matches("^\\d{2}-\\d{2}-\\d{4}$") || points < 1 || points > 6){
     return "Failed";
 }
  try {
      // appending the offense to offenses.txt
      try (FileWriter writer = new FileWriter("offenses.txt", true)) {
      writer.write(String.join(",", this.personID, offenseDate, String.valueOf(points)) + "\n");
            }
  //calculating total demerit points in the last 2 years
      List<String> lines = Files.readAllLines(Paths.get("offenses.txt"));
      int total = 0;
      LocalDate now = LocalDate.now();
      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

      for (String line : lines) {
                String[] parts = line.split(",", -1);
                if (parts.length != 3) continue;
                if (!parts[0].equals(this.personID)) continue;

                LocalDate offense = LocalDate.parse(parts[1], fmt);
                // REPLACE the Period-based check with:
                LocalDate twoYearsAgo = LocalDate.now().minusYears(2);
                if (!offense.isBefore(twoYearsAgo)) {
                    total += Integer.parseInt(parts[2]);
             }
       }
      this.demeritPoints = total;

            int age = getAge(this.birthdate);
            if ((age < 21 && total > 6) || (age >= 21 && total > 12)) {
                this.isSuspended = true;
            }

            // updates suspension status in persons.txt
            updateSuspensionStatus();

            return "Success";

        } catch (IOException | DateTimeParseException | NumberFormatException e) {
            e.printStackTrace();
            return "Failed";
        }
    }
public boolean isSuspended() {
        return this.isSuspended;
    }
 public int getDemeritPoints() {
        return this.demeritPoints;
    }

    private void updateSuspensionStatus() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("persons.txt"));
        List<String> updated = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(",", -1);
            if (parts.length < 7) continue;

            if (parts[0].equals(this.personID)) {
                // Rebuild updated line
                String updatedLine = String.join(",", personID, firstName, lastName, address, birthdate,
                        String.valueOf(demeritPoints), String.valueOf(isSuspended));
                updated.add(updatedLine);
            } else {
                updated.add(line);
            }
        }

        Files.write(Paths.get("persons.txt"), updated);
    }
// Triggering CI test
 
}


