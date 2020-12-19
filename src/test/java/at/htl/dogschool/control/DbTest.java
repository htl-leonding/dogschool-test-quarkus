package at.htl.dogschool.control;

import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.db.api.Assertions.assertThat;
import static org.assertj.db.output.Outputs.output;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class DbTest {

    private static final String URL = "jdbc:derby://localhost:1527/db";
    private static final String USERNAME = "app";
    private static final String PASSWORD = "app";

    // tag::table-names[]
//    public static final String bookingTable = "BOOKING";
//    public static final String courseTable = "COURSE";
//    public static final String coursetypeTable = "COURSETYPE";
//    public static final String dogTable = "DOG";
//    public static final String personTable = "PERSON";

    public static final String bookingTable = "S_BOOKING";
    public static final String courseTable = "S_COURSE";
    public static final String coursetypeTable = "S_COURSE_TYPE";
    public static final String dogTable = "S_DOG";
    public static final String personTable = "S_PERSON";
    // end::table-names[]

    private static Source source;

    @BeforeAll
    private static void setup() {
        source = new Source(URL, USERNAME, PASSWORD);
    }

    @Test
    void test10_dbStateDogAfterInit() {
        Table dog = new Table(source, dogTable);
        output(dog).toConsole();
        assertThat(dog).hasNumberOfRows(5);
        assertThat(dog).column("OWNER_ID")
                .value().isEqualTo(1L)
                .value().isEqualTo(1L)
                .value().isEqualTo(2L)
                .value().isEqualTo(2L)
                .value().isEqualTo(2L);

    }

    @Test
    void test15_dbMetadata() {
        Table dog = new Table(source, dogTable);
        assertThat(dog).column("name").isText(true);
        //assertThat(dog).column("name").isDate(true);
    }

    @Test
    void test20_dbStatePersonAfterInit() {
        Table dog = new Table(source, personTable);
        output(dog).toConsole();
        assertThat(dog).hasNumberOfRows(2);
        assertThat(dog).column("FIRSTNAME")
                .value().isEqualTo("Matt")
                .value().isEqualTo("Mathilda");
        assertThat(dog).column("LASTNAME")
                .value().isEqualTo("Murdock")
                .value().isEqualTo("Lando");

    }

    @Test
    void test30_dbStateCourseAfterInit() {
        Table dog = new Table(source, courseTable);
        output(dog).toConsole();
        assertThat(dog).hasNumberOfRows(6);
        assertThat(dog).row(0)
                .value().isEqualTo(1)
                .value().isEqualTo("BG1 - Frühlingskurs")
                .value().isEqualTo(8)
                .value().isEqualTo(80)
                .value().isEqualTo("2020-03-07T10:00")
                .value().isEqualTo(2);
        assertThat(dog).row(5)
                .value().isEqualTo(6)
                .value().isEqualTo("BG2 - Herbstkurs")
                .value().isEqualTo(8)
                .value().isEqualTo(85)
                .value().isEqualTo("2019-10-18T10:00")
                .value().isEqualTo(3);
    }

   @Test
    void test40_dbStateCourseTypeAfterInit() {
        Table dog = new Table(source, coursetypeTable);
        output(dog).toConsole();
        assertThat(dog).hasNumberOfRows(3);
        assertThat(dog).row(0)
                .value().isEqualTo(1)
                .value().isEqualTo("w")
                .value().isEqualTo("Welpenkurs");
        assertThat(dog).row(1)
                .value().isEqualTo(2)
                .value().isEqualTo("bg1")
                .value().isEqualTo("Begleithunde1");
        assertThat(dog).row(2)
                .value().isEqualTo(3)
                .value().isEqualTo("bg2")
                .value().isEqualTo("Begleithunde2");
    }

}
