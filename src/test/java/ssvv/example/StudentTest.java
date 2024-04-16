package ssvv.example;

import org.example.domain.Student;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.example.repository.StudentXMLRepo;
import org.example.service.Service;
import org.example.validation.StudentValidator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for simple App.
 */
public class StudentTest
{
    private StudentXMLRepo xmlRepo;
    private Service service;

    @BeforeAll
    static void createXML() {
        // Create a new xml file
        try {
            File xmlFile = new File("fisiere/test-Studenti.xml");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(xmlFile))) {
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                        "<inbox>\n" +
                        "\n" +
                        "</inbox>");
                writer.flush();
            }
        } catch (Exception e) {
            fail();
        }


    }

    @AfterAll
    static void deleteXML() {
        // Delete the xml file
        try {
            File xmlFile = new File("fisiere/test-Studenti.xml");
            xmlFile.delete();
        } catch (Exception e) {
            fail();
        }
    }

    @BeforeEach
    public void setUp() {
        xmlRepo = new StudentXMLRepo("fisiere/test-Studenti.xml");
        this.service = new Service(
                xmlRepo,
                new StudentValidator(),
                null,
                null,
                null,
                null);
    }

    @org.junit.jupiter.api.Test
    public void testAddStudent_Success()
    {
        Student student = new Student("111", "test", 937, "t@t.com");

        try {
            service.addStudent(student);

            // Get the student from the repository
            Student newStudent = xmlRepo.findOne("111");

            assertEquals(student, newStudent);
        } catch (Exception e) {
            fail();
        }

        service.deleteStudent("111");
    }

    @org.junit.jupiter.api.Test
    public void testAddStudent_NullId()
    {
        Student student = new Student(null, "test", 937, "t@t.com");

        try {
            service.addStudent(student);
            fail();
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Id incorect!");
        }
    }

    @org.junit.jupiter.api.Test
    public void testAddStudent_EmptyName()
    {
        Student student = new Student("111", "", 937, "t@t.com");

        try {
            service.addStudent(student);
            fail();
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Nume incorect!");
        }
    }

    @org.junit.jupiter.api.Test
    public void testAddStudent_EmptyEmail()
    {
        Student student = new Student("111", "test", 937, "");

        try {
            service.addStudent(student);
            fail();
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Email incorect!");
        }
    }

    @org.junit.jupiter.api.Test
    public void testAddStudent_NegativeGroup()
    {
        Student student = new Student("111", "test", -1, "t@t.com");

        try {
            service.addStudent(student);
            fail();
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Grupa incorecta!");
        }
    }

    @org.junit.jupiter.api.Test
    public void testAddStudent_BoundaryValuesGroup_Error()
    {
        Student student = new Student("111", "test", -1, "t@t.com");

        try {
            service.addStudent(student);
            fail();
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Grupa incorecta!");
        }
    }

    @org.junit.jupiter.api.Test
    public void testAddStudent_BoundaryValuesGroup_Success()
    {
        Student student = new Student("111", "test", 0, "t@t.com");

        try {
            service.addStudent(student);

            // Get the student from the repository
            Student newStudent = xmlRepo.findOne("111");

            assertEquals(student, newStudent);
        } catch (Exception e) {
            fail();
        }

        service.deleteStudent("111");
    }
}