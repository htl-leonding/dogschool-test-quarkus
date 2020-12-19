package at.htl.dogschool.boundary;

import at.htl.dogschool.control.DbTest;
import com.intuit.karate.junit5.Karate;
import io.restassured.RestAssured;
import org.apache.derby.jdbc.ClientDataSource;
import org.assertj.db.type.Request;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.json.Json;
import javax.json.JsonObject;
import javax.sql.DataSource;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.output.Outputs.output;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class RestTest {

    private static final String DATABASE = "db";
    private static final String USERNAME = "app";
    private static final String PASSWORD = "app";
    private final DataSource ds = getDatasource();

    /**
     * By default REST assured assumes host localhost and port 8080 when doing a request
     */

    @BeforeAll
    private static void init() {
        RestAssured.basePath = "/api";
    }

    @Test
    void t010_listAllCourseTypes() {
        given()
        .when()
            .get("/course_type")
        .then()
            .assertThat()
            .statusCode(200)
            .contentType("application/json")
            .body("name", hasItems("Welpenkurs", "Begleithunde1", "Begleithunde2"))
            .body("id", hasItems(1,2,3))
            .body("abbr", hasItems("w", "bg1", "bg2"))
            .log().body();  // Ausgabe am Bildschirm

    }


    @Test
    void t020_postNewCourseTypeSchutzhundeausbildung() {
        // given
        final JsonObject schutzhundeausbildung = Json.createObjectBuilder()
                .add("abbr", "schutz")
                .add("name", "Schutzhunde-Ausbildung")
                .build();
        System.out.println(schutzhundeausbildung.toString());

        // when
        given()
                .header("Content-Type","application/json")
                .body(schutzhundeausbildung.toString())
        .when()
                .post("/course_type")
        .then()
                .statusCode(anyOf(is(200),is(201)))
                .log().headers()  // Ausgabe am Bildschirm
                .assertThat().header("Location", notNullValue());

        // then
        Table courseTypeTable = new Table(ds, DbTest.coursetypeTable);
        org.assertj.db.api.Assertions.assertThat(courseTypeTable).row(3)
                .value().isGreaterThan(3)
                .value().isEqualTo("schutz")
                .value().isEqualTo("Schutzhunde-Ausbildung");
    }

    @Test
    void t030_putUpdateCourseTypeSchutzhundeausbildungToSuchungeausbildung() {
        // given
        final JsonObject suchhundeausbildung = Json.createObjectBuilder()
                .add("abbr", "such")
                .add("name", "Suchhunde-Ausbildung")
                .build();
        System.out.println(suchhundeausbildung.toString());

        // What is the actual id?
        Request schutzhundeausbildung = new Request(ds, "select id, abbr, name from " + DbTest.coursetypeTable + " where abbr='schutz'");
        output(schutzhundeausbildung).toConsole();
        int id = Long.valueOf(schutzhundeausbildung
                        .getRow(0)
                        .getColumnValue("id")
                        .getValue()
                        .toString()
        ).intValue();
        System.out.println("id: = " + id);

        // when
        given()
            .header("Content-Type", "application/json")
            .body(suchhundeausbildung.toString())
        .when()
            .put("/course_type/" + id)
        .then()
            .statusCode(200)
            .assertThat()
            .body("abbr", equalTo("such"))
            .body("name", equalTo("Suchhunde-Ausbildung"))
            .body("id", equalTo(id));

        // then
        Table courseTypeTable = new Table(ds, DbTest.coursetypeTable);
        org.assertj.db.api.Assertions.assertThat(courseTypeTable).row(3)
                .value().isEqualTo(id)
                .value().isEqualTo("such")
                .value().isEqualTo("Suchhunde-Ausbildung");
        assertThat(courseTypeTable.getRowsList().size()).isEqualTo(4);
    }

    @Test
    void t040_putUpdateCourseTypeFailWithBadRequest() {
        // given
        final JsonObject suchhundeausbildung = Json.createObjectBuilder()
                .add("abbr", "xxxx")
                .add("name", "xxxx-Ausbildung")
                .build();
        System.out.println(suchhundeausbildung.toString());

        int id = 10000;
        System.out.println("id: = " + id);

        // when
        given()
                .header("Content-Type","application/json")
                .body(suchhundeausbildung.toString())
        .when()
                .put("/course_type/" + id)
        .then()
                .statusCode(400)
                .assertThat()
                .header("Reason",equalTo("courseType with id " + id + " does not exist"));

        // then
        Table courseTypeTable = new Table(ds, DbTest.coursetypeTable);
        // In der DB-TAbelle darf sich nichts ändern
        assertThat(courseTypeTable.getRowsList().size()).isEqualTo(4);
    }

    @Test
    void t050_deleteCourseTypeSuchhundeausbildung() {
        // given

        // What is the actual id of the Suchhunde-ausbildung?
        final String sql = "select id, abbr, name from " + DbTest.coursetypeTable + " where abbr='such'";
        Request suchhundeausbildung = new Request(ds, sql);
        output(suchhundeausbildung).toConsole();
        int id = Long.valueOf(suchhundeausbildung
                        .getRow(0)
                        .getColumnValue("id")
                        .getValue()
                        .toString()
        ).intValue();
        System.out.println("id: = " + id);

        // when
        given()
        .when()
                .delete("/course_type/" + id)
        .then()
                .statusCode(anyOf(is(200),is(204))); // sollte eigentlich 204 NO CONTENT sein

        // then
        Table courseTypeTable = new Table(ds, DbTest.coursetypeTable);
        output(courseTypeTable).toConsole();
        assertThat(courseTypeTable.getRowsList().size()).isEqualTo(3);
    }


//    @Test
//    void t060_createBooking() {
//        // given
//        final JsonObject booking = Json.createObjectBuilder()
//                .add("bookingdate", "2020-03-07")
//                .add("price", 45.0)
//                .add("course", 1)
//                .add("dog", 2)
//                .build();
//        System.out.println(booking.toString());
//
//        // when
//        given()
//                .header("Content-Type","application/json")
//                .body(booking.toString())
//        .when()
//                .post("/booking")
//        .then()
//                .statusCode(anyOf(is(200),is(201)))
//                .log().headers()  // Ausgabe am Bildschirm
//                .assertThat().header("Location", notNullValue());
//
//        // then
//        Table table = new Table(ds, DbTest.bookingTable);
//        org.assertj.db.api.Assertions.assertThat(table).row(3)
//                .value().isGreaterThan(0);
//
//    }


    @Karate.Test
    Karate t200_createBooking() {
        return Karate.run("booking-create").relativeTo(getClass());
    }


    private static DataSource getDatasource() {
        ClientDataSource dataSource = new ClientDataSource();
        dataSource.setServerName("localhost");   // ist default Wert
        dataSource.setPortNumber(1527);   // ist default Wert
        dataSource.setDatabaseName(DATABASE);
        dataSource.setUser(USERNAME);
        dataSource.setPassword(PASSWORD);
        return dataSource;
    }

}
