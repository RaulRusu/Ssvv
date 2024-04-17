package ssvv.example;

import org.example.domain.Nota;
import org.example.domain.Student;
import org.example.domain.Tema;
import org.junit.jupiter.api.*;
import org.example.repository.NotaXMLRepo;
import org.example.repository.StudentXMLRepo;
import org.example.repository.TemaXMLRepo;
import org.example.service.Service;
import org.example.validation.NotaValidator;
import org.example.validation.StudentValidator;
import org.example.validation.TemaValidator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class IncrementalIntegrationTest {

    private TemaXMLRepo assignmentRepo;
    private StudentXMLRepo studentRepo;
    private NotaXMLRepo gradesRepo;

    private Service service;

    @BeforeAll
    static void createXML() {
        File xml = new File("fisiere/test-teme.xml");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(xml))) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                    "<inbox>\n" +
                    "\n" +
                    "</inbox>");
            writer.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        File xml2 = new File("fisiere/test-studenti.xml");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(xml2))) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                    "<inbox>\n" +
                    "\n" +
                    "</inbox>");
            writer.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        File xml3 = new File("fisiere/test-note.xml");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(xml3))) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                    "<inbox>\n" +
                    "\n" +
                    "</inbox>");
            writer.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void setup() {
        this.assignmentRepo = new TemaXMLRepo("fisiere/test-teme.xml");
        this.studentRepo = new StudentXMLRepo("fisiere/test-studenti.xml");
        this.gradesRepo = new NotaXMLRepo("fisiere/test-note.xml");

        this.service = new Service(
                this.studentRepo,
                new StudentValidator(),
                this.assignmentRepo,
                new TemaValidator(),
                this.gradesRepo,
                new NotaValidator(this.studentRepo, this.assignmentRepo)
        );
    }

    @AfterAll
    static void removeXML() {
        new File("fisiere/test-studenti.xml").delete();
        new File("fisiere/test-note.xml").delete();
        new File("fisiere/test-teme.xml").delete();
    }


    @Test
    public void testAddStudent() {
        Student student = new Student("test", "test", 1, "test@test.com");

        try {
            service.addStudent(student);

            Student newStudent = this.studentRepo.findOne("test");

            assertEquals(student, newStudent);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddStudentAddAssignment() {
        Student student = new Student("1", "a", 1, "test@test.com");
        Tema tema = new Tema("1", "testDeesc", 2, 1);

        try {
            service.addStudent(student);
            service.addTema(tema);

            Student newStudent = this.studentRepo.findOne("1");
            Tema temaFromRepo = this.assignmentRepo.findOne("1");

            assertEquals(tema, temaFromRepo);
            assertEquals(student, newStudent);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void testIncrementalFinal() {
        Student student = new Student("2", "a", 1, "test@test.com");
        Tema tema = new Tema("2", "testdesc", 8, 1);
        Nota nota = new Nota("1", "2", "2", 10, LocalDate.now());

        try {
            service.addStudent(student);
            service.addTema(tema);
            service.addNota(nota, "msg");

            Student newStudent = this.studentRepo.findOne("2");
            Tema newTema = this.assignmentRepo.findOne("2");
            Nota newNota = this.gradesRepo.findOne("1");

            assertEquals(student, newStudent);
            assertEquals(tema, newTema);
            assertEquals(nota, newNota);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testIncrementalFinalError() {
        Student student = new Student("3", "a", 1, "test@test.com");
        Tema tema = new Tema("3", "testdesc", 4, 1);
        Nota nota = new Nota("2", "3", "3", 10, LocalDate.now());

        try {
            service.addStudent(student);
            service.addTema(tema);
            service.addNota(nota, "msg");

            Student newStudent = this.studentRepo.findOne("2");
            Tema newTema = this.assignmentRepo.findOne("2");
            Nota newNota = this.gradesRepo.findOne("1");
            fail();
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Studentul nu mai poate preda aceasta tema!");
        }
    }
}