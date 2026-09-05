# Driving School Management System

A JSF and Hibernate web application for managing student enrolment and
practical lesson scheduling at a driving school.

Assignment 3, Project Phase 1 - Web Technologies, Adventist University of
Central Africa.

## What it does

Driving schools schedule practical lessons in a paper diary. Because every
lesson needs a student, an instructor and a vehicle free at the same time, the
same instructor or car regularly gets promised to two students at once, and
nobody finds out until both turn up. This system refuses the clash at the
moment of booking instead.

## Technology

| Layer | Technology |
|---|---|
| View | JSF 2.3 (Mojarra 2.3.9), Facelets, XHTML |
| Beans | CDI (Weld 3.1.9), `@Named` + `@ViewScoped` |
| Persistence | Hibernate ORM 5.6.15 |
| Database | PostgreSQL |
| Build | Maven, packaged as a WAR |
| Server | Apache Tomcat 9 |

## Entities

Six entities are mapped. Two of them, **Student** and **Lesson**, have complete
create, read, update and delete screens as required by the assignment.

| Entity | Mapped | CRUD screen |
|---|---|---|
| Student | yes | full CRUD |
| Lesson | yes | full CRUD |
| Instructor | yes | create and read |
| Vehicle | yes | create and read |
| Payment | yes | none in this phase |
| MockExam | yes | none in this phase |

## The three types of validation

The assignment requires all three. Each one is used where it is genuinely the
right tool, not just to tick a box.

**1. Built-in JSF validators** - declared in the XHTML, no Java written.
`required`, `f:validateLength` on the student ID, and `f:validateRegex` on
phone numbers, email addresses and number plates.
See `students.xhtml` and `setup.xhtml`.

**2. Custom validator** - `LegalAgeValidator`, registered as
`legalAgeValidator`. Rejects a date of birth that would make the student
younger than 18, and rejects future dates. No built-in tag can express this
because it is a calculation across two dates.
See `validator/LegalAgeValidator.java`.

**3. Application level validation** - two rules inside `LessonBean.save()`.
The first checks that the end time is later than the start time, which a
validator cannot do because a validator only ever sees one field. The second
is the double-booking check, which cannot be a validator because answering it
requires querying every other lesson in the database.
See `bean/LessonBean.java` and `dao/LessonDao.findConflicts()`.

## The three types of CSS

**External** - `src/main/webapp/resources/css/style.css`, linked from every
page with `<h:outputStylesheet library="css" name="style.css"/>`.

**Internal** - a `<style>` block in the head of `lessons.xhtml`, holding rules
that only the scheduling page needs.

**Inline** - `style="..."` attributes on individual elements, used for one-off
adjustments in `index.xhtml`, `students.xhtml` and `setup.xhtml`.

## Setting up

### 1. Create the database

```sql
CREATE DATABASE driving_school;
```

Hibernate creates the tables itself on first startup, because
`hibernate.hbm2ddl.auto` is set to `update` in `hibernate.cfg.xml`.

### 2. Set your database credentials

Edit `src/main/resources/hibernate.cfg.xml` and change the username and
password to match your local PostgreSQL installation:

```xml
<property name="hibernate.connection.username">postgres</property>
<property name="hibernate.connection.password">postgres</property>
```

### 3. Build

```bash
mvn clean package
```

This produces `target/DrivingSchoolManagementSystem.war`.

### 4. Deploy

Copy the WAR into Tomcat's `webapps` folder, or run the project directly from
IntelliJ IDEA with a Tomcat run configuration. Then open:

```
http://localhost:8080/DrivingSchoolManagementSystem/
```

## Demonstration script

Follow these steps in order. They exercise every feature the assignment asks
for, in the order that makes the video easiest to narrate.

1. **Instructors and vehicles** - register instructor `INS01` and vehicle
   `RAB123C`. Try the plate `ABC123` first to see the regex validator reject it.

2. **Students** - enrol `STD001`. Before submitting a valid one, try:
   - a five character ID, to trigger `f:validateLength`
   - the phone number `0123456789`, to trigger `f:validateRegex`
   - a date of birth from 2015, to trigger the custom `LegalAgeValidator`

3. **Lesson schedule** - book a lesson for `STD001` with `INS01` in `RAB123C`
   on any date, 09:00 to 10:00. It saves.

4. **Show the clash check** - enrol a second student `STD002`, then try to book
   them with the same instructor on the same date from 09:30 to 10:30. The
   booking is refused and the message names the instructor as the reason.

5. **Show that back-to-back is allowed** - book `STD002` from 10:00 to 11:00
   instead. It saves, proving the overlap test uses strict inequalities.

6. **Show update and delete** - edit a lesson to a new time, then delete it.
   Edit a student, then delete one.

## Project structure

```
src/main/java/rw/ac/auca/drivingschool/
    model/       entities: Audit, Student, Instructor, Vehicle,
                 Lesson, Payment, MockExam
    dao/         HibernateUtil and one DAO per entity
    bean/        StudentBean, LessonBean, SetupBean
    util/        LocalDate, LocalTime and entity converters
    validator/   LegalAgeValidator
src/main/resources/
    hibernate.cfg.xml
src/main/webapp/
    index.xhtml, students.xhtml, lessons.xhtml, setup.xhtml
    resources/css/style.css
    WEB-INF/web.xml, beans.xml, faces-config.xml
docs/
    DOCUMENTATION.md
```

## Links

- GitHub repository: _add your public repository URL here_
- Video walkthrough: _add your Google Vid link here_

## Author

_Your name_, _your student number_
